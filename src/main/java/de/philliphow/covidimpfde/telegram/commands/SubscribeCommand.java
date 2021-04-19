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
 * Implements the logic of the {@code /sub} command. Tries to subscribe the user
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
	public SendMessage getAnswerForQuery(String userId, String[] args) {

		try {

			SendMessage answerMessage = new SendMessage();
			answerMessage.setChatId(userId);

			MessageStringGenerator answerString;
			if (new SubListPersistence().subscribe(userId)) {
				answerString = SubscriptionAnswerString.subscriptionSucessfull();
				Logger.info("A user subscribed");
			} else {
				answerString = SubscriptionAnswerString.alreadySubscribed();
			}

			answerMessage.setText(answerString.getTextAsMarkdown());
			return answerMessage;

		} catch (SubPersistenceException subPersistenceException) {
			this.getBot().notifyAdminOnTelegram("SubPesistence threw exception on user sub!");
			Logger.error(subPersistenceException);
			return ErrorSendMessage.couldNotSubscribe(userId);
		}

	}

}