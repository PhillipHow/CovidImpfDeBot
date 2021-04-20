package de.philliphow.covidimpfde.telegram.commands;

import org.pmw.tinylog.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import de.philliphow.covidimpfde.exceptions.SubPersistenceException;
import de.philliphow.covidimpfde.services.SubListPersistence;
import de.philliphow.covidimpfde.strings.messagegenerators.MessageStringGenerator;
import de.philliphow.covidimpfde.strings.messagegenerators.SubscriptionAnswerString;
import de.philliphow.covidimpfde.telegram.CovidImpfDeBot;
import de.philliphow.covidimpfde.telegram.ErrorSendMessage;
import de.philliphow.covidimpfde.telegram.TelegramCommandWrapper;

/**
 * Implements the logic of the {@code /sub} command. Tries to subscribe the chat
 * and gives feedback.
 * 
 * @author PhillipHow
 *
 */
public class SubscribeCommand extends TelegramCommandWrapper {

	public SubscribeCommand(CovidImpfDeBot bot) {
		super("sub", "", bot);
	}

	@Override
	public SendMessage getAnswerForQuery(String chatId, String[] args) {

		try {

			SendMessage answerMessage = new SendMessage();
			answerMessage.setChatId(chatId);

			MessageStringGenerator answerString;
			if (new SubListPersistence().subscribe(chatId)) {
				answerString = SubscriptionAnswerString.subscriptionSucessfull();
				Logger.info("A chat subscribed");
			} else {
				answerString = SubscriptionAnswerString.alreadySubscribed();
			}

			answerMessage.setText(answerString.getTextAsMarkdown());
			return answerMessage;

		} catch (SubPersistenceException subPersistenceException) {
			this.getBot().notifyAdminOnTelegram("SubPesistence threw exception on user sub!");
			Logger.error(subPersistenceException);
			return ErrorSendMessage.couldNotSubscribe(chatId);
		}

	}

}