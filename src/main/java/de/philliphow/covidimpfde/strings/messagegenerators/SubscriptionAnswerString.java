package de.philliphow.covidimpfde.strings.messagegenerators;

import de.philliphow.covidimpfde.telegram.commands.SubscribeCommand;
import de.philliphow.covidimpfde.telegram.commands.UnsubscribeCommand;

/**
 * Class that provides several textual answers to the /sub or /unsub commands
 * 
 * @see {@link SubscribeCommand}
 * @see {@link UnsubscribeCommand}
 * @author PhillipHow
 *
 */
public class SubscriptionAnswerString implements MessageStringGenerator {

	private final String text;

	private SubscriptionAnswerString(String text) {
		this.text = text;
	}

	@Override
	public String getTextAsMarkdown() {
		return text;
	}

	public static SubscriptionAnswerString subscriptionSucessfull() {
		return new SubscriptionAnswerString(
				"Dieser Chat enthält ab jetzt wöchentliche Updates. Um die wöchentlichen Updates zu beenden, klicke /unsub.");
	}

	public static SubscriptionAnswerString alreadySubscribed() {
		return new SubscriptionAnswerString("Dieser Chat erhält bereits Updates!");
	}

	public static SubscriptionAnswerString unsubscribeSucessfull() {
		return new SubscriptionAnswerString(
				"Dieser Chat erhält keine wöchentliche Updates mehr. Wenn du es dir anders überlegst, klick /sub");
	}

	public static SubscriptionAnswerString notSubscribed() {
		return new SubscriptionAnswerString("Dieser Chat ist im Moment nicht abonniert!");
	}

}
