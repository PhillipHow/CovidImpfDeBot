package de.philliphow.covidimpfde.api.models;

import java.time.LocalDate;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A representation of the vaccination data for one day. Contains new
 * vaccinations on that day, the total vaccinations, vaccinations by vaccine, first
 * and full dose statistics and the vaccinated population quota. Note that
 * additional data for the indication (the vaccination "reason") is also
 * provided by the API, but not used currently.
 * 
 * @author PhillipHow
 *
 */
public class VaccinationDataRow extends AbstractTsvRow {

	/**
	 * Enum that contains the data field values for the delivered vaccination tsv
	 * file. A tsv vaccination row can be queried for these, for example to get the
	 * number of people vaccinated once.
	 * 
	 * @author PhillipHow
	 *
	 */
	public static enum VaccinationsDataField {
		DATE("date"), SHOTS_TOTAL("dosen_kumulativ"), 
		SHOTS_TOTAL_BIONTECH("dosen_biontech_kumulativ"),
		SHOTS_TOTAL_MODERNA("dosen_moderna_kumulativ"), 
		SHOTS_TOTAL_ASTRA("dosen_astra_kumulativ"),
		SHOTS_TOTAL_JOHNSON("dosen_johnson_kumulativ"),

		SHOTS_TODAY("dosen_differenz_zum_vortag"), 
		SHOTS_TODAY_FIRST("dosen_erst_differenz_zum_vortag"),
		SHOTS_TODAY_SECOND("dosen_zweit_differenz_zum_vortag"),
		SHOTS_TODAY_THIRD("dosen_dritt_differenz_zum_vortag"),

		PERSONS_TOTAL_FIRST("personen_erst_kumulativ"), 
		PERSONS_TOTAL_SECOND("personen_voll_kumulativ"),
		PERSONS_TOTAL_THIRD("personen_auffrisch_kumulativ"),
		
		POPULATION_QUOTA_FIRST_SHOT("impf_quote_erst"), 
		POPULATION_QUOTA_SECOND_SHOT("impf_quote_voll");

		// INDICATION NOT USED CURRENTLY

		/**
		 * The field name. The AbstractTsvRow can be queried with this String to get
		 * date, total shots, etc as string.
		 */
		private final String fieldName;

		private VaccinationsDataField(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldName() {
			return this.fieldName;
		}
	}

	/**
	 * Constructs a {@code VaccinationsDataRow} by a raw tsv file data row.
	 * 
	 * @param dataRow   the raw tsv data row
	 * @param headerRow the raw header row of the tsv file
	 */
	public VaccinationDataRow(String dataRow, String headerRow) {
		super(dataRow, headerRow);
	}

	public LocalDate getDate() {
		return LocalDate.parse(this.getRawStringField(VaccinationsDataField.DATE.fieldName));
	}

	/**
	 * @return total number of shots issued to the day of this data record
	 */
	public int getTotalShots() {
		return this.getIntField(VaccinationsDataField.SHOTS_TOTAL);
	}

	/**
	 * Gets total shots issued by a certain vaccine
	 * 
	 * @param vaccine
	 * @return total number of shots by vaccine
	 */
	public int getTotalShotsByVaccine(Vaccine vaccine) {
		return this.getIntField(vaccine.getVaccinationsDataField());
	}

	/**
	 * @return the number of vaccination shots issued on this day
	 */
	public int getShotsToday() {
		return this.getIntField(VaccinationsDataField.SHOTS_TODAY);
	}

	/**
	 * @return the number of vaccination shots issued on this day on people who have
	 *         not received any vaccine yet
	 */
	public int getFirstShotsToday() {
		return this.getIntField(VaccinationsDataField.SHOTS_TODAY_FIRST);
	}

	/**
	 * @return the number of vaccination shots issued on this day that completed the
	 *         vaccination process for those people
	 */
	public int getSecondShotsToday() {
		return this.getIntField(VaccinationsDataField.SHOTS_TODAY_SECOND);
	}
	
	
	public int getThirdShotsToday() {
		return this.getIntField(VaccinationsDataField.SHOTS_TODAY_THIRD);
	}

	public int getPersonsVaccinatedOnce() {
		return this.getIntField(VaccinationsDataField.PERSONS_TOTAL_FIRST);
	}

	public int getPersonsVaccinatedFull() {
		return this.getIntField(VaccinationsDataField.PERSONS_TOTAL_SECOND);
	}
	
	public int getPersonsVaccinatedThrice() {
		return this.getIntField(VaccinationsDataField.PERSONS_TOTAL_THIRD);
	}

	/**
	 * @return the portion of the population who have received at least one dose
	 */
	public double getPopulationQuotaVaccinatedOnce() {
		return this.getDoubleField(VaccinationsDataField.POPULATION_QUOTA_FIRST_SHOT);
	}

	/**
	 * @return the portion of the population who have been fully vaccinated
	 */
	public double getPopulationQuotaVaccinatedFull() {
		return this.getDoubleField(VaccinationsDataField.POPULATION_QUOTA_SECOND_SHOT);
	}

	private int getIntField(VaccinationsDataField field) {
		return this.getIntField(field.fieldName);
	}

	private double getDoubleField(VaccinationsDataField field) {
		return this.getDoubleField(field.fieldName);
	}

	@Override
	public String toString() {
		return "VaccinationsDataRow " + super.toString();
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof VaccinationDataRow))
			return false;
		VaccinationDataRow other = (VaccinationDataRow) obj;

		Predicate<VaccinationsDataField> fieldsAreEqual = (field) -> this.getRawStringField(field.getFieldName())
				.equals(other.getRawStringField(field.getFieldName()));

		return Stream.of(VaccinationsDataField.values()).filter(field -> this.hasField(field.getFieldName()))
				.allMatch(fieldsAreEqual);

	}



	

}
