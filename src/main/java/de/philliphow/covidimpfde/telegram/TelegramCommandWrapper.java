package de.philliphow.covidimpfde.telegram;

import org.pmw.tinylog.Logger;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Wrapper to simplify and abstract from the {@link BotCommand} API. The
 * {@code execute(AbsSender, User, Chat, String[])} API is replaced by
 * getAnswerForQuery(String, String[]), which just needs clients to provide the
 * answer to the query as {@link SendMessage} object. Clients do not need to 
 * worry about the sending logic. This class also contains a global error handler.
 * 
 * @author PhillipHow
 *
 */
public abstract class TelegramCommandWrapper extends BotCommand {

	private final CovidImpfDeBot bot;

	/**
	 * Constructs a {@link TelegramCommandWrapper}
	 * 
	 * @param commandIdentifier the command as String
	 * @param description       the description for Telegram
	 * @param bot               the {@link CovidImpfDeBot} instance
	 */
	public TelegramCommandWrapper(String commandIdentifier, String description, CovidImpfDeBot bot) {
		super(commandIdentifier, description);
		this.bot = bot;
	}

	/**
	 * Hook method to define the business logic for this command.
	 * 
	 * @param userId Telegram userId of the user that issued the command
	 * @param args   the command arguments
	 * @return an answer to the command query
	 */
	public abstract SendMessage getAnswerForQuery(String userId, String[] args);

	/**
	 * Satisfies the {@code execute()} interface of {@link BotCommand}. Also defines
	 * a global error handler, so that users still receive feedback even in case of
	 * runtime errors.
	 */
	@Override
	public final void execute(AbsSender absSender, User user, Chat chat, String[] args) {

		SendMessage answer;
		try {
			answer = getAnswerForQuery(user.getId().toString(), args);
		} catch (Exception exception) {
			// emergency handler to avoid giving no feedback to user
			Logger.error(exception, "error while getting command answer for {}", this.getCommandIdentifier());
			bot.notifyAdminOnTelegram(
					"There was a runtime error, a command answer could not be given. Check logs for more info, impfdashboard api might have changed. Command: "
							+ this.getCommandIdentifier());
			answer = ErrorSendMessage.unknownError(user.getId().toString());
		}

		try {
			absSender.execute(answer);
		} catch (TelegramApiException exception) {
			Logger.error(exception,
					"Error communicating with the telegram API, answering " + this.getCommandIdentifier());
		}
	}

	protected CovidImpfDeBot getBot() {
		return this.bot;
	}

}
