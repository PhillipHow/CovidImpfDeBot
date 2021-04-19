package de.philliphow.covidimpfde.telegram.commands;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import de.philliphow.covidimpfde.api.VaccinationsApiManager;
import de.philliphow.covidimpfde.api.models.VaccinationDataRow;
import de.philliphow.covidimpfde.exceptions.SubPersistenceException;
import de.philliphow.covidimpfde.logic.UpdateMessageBuilder;
import de.philliphow.covidimpfde.logic.VaccinationUpdateBuilder;
import de.philliphow.covidimpfde.strings.messagegenerators.VaccinationUpdateString;
import de.philliphow.covidimpfde.telegram.CovidImpfDeBot;
import de.philliphow.covidimpfde.telegram.TelegramCommandWrapper;
import de.philliphow.covidimpfde.util.SubListPersistence;

/**
 * Implements the logic for the {@code /impf} command. Answers with a
 * vaccination update.
 * 
 * @author PhillipHow
 * @see VaccinationUpdateBuilder
 * @see VaccinationUpdateString
 *
 */
public class VaccinationCommand extends TelegramCommandWrapper {

	public VaccinationCommand(CovidImpfDeBot bot) {
		super("impf", "", bot);
	}

	@Override
	public SendMessage getAnswerForQuery(String userId, String[] args) {

		List<VaccinationDataRow> vaccinationData = VaccinationsApiManager.getInstance(getBot().getDebugMode())
				.getCurrentData();
		boolean userIsSubbed = getUserIsSubbed(userId);

		UpdateMessageBuilder<VaccinationDataRow> updateBuilder = new VaccinationUpdateBuilder()
				.setIsSubbed(userIsSubbed).setChatId(userId).setContentData(vaccinationData);

		addDebugDateToBuilderIfGiven(updateBuilder, args);

		return updateBuilder.build();
	}

	private boolean getUserIsSubbed(String userId) {
		try {
			return new SubListPersistence().isSubbed(userId);
		} catch (SubPersistenceException subPersistenceException) {
			// recover and log to deliver message nontheless
			this.getBot().notifyAdminOnTelegram("WARNING: SubPersistence threw IOException!");
			return false;
		}
	}

	private void addDebugDateToBuilderIfGiven(UpdateMessageBuilder<VaccinationDataRow> updateBuilder, String[] args) {

		if (args.length == 1) {
			try {
				LocalDate debugDate = LocalDate.parse(args[0]);
				updateBuilder.setDebugDate(debugDate);
			} catch (DateTimeException e) {
				// user gave non-parseable date - just ignore it, was probably by accident
			}
		}

	}

}
