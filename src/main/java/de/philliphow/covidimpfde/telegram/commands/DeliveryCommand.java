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
 * Implements the logic for the {@code /lieferung} command. Answers with a
 * delivery update.
 * 
 * @author PhillipHow
 * @see DeliveryUpdateBuilder
 * @see DeliveryUpdateString
 *
 */
public class DeliveryCommand extends TelegramCommandWrapper {

	public DeliveryCommand(CovidImpfDeBot bot) {
		super("lieferung", "", bot);
	}

	@Override
	public SendMessage getAnswerForQuery(String chatId, String[] args) {

		List<DeliveryDataRow> deliveries = DeliveryApiManager.getInstance(getBot().getDebugMode()).getCurrentData();
		boolean chatIsSubbed = getChatIsSubbed(chatId);
		int subCount = getSubCount();

		UpdateMessageBuilder<DeliveryDataRow> deliveryUpdateBuilder = new DeliveryUpdateBuilder()
				.setChatId(chatId)
				.setContentData(deliveries)
				.setIsSubbed(chatIsSubbed)
				.setSubCount(subCount);

		return deliveryUpdateBuilder.build();
	}

	private boolean getChatIsSubbed(String chatId) {
		try {
			return new SubListPersistence().isSubbed(chatId);
		} catch (SubPersistenceException subPersistenceException) {
			// recover and log to deliver message nonetheless
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

}
