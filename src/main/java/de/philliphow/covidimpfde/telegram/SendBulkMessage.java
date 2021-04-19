package de.philliphow.covidimpfde.telegram;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.pmw.tinylog.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SendBulkMessage {

	public interface SendBulkMessageCallback {
		public void callback(List<String> sucessfullChatIds, List<String> chatIdsWithErrors);
	}

	private final long TIME_BETWEEN_SENDS_MILLIS = 200;

	private final List<String> chatIds;
	private final Function<String, SendMessage> getMessageForChatId;
	private final AbsSender sender;

	private final List<String> successfullChatIds = new ArrayList<>();
	private final List<String> errorChatIds = new ArrayList<>();

	public SendBulkMessage(List<String> chatIds, Function<String, SendMessage> getMessageForChatId, AbsSender sender) {
		this.chatIds = new ArrayList<>(chatIds);
		this.getMessageForChatId = getMessageForChatId;
		this.sender = sender;
	}

	public void sendAll(SendBulkMessageCallback callback) {

		Runnable thread = new Thread() {

			@Override
			public void run() {

				chatIds.forEach(chatId -> {

					try {
						sender.execute(getMessageForChatId.apply(chatId));
						successfullChatIds.add(chatId);
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

	public void sendAll() {
		this.sendAll((sucess, fail) -> {
		});
	}

}
