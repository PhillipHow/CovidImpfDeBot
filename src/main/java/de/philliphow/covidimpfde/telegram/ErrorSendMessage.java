package de.philliphow.covidimpfde.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Class that provides a few error messages via static factory functions. The
 * returned objects are ready to be send via the {@link TelegramLongPollingBot}
 * {@code execute(SendMessage)} API.
 * 
 * @author PhillipHow
 *
 */
public class ErrorSendMessage extends SendMessage {

	private ErrorSendMessage(String chatId, String text) {
		this.enableMarkdown(true);
		this.setChatId(chatId);
		this.setText(text);
	}

	@Override
	public void setText(String text) {
		super.setText(String.format("*Fehler! --*\n%s", text));
	}

	public static ErrorSendMessage unknownError(String chatId) {
		return new ErrorSendMessage(chatId, "Das hat nicht geklappt! Bitte versuche es später noch einmal.");
	}

	public static ErrorSendMessage couldNotSubscribe(String chatId) {
		return new ErrorSendMessage(chatId,
				"Subscriben hat gerade nicht geklappt. Bitte versuche es später noch einmal.");
	}

	public static ErrorSendMessage couldNotUnsubscribe(String chatId) {
		return new ErrorSendMessage(chatId,
				"Unsubscriben hat gerade nicht geklappt. Bitte versuche es später noch einmal.");
	}

}
