package de.philliphow.covidimpfde.telegram.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import de.philliphow.covidimpfde.api.DeliveryApiManager;
import de.philliphow.covidimpfde.api.models.DeliveryDataRow;
import de.philliphow.covidimpfde.exceptions.SubPersistenceException;
import de.philliphow.covidimpfde.logic.DeliveryUpdateBuilder;
import de.philliphow.covidimpfde.logic.UpdateMessageBuilder;
import de.philliphow.covidimpfde.services.SubListPersistence;
import de.philliphow.covidimpfde.strings.messagegenerators.DeliveryUpdateString;
import de.philliphow.covidimpfde.telegram.CovidImpfDeBot;
import de.philliphow.covidimpfde.telegram.TelegramCommandWrapper;

/**
 * Implements the logic for the {@code /letztelieferung} command. Answers with a
 * delivery update.
 * 
 * @author PhillipHow
 * @see DeliveryUpdateBuilder
 * @see DeliveryUpdateString
 *
 */
public class DeliveryCommand extends TelegramCommandWrapper {

	public DeliveryCommand(CovidImpfDeBot bot) {
		super("letztelieferung", "", bot);
	}

	@Override
	public SendMessage getAnswerForQuery(String userId, String[] args) {

		List<DeliveryDataRow> deliveries = DeliveryApiManager.getInstance(getBot().getDebugMode()).getCurrentData();
		boolean userIsSubbed = getUserIsSubbed(userId);

		UpdateMessageBuilder<DeliveryDataRow> deliveryUpdateBuilder = new DeliveryUpdateBuilder().setChatId(userId)
				.setContentData(deliveries).setIsSubbed(userIsSubbed);

		return deliveryUpdateBuilder.build();
	}

	private boolean getUserIsSubbed(String userId) {
		try {
			return new SubListPersistence().isSubbed(userId);
		} catch (SubPersistenceException subPersistenceException) {
			// recover and log to deliver message nonetheless
			this.getBot().notifyAdminOnTelegram("WARNING: SubPersistence threw IOException!");
			return false;
		}
	}

}
