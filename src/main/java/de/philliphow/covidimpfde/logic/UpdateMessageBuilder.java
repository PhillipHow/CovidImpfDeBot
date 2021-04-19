package de.philliphow.covidimpfde.logic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.pmw.tinylog.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Abstract Builder to generate a {@link SendMessage} update for a list of data
 * entries of type {@code T}.
 * 
 * @author PhillipHow
 * @see DeliveryUpdateBuilder, {@link VaccinationUpdateBuilder}
 * @param <T> the data type of the data rows used as source material for the
 *        update
 */
public abstract class UpdateMessageBuilder<T> {

	/**
	 * Telegram chatId of the user receiving the update
	 */
	private String chatId = null;
	/**
	 * List of data rows that form the source material for this update
	 */
	private List<T> allDataRows = null;
	/**
	 * true if the user has subscribed to daily updates from the Telegram bot.
	 */
	private boolean isSubbed = true;
	/**
	 * Date to filter data records by. If set, only data rows up to this date will
	 * be used to generate the update.
	 */
	private LocalDate debugDate = LocalDate.now();

	/**
	 * Generates the update String from the source data.
	 * 
	 * @param allDataRows the source data
	 * @param isSubbed    true if the user has subscribed to daily bot updates
	 * @return a String in markdown format with the update
	 */
	public abstract String getMessageText(List<T> allDataRows, boolean isSubbed);

	/**
	 * Extracts the date from a data records. Used to filter dates for debug
	 * purposes.
	 * 
	 * @param dataRow the data row which date is needed
	 * @return the date of the data row
	 */
	public abstract LocalDate getDateFor(T dataRow);

	public UpdateMessageBuilder<T> setChatId(String chatId) {
		this.chatId = chatId;
		return this;
	}

	public UpdateMessageBuilder<T> setChatId(long chatId) {
		return this.setChatId(Long.toString(chatId));
	}

	public UpdateMessageBuilder<T> setIsSubbed(boolean isSubbed) {
		this.isSubbed = isSubbed;
		return this;
	}

	public UpdateMessageBuilder<T> setContentData(List<T> allDataRows) {
		this.allDataRows = new ArrayList<>(allDataRows);
		return this;
	}

	/**
	 * If this date gets set, the update ignores all data rows that have a date
	 * after debugDate.
	 * 
	 * @param debugDate the last date to be included in the update
	 * @return the builder
	 */
	public UpdateMessageBuilder<T> setDebugDate(LocalDate debugDate) {
		this.debugDate = debugDate;
		return this;
	}

	private boolean isReady() {
		return chatId != null && allDataRows != null;
	}

	public SendMessage build() {
		if (!isReady())
			throw new UnsupportedOperationException("data and chatId need to be set to build UpdateMessage!");

		if (!this.debugDate.equals(LocalDate.now()))
			Logger.debug("Including only updates up to {}", debugDate);

		List<T> vaccinationDataToUse = this.allDataRows.stream()
				.filter(vaccDataRow -> getDateFor(vaccDataRow).isBefore(debugDate)
						|| getDateFor(vaccDataRow).isEqual(debugDate))
				.collect(Collectors.toList());

		SendMessage message = new SendMessage();
		message.enableMarkdown(true);
		message.setDisableWebPagePreview(true);
		message.setChatId(chatId);
		message.setText(getMessageText(vaccinationDataToUse, this.isSubbed));

		return message;
	}

}
