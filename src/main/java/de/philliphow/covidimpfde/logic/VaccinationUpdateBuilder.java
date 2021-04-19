package de.philliphow.covidimpfde.logic;

import java.time.LocalDate;
import java.util.List;

import de.philliphow.covidimpfde.api.models.VaccinationDataRow;
import de.philliphow.covidimpfde.strings.messagegenerators.VaccinationUpdateString;
import de.philliphow.covidimpfde.telegram.commands.VaccinationCommand;

/**
 * Concrete Builder for a vaccination update, for example for answering a query
 * with the {@link VaccinationCommand}. Relies heavily on
 * {@link VaccinationDataInterpretation} and {@link VaccinationUpdateString} for
 * that. See {@link UpdateMessageBuilder} for more documentation on the desired
 * behavior of this class.
 * 
 * @author PhillipHow
 *
 */
public class VaccinationUpdateBuilder extends UpdateMessageBuilder<VaccinationDataRow> {

	@Override
	public String getMessageText(List<VaccinationDataRow> allDataRows, boolean isSubbed) {
		VaccinationDataInterpretation vaccinationDataInterpretation = new VaccinationDataInterpretation(allDataRows);
		return new VaccinationUpdateString(vaccinationDataInterpretation, isSubbed).getTextAsMarkdown();
	}

	@Override
	public LocalDate getDateFor(VaccinationDataRow dataRow) {
		return dataRow.getDate();
	}

}
