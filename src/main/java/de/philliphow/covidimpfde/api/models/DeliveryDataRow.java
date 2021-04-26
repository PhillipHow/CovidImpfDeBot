package de.philliphow.covidimpfde.api.models;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

/***
 * A representation of a vaccine delivery in a certain region.
 * 
 * @author PhillipHow
 *
 */
public class DeliveryDataRow extends AbstractTsvRow {

	/**
	 * Enum that contains all possible data field values for the delivery API tsv
	 * file. A tsv delivery row can be queried for these, for example to get the
	 * number of doses in one row.
	 * 
	 * @author PhillipHow
	 */
	private static enum DeliveryDataField {
		DATE("date"), VACCINE("impfstoff"), DOSES("dosen"), REGION("region");

		/**
		 * The field name. The AbstractTsvRow can be queries with this String to get the
		 * date, vaccine, etc.
		 */
		private final String fieldName;

		private DeliveryDataField(String fieldName) {
			this.fieldName = fieldName;
		}

		/**
		 * @return the title of the data field
		 */
		public String getFieldName() {
			return this.fieldName;
		}
	}

	/**
	 * Constructs a {@code DeliveryDataRow} from a raw tsv data row
	 * 
	 * @param dataRow   the raw tsv data row
	 * @param headerRow the header row of the tsv file
	 */
	public DeliveryDataRow(String dataRow, String headerRow) {
		super(dataRow, headerRow);
	}

	/**
	 * Constructs a {@code DeliveryDataRow} from its plain values
	 * 
	 * @param date    delivery date
	 * @param vaccine delivery vaccine identifier, as given in the tsv row
	 * @param region  region of the delivery
	 * @param doses   number of doses delivered
	 */
	public DeliveryDataRow(LocalDate date, String vaccine, String region, int doses) {
		super(DeliveryDataRow.generateKeyValueMap(date, vaccine, region, doses));
	}

	private LocalDate getDate() {
		return LocalDate.parse(this.getRawStringField(DeliveryDataField.DATE.getFieldName()));
	}
	
	public int getCalendarWeek() {
		return getDate().get(ChronoField.ALIGNED_WEEK_OF_YEAR);
	}
	
	public LocalDate getCalendarWeekMonday() {
		return getMondayFor(this.getDate());
	}

	/**
	 * @return a plain String that is the vaccine name, as provided in the tsv file
	 */
	public String getVaccineIdentifier() {
		return this.getStringField(DeliveryDataField.VACCINE);
	}

	/**
	 * @return an object of type Vaccine
	 */
	public Vaccine getVaccine() {
		return Vaccine.byDeliveryDataIdentifier(this.getVaccineIdentifier());
	}

	/**
	 * @return the number of doses delivered in this delivery
	 */
	public int getDoses() {
		return this.getIntField(DeliveryDataField.DOSES);
	}

	/**
	 * @return the local region to which the vaccine has been delivered. Can be used
	 *         to group multiple local deliveries to one big federal delivery.
	 */
	public String getRegion() {
		return this.getStringField(DeliveryDataField.REGION);
	}

	private String getStringField(DeliveryDataField field) {
		return this.getRawStringField(field.fieldName);
	}

	private int getIntField(DeliveryDataField field) {
		return this.getIntField(field.fieldName);
	}

	/**
	 * Combines two similar deliveries. <i>Similar</i> means that the two deliveries
	 * contained the same vaccine and were issued in the same week. The number of
	 * doses are added to each other. Method can be used to combine multiple local
	 * deliveries to get one big federal delivery.
	 * 
	 * @param onePart     the first delivery
	 * @param anotherPart the second delivery, with the same date and the same
	 *                    vaccineIdentifier as the {@code onePart} delivery
	 * @return a new delivery with the same date and vaccine as the input
	 *         deliveries. Doses are summed up. Region information is lost and set
	 *         to "MULTIPLE".
	 */
	public static DeliveryDataRow combineDeliveryParts(DeliveryDataRow onePart, DeliveryDataRow anotherPart) {

		if (onePart.referenceTheSameWeeklyDelivery(anotherPart)) {
			return new DeliveryDataRow(onePart.getDate(), onePart.getVaccineIdentifier(), "MULTIPLE",
					onePart.getDoses() + anotherPart.getDoses());
		} else {
			throw new IllegalArgumentException("can only combine rows that belong to the same federal delivery");
		}
	}

	private static Map<String, String> generateKeyValueMap(LocalDate date, String vaccine, String region, int doses) {
		Map<String, String> keyValueMap = new HashMap<>();
		keyValueMap.put(DeliveryDataField.DATE.fieldName, date.toString());
		keyValueMap.put(DeliveryDataField.VACCINE.fieldName, vaccine);
		keyValueMap.put(DeliveryDataField.DOSES.fieldName, Integer.toString(doses));
		keyValueMap.put(DeliveryDataField.REGION.fieldName, region);
		return keyValueMap;
	}

	@Override
	public String toString() {
		return "DeliveryDataRow " + super.toString();
	}

	private boolean referenceTheSameWeeklyDelivery(DeliveryDataRow other) {
		return this.getCalendarWeekMonday().equals(other.getCalendarWeekMonday())
				&& this.getVaccineIdentifier().equals(other.getVaccineIdentifier());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DeliveryDataRow))
			return false;
		DeliveryDataRow other = (DeliveryDataRow) obj;

		return this.referenceTheSameWeeklyDelivery(other) && this.getDoses() == other.getDoses()
				&& this.getRegion().equals(other.getRegion());

	}
	
	public static LocalDate getMondayFor(LocalDate date) {
		int dayOfWeek = date.getDayOfWeek().getValue();
		LocalDate monday = date.minusDays(dayOfWeek - 1);
		return monday;
	}

}
