package de.philliphow.covidimpfde.telegram;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.pmw.tinylog.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Class that sends message to an arbitrary big number of telegram
 * recipients. 
 * @author PhillipHow
 *
 */
public class BulkMessageSender {

	public interface SendBulkMessageCallback {
		/**
		 * Method that is called after a bulk update is send. 
		 * @param sucessfullChatIds the chatIds that were reached successfully 
		 * @param chatIdsWithErrors the chatIds that threw an error. These users most
		 * 	likely blocked the bot.
		 */
		public void callback(List<String> sucessfullChatIds, List<String> chatIdsWithErrors);
	}

	private final long TIME_BETWEEN_SENDS_MILLIS = 100;

	private final List<String> chatIds;
	private final Function<String, SendMessage> getMessageForChatId;
	private final AbsSender sender;

	private final List<String> successfullChatIds = new ArrayList<>();
	private final List<String> errorChatIds = new ArrayList<>();

	/**
	 * Constructs a BulkMessageSender.
	 * @param chatIds the recipients of the bulk message
	 * @param getMessageForChatId a function that generates the {@link SendMessage} for a userId
	 * @param sender the bot to send the message with
	 */
	public BulkMessageSender(List<String> chatIds, Function<String, SendMessage> getMessageForChatId, AbsSender sender) {
		this.chatIds = new ArrayList<>(chatIds);
		this.getMessageForChatId = getMessageForChatId;
		this.sender = sender;
	}

	/**
	 * Initiates bulk message send. 
	 * @param callback to be called when all messages have been send, contains
	 * successfull and failed chatIds
	 */
	public void sendAllAsync(SendBulkMessageCallback callback) {

		Runnable thread = new Thread() {

			@Override
			public void run() {

				chatIds.forEach(chatId -> {

					try {
						sender.execute(getMessageForChatId.apply(chatId));
						successfullChatIds.add(chatId);
						//sleep to avoid hitting telegram limits
						TimeUnit.MILLISECONDS.sleep(TIME_BETWEEN_SENDS_MILLIS);
					} catch (TelegramApiException exception) {
						errorChatIds.add(chatId);
					} catch (InterruptedException exception) {
						Logger.error(exception, "Interruped during BulkSend");
					}

				});

				callback.callback(successfullChatIds, errorChatIds);
			}
		};

		thread.run();
	}

	
	/**
	 * Initiates bulk message send. 
	 */
	public void sendAllAsync() {
		this.sendAllAsync((sucess, fail) -> {
		});
	}

}
