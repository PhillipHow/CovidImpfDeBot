package de.philliphow.de.philliphow.covidimpfde;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.philliphow.covidimpfde.api.DeliveryApiManager;
import de.philliphow.covidimpfde.api.models.Vaccine;
import de.philliphow.covidimpfde.exceptions.ImpfDashboardApiException;

public class VaccineTest {

	// will fail until johnson&johnson is fully integrated - but 
	// can only integrate j&j when it appears in vaccinations.tsv
	@Test
	public void everyVaccineInCurrentDeliveryDataExistsInCode() throws ImpfDashboardApiException {

		DeliveryApiManager.getInstance(false).getNewDataIfNecessary();
		DeliveryApiManager.getInstance(false).getCurrentData().forEach(dataRow -> {

			assertTrue(Vaccine.getAll().contains(dataRow.getVaccine()));
			assertNotNull(Vaccine.byDeliveryDataIdentifier(dataRow.getVaccineIdentifier()));
		});
	}

	@Test
	public void equalsWorks() {
		Vaccine example1 = Vaccine.getAll().get(0);
		Vaccine example2 = Vaccine.getAll().get(1);

		assertEquals(example1, example1);
		assertNotEquals(example1, example2);

	}

	@Test
	public void allGeneratedVaccinesProvideFieldsForDeliveryAndVaccinationData() {

		Vaccine.getAll().forEach(vaccine -> {

			assertNotNull(vaccine.getDeliveryDataIdentifier());
			assertNotNull(vaccine.getHumamReadableName());
			assertNotNull(vaccine.getVaccinationsDataField());
		});
	}

}
