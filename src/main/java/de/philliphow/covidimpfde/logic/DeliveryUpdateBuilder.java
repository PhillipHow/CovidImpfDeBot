package de.philliphow.covidimpfde.logic;

import java.time.LocalDate;
import java.util.List;

import de.philliphow.covidimpfde.api.models.DeliveryDataRow;
import de.philliphow.covidimpfde.strings.messagegenerators.DeliveryUpdateString;
import de.philliphow.covidimpfde.telegram.commands.DeliveryCommand;

/**
 * Concrete Builder for a delivery update, for example after a query with the
 * {@link DeliveryCommand}. Relies heavily on {@link DeliveryUpdateString} and
 * {@link DeliveryDataInterpretation} for that. See {@link UpdateMessageBuilder}
 * for more documentation on the desired behavior of this class.
 * 
 * @author PhillipHow
 *
 */
public class DeliveryUpdateBuilder extends UpdateMessageBuilder<DeliveryDataRow> {

	@Override
	public String getMessageText(List<DeliveryDataRow> allDataRows, boolean isSubbed) {
		DeliveryDataInterpretation dataInterpretation = new DeliveryDataInterpretation(allDataRows);
		return new DeliveryUpdateString(dataInterpretation, isSubbed).getTextAsMarkdown();
	}

	@Override
	public LocalDate getDateFor(DeliveryDataRow dataRow) {
		return dataRow.getCalendarWeekMonday();
	}

}