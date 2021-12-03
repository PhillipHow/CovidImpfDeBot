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
import de.philliphow.covidimpfde.services.SubListPersistence;
import de.philliphow.covidimpfde.strings.messagegenerators.VaccinationUpdateString;
import de.philliphow.covidimpfde.telegram.CovidImpfDeBot;
import de.philliphow.covidimpfde.telegram.TelegramCommandWrapper;

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
	public SendMessage getAnswerForQuery(String chatId, String[] args) {

		List<VaccinationDataRow> vaccinationData = VaccinationsApiManager.getInstance(getBot().getDebugMode())
				.getCurrentData();
		boolean chatIsSubbed = getChatIsSubbed(chatId);
		int subCount = getSubCount();

		UpdateMessageBuilder<VaccinationDataRow> updateBuilder = new VaccinationUpdateBuilder()
				.setIsSubbed(chatIsSubbed)
				.setChatId(chatId)
				.setSubCount(subCount)
				.setContentData(vaccinationData);

		addDebugDateToBuilderIfGiven(updateBuilder, args);

		return updateBuilder.build();
	}

	private boolean getChatIsSubbed(String chatId) {
		try {
			return new SubListPersistence().isSubbed(chatId);
		} catch (SubPersistenceException subPersistenceException) {
			// recover and log to deliver message nontheless
			this.getBot().notifyAdminOnTelegram("WARNING: SubPersistence threw IOException!");
			return false;
		}
	}
	

	private int getSubCount() {
		try {
			return new SubListPersistence().getSubCount();
		} catch (SubPersistenceException subPersistenceException) {
			// recover and log to deliver message nonetheless
			this.getBot().notifyAdminOnTelegram("WARNING: SubPersistence threw IOException!");
			return -1;
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
