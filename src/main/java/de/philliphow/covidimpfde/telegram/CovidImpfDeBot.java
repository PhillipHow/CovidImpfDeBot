package de.philliphow.covidimpfde.telegram;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.pmw.tinylog.Logger;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import de.philliphow.covidimpfde.api.DeliveryApiManager;
import de.philliphow.covidimpfde.api.VaccinationsApiManager;
import de.philliphow.covidimpfde.api.models.DeliveryDataRow;
import de.philliphow.covidimpfde.api.models.VaccinationDataRow;
import de.philliphow.covidimpfde.exceptions.ImpfDashboardApiException;
import de.philliphow.covidimpfde.exceptions.SubPersistenceException;
import de.philliphow.covidimpfde.logic.DeliveryUpdateBuilder;
import de.philliphow.covidimpfde.logic.UpdateMessageBuilder;
import de.philliphow.covidimpfde.logic.VaccinationUpdateBuilder;
import de.philliphow.covidimpfde.services.SubListPersistence;
import de.philliphow.covidimpfde.telegram.commands.DeliveryCommand;
import de.philliphow.covidimpfde.telegram.commands.StartCommand;
import de.philliphow.covidimpfde.telegram.commands.SubscribeCommand;
import de.philliphow.covidimpfde.telegram.commands.UnsubscribeCommand;
import de.philliphow.covidimpfde.telegram.commands.VaccinationCommand;

/**
 * The Telegram bot instance and main entry point of the application. Its job is
 * to answer command queries and to check for new vaccination and delivery
 * data in regular intervals. 
 * 
 * Can be started in normal and debug mode. In debug
 * mode, the interval to check for new data is much shorter, and data is
 * acquired from two local mock files instead of the real impfdashboard.de.
 * If running from executable jar, the data needs to be put in a folder next
 * to the jar. See {@link DeliveryApiManager} and {@link VaccinationsApiManager}
 * for paths.
 * 
 * @author PhillipHow
 *
 */
public class CovidImpfDeBot extends TelegramLongPollingCommandBot {

	/**
	 * The interval in which the impfdashboard is queried for new data
	 */
	private static final int POLLING_INTERVALL_SECONDS = 60 * 30;
	/**
	 * The interval in which the local files are queried in debug mode
	 */
	private static final int POLLING_INTERVALL_DEBUG_MODE_SECONDS = 30;

	/**
	 * Telegram bot API token, used by superclass to run bot
	 */
	private final String botToken;
	/**
	 * Telegram bot user name, used by superclass to run bot
	 */
	private final String botUsername;
	/**
	 * If present, important updates and errors are send to this telegram chat Id
	 * for debugging purposes.
	 */
	private final Optional<String> adminChatId;
	/**
	 * If debugMode is true, two local test files are queried for new data, instead
	 * of impfdashboard.de. Used for development and testing. Do not turn on in
	 * production environment.
	 */
	private final boolean debugMode;

	/**
	 * 
	 * @param botToken    token to connect to Telegram bot API, obtain via BotFather
	 * @param botUsername Telegram user name of the bot
	 * @param adminChatId chatId to send important bot updates and error
	 *                    notifications to. Just pass empty optional if this
	 *                    behavior is not required
	 */
	public CovidImpfDeBot(String botToken, String botUsername, Optional<String> adminChatId) {
		this(botToken, botUsername, adminChatId, false);
	}

	/**
	 * 
	 * @param botToken    token to connect to Telegram bot API, obtain via BotFather
	 * @param botUsername Telegram user name of the bot
	 * @param adminChatId chatId to send important bot updates and error
	 *                    notifications to. Just pass empty optional if this
	 *                    behavior is not required
	 * @param debugMode   if set to true, local test files instead of the real
	 *                    impfdashboard will be queried. Use for debugging and
	 *                    testing, but not in production environment!
	 */
	public CovidImpfDeBot(String botToken, String botUsername, Optional<String> adminChatId, boolean debugMode) {
		super();
		this.botToken = botToken;
		this.botUsername = botUsername;
		this.adminChatId = adminChatId;
		this.debugMode = debugMode;
		this.registerCommands();
	}

	private void registerCommands() {
		register(new VaccinationCommand(this));
		register(new SubscribeCommand(this));
		register(new StartCommand(this));
		register(new UnsubscribeCommand(this));
		register(new DeliveryCommand(this));
	}

	/**
	 * Provides the ability to send important notifications about the bot to an
	 * telegram chatId. If this behavior has been disabled, the message will just be
	 * logged.
	 * 
	 * @param str that contains the message
	 */
	public void notifyAdminOnTelegram(String str) {

		Logger.info(str);

		if (this.adminChatId.isPresent()) {
			SendMessage message = new SendMessage();
			message.setChatId(this.adminChatId.get());
			message.setText(str);

			try {
				this.execute(message);
			} catch (Exception e) {
				Logger.error(e, "Error notifying telegram admin (id {})", this.adminChatId);
			}
		}
	}

	/**
	 * Initializes and starts the bot. Runs until the process is interrupted. Note
	 * that that the impfdashboard API is called synchronous once, hence bot
	 * creation fails if it is not reachable. Note furthermore that the downloaded
	 * data is parsed and interpreted only when the bot answers to a command, so
	 * this method will still return successfully even if there are breaking changes
	 * within the impfdashboard files.
	 * 
	 * @throws ImpfDashboardApiException if the impfdashboard api is not reachable
	 * @throws TelegramApiException      if the telegram API is not reachable
	 */
	public void startAndRunUntilInterrupted() throws ImpfDashboardApiException, TelegramApiException {

		if (this.adminChatId == null)
			Logger.info("Starting without adminChatId");
		else
			Logger.info("Starting with admin chat Id {} ", this.adminChatId);

		if (this.getDebugMode())
			Logger.warn("Starting in debug mode - data will be fetched from local test files!");
		else
			Logger.info("Starting in production mode - data will be fetched from impfdashboard.de");

		VaccinationsApiManager.getInstance(debugMode).getNewDataIfNecessary();
		DeliveryApiManager.getInstance(debugMode).getNewDataIfNecessary();
		Logger.info("Got initial vaccination data");

		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		botsApi.registerBot(this);

		this.notifyAdminOnTelegram(String.format("Bot is running (debugMode: %s)", debugMode));
		startImpfDashboardPolling();
	}

	/**
	 * After this method is called, the bot periodically calls the given vaccination
	 * and delivery files and notifies all subscribed chats with updates when new
	 * data is found in either of them.
	 */
	private void startImpfDashboardPolling() {

		final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

		executorService.scheduleAtFixedRate(() -> {

			try {
				// only send vaccination updates once a week for now
				if (checkForNewVaccinationData() && lastVaccinationUpdateWasOnSunday())
					sendVaccinationUpdateToAllSubs();
				
				if (checkForNewDeliveryData())
					sendDeliveryUpdateToAllSubs();

			} catch (ImpfDashboardApiException exception) {
				this.notifyAdminOnTelegram("Update could not be send, Impfdashboard is not reachable!");
			} catch (SubPersistenceException e) {
				this.notifyAdminOnTelegram("Update could not be send, SubPersistence not reachable!");
			}

		}, getPollingIntervallSeconds(), getPollingIntervallSeconds(), TimeUnit.SECONDS);

		Logger.info("Will check for new data every {} seconds", getPollingIntervallSeconds());

	}

	private boolean lastVaccinationUpdateWasOnSunday() {
		return (VaccinationsApiManager.getInstance(debugMode).getLastUpdateDate().getDayOfWeek().getValue() == 7);
	}

	private boolean checkForNewDeliveryData() throws ImpfDashboardApiException {
		return DeliveryApiManager.getInstance(debugMode).getNewDataIfNecessary();
	}

	private boolean checkForNewVaccinationData() throws ImpfDashboardApiException {
		return VaccinationsApiManager.getInstance(debugMode).getNewDataIfNecessary();
	}

	private void sendDeliveryUpdateToAllSubs() throws SubPersistenceException {

		UpdateMessageBuilder<DeliveryDataRow> updateBuilder = new DeliveryUpdateBuilder()
				.setContentData(DeliveryApiManager.getInstance(debugMode).getCurrentData())
				.setIsSubbed(true)
				.setSubCount(new SubListPersistence().getSubCount());

		this.sendUpdateToAllSubs(updateBuilder);
	}

	private void sendVaccinationUpdateToAllSubs() throws SubPersistenceException {

		UpdateMessageBuilder<VaccinationDataRow> updateBuilder = new VaccinationUpdateBuilder()
				.setContentData(VaccinationsApiManager.getInstance(debugMode).getCurrentData())
				.setIsSubbed(true)
				.setSubCount(new SubListPersistence().getSubCount());

		this.sendUpdateToAllSubs(updateBuilder);
	}

	/**
	 * Sends a provided update to all chats whose chatId is in the subscriptions
	 * file.
	 * 
	 * @param updateBuilder builder for the message. ChatId will be set by this
	 *                      method, so don't worry about that.
	 * @param <T> the type of data rows that is used for constructing the update
	 * @throws SubPersistenceException if the subscription file can not be read
	 */
	private <T> void sendUpdateToAllSubs(UpdateMessageBuilder<T> updateBuilder) throws SubPersistenceException {

		List<String> subbedChatIds = new SubListPersistence().getAllSubs();
		BulkMessageSender bulkMessageSender = new BulkMessageSender(subbedChatIds,
				(chatId) -> updateBuilder.setChatId(chatId).build(), this);

		bulkMessageSender.sendAllAsync((success, fail) -> notifyAdminOnTelegram("Update versendet! " + success.size() + " erfolgreich, " + fail.size() + " nicht erreichbar"));
	}

	@Override
	public String getBotToken() {
		return this.botToken;
	}

	@Override
	public String getBotUsername() {
		return this.botUsername;
	}

	@Override
	public void processNonCommandUpdate(Update update) {
		// UNUSED
	}

	public boolean getDebugMode() {
		return this.debugMode;
	}

	private int getPollingIntervallSeconds() {
		return getDebugMode() ? POLLING_INTERVALL_DEBUG_MODE_SECONDS : POLLING_INTERVALL_SECONDS;
	}

}
