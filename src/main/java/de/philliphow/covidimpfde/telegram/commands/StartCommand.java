package de.philliphow.covidimpfde.telegram.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import de.philliphow.covidimpfde.telegram.CovidImpfDeBot;
import de.philliphow.covidimpfde.telegram.TelegramCommandWrapper;

/**
 * Implements the logic for the {@code /start} command, which is called when the
 * bot is first launches. Just forwards the query as if it was an {@code /impf}
 * command.
 * 
 * @author PhillipHow
 *
 */
public class StartCommand extends TelegramCommandWrapper {

	public StartCommand(CovidImpfDeBot bot) {
		super("start", "", bot);
	}

	@Override
	public SendMessage getAnswerForQuery(String userId, String[] args) {
		return new VaccinationCommand(this.getBot()).getAnswerForQuery(userId, args);
	}

}
