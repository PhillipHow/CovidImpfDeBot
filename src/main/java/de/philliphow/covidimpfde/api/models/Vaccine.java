package de.philliphow.covidimpfde.api.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.pmw.tinylog.Logger;

import de.philliphow.covidimpfde.api.models.VaccinationDataRow.VaccinationsDataField;
import de.philliphow.covidimpfde.strings.StrUtil;

/**
 * An abstraction of the different vaccines used in the current vaccine
 * campaign. Provides a static factory method which returns all known vaccines
 * used so far. Might need to be extended in the future to exclude extra
 * vaccines. Until then, new vaccine fields in the source data do not break the
 * code, but might be ignored if only vaccines from {@code getAll()} are used.
 * 
 * @author PhillipHow
 *
 */
public class Vaccine {

	/**
	 * The field to query in a {@link VaccinationDataRow} to get the total
	 * vaccinations with this vaccine
	 */
	private final VaccinationsDataField vaccinationsDataField;
	/**
	 * The value to identify this vaccine in the {@code DeliveryDataField.VACCINE}
	 * field in a {@link DeliveryDataRow}
	 */
	private final String deliveryDataIdentifier;
	private final String humanReadableName;

	/**
	 * Returns all vaccines currently known to be used. Keep in mind that this list
	 * might not be exhaustive, as new vaccines are added during the campaign.
	 * 
	 * @return the list of vaccines
	 */
	public static List<Vaccine> getAll() {
		List<Vaccine> vaccines = new ArrayList<>();
		vaccines.add(new Vaccine(VaccinationsDataField.SHOTS_TOTAL_MODERNA, "moderna", "Moderna"));
		vaccines.add(new Vaccine(VaccinationsDataField.SHOTS_TOTAL_BIONTECH, "comirnaty", "Biontech/Pfizer"));
		vaccines.add(new Vaccine(VaccinationsDataField.SHOTS_TOTAL_ASTRA, "astra", "Astra Zeneca"));
		vaccines.add(new Vaccine(VaccinationsDataField.SHOTS_TOTAL_JOHNSON, "johnson", "Johnson & Johnson"));
		
		return vaccines;
	}

	/**
	 * Gets a {@code Vaccine} by the identifier in the
	 * {@code DeliveryDataField.VACCINE} field of a {@link DeliveryDataRow} Note
	 * that this also returns new unknown vaccines. In that case,
	 * vaccinationsDataField is null.
	 * 
	 * @param deliveryDataIdentifier
	 * @return the Vaccine object
	 */
	public static Vaccine byDeliveryDataIdentifier(String deliveryDataIdentifier) {
		Optional<Vaccine> candidate = getAll().stream()
				.filter(vaccine -> vaccine.getDeliveryDataIdentifier().equals(deliveryDataIdentifier)).findFirst();

		if (candidate.isPresent()) {
			return candidate.get();
		} else {
			Logger.warn("An unkown vaccine identifier appeared in delivery data: {}", deliveryDataIdentifier);
			return new Vaccine(null, deliveryDataIdentifier, StrUtil.capitalized(deliveryDataIdentifier));
		}
	}

	private Vaccine(VaccinationsDataField vaccinationRowField, String deliveryDataIdentifier,
			String humanReadableName) {
		this.vaccinationsDataField = vaccinationRowField;
		this.humanReadableName = humanReadableName;
		this.deliveryDataIdentifier = deliveryDataIdentifier;
	}

	public String getHumamReadableName() {
		return humanReadableName;
	}

	/**
	 * @return the identifier to search for in the {@code DeliveryDataField.VACCINE}
	 *         field of a {@link DeliveryDataRow} to identify a delivery of this
	 *         vaccine
	 */
	public String getDeliveryDataIdentifier() {
		return deliveryDataIdentifier;
	}

	/**
	 * @return the field to query in a {@link VaccinationDataRow} to get the total
	 *         vaccinations with this vaccine
	 */
	public VaccinationsDataField getVaccinationsDataField() {
		return this.vaccinationsDataField;
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof Vaccine))
			return false;

		Vaccine other = (Vaccine) obj;
		return this.getDeliveryDataIdentifier().equals(other.getDeliveryDataIdentifier());
	}

	@Override
	public int hashCode() {
		return this.getDeliveryDataIdentifier().hashCode();
	}

}
