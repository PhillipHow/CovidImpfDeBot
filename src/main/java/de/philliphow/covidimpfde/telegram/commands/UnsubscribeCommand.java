package de.philliphow.covidimpfde.telegram.commands;

import org.pmw.tinylog.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import de.philliphow.covidimpfde.exceptions.SubPersistenceException;
import de.philliphow.covidimpfde.strings.messagegenerators.MessageStringGenerator;
import de.philliphow.covidimpfde.strings.messagegenerators.SubscriptionAnswerString;
import de.philliphow.covidimpfde.telegram.CovidImpfDeBot;
import de.philliphow.covidimpfde.telegram.ErrorSendMessage;
import de.philliphow.covidimpfde.telegram.TelegramCommandWrapper;
import de.philliphow.covidimpfde.util.SubListPersistence;

/**
 * Implements the logic of the {@code /unsub} command. Tries to unsubscribe the
 * user and gives feedback.
 * 
 * @author PhillipHow
 *
 */
public class UnsubscribeCommand extends TelegramCommandWrapper {

	public UnsubscribeCommand(CovidImpfDeBot bot) {
		super("unsub", "", bot);
	}

	@Override
	public SendMessage getAnswerForQuery(String userId, String[] args) {

		try {
			SendMessage answerMessage = new SendMessage();
			answerMessage.setChatId(userId);

			MessageStringGenerator answerString;
			if (new SubListPersistence().unsubscribe(userId)) {
				answerString = SubscriptionAnswerString.unsubscribeSucessfull();
				Logger.info("A user unsubscribed");
			} else {
				answerString = SubscriptionAnswerString.notSubscribed();
			}

			answerMessage.setText(answerString.getTextAsMarkdown());
			return answerMessage;

		} catch (SubPersistenceException subPersistenceException) {
			this.getBot().notifyAdminOnTelegram("SubPesistence threw exception on user unsub!");
			Logger.error(subPersistenceException);
			return ErrorSendMessage.couldNotUnsubscribe(userId);
		}

	}

}