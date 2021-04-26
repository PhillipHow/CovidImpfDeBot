package de.philliphow.de.philliphow.covidimpfde;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.philliphow.covidimpfde.api.models.DeliveryDataRow;
import de.philliphow.covidimpfde.api.models.Vaccine;
import de.philliphow.covidimpfde.logic.DeliveryDataInterpretation;

public class DeliveryDataInterpretationTest {

	DeliveryDataInterpretation example;
	List<DeliveryDataRow> exampleData;

	LocalDate today = LocalDate.now();
	LocalDate oneWeekAgo = LocalDate.now().minusDays(8);
	LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);

	final String vaccineModerna = "moderna";
	final String vaccineBiontech = "comirnaty";

	public static final double EPSILON = 0.000000001;

	@BeforeEach
	public void setUp() {

		exampleData = new ArrayList<>();
		exampleData.add(new DeliveryDataRow(oneMonthAgo, vaccineBiontech, "DE-BW", 600));
		exampleData.add(new DeliveryDataRow(oneMonthAgo, vaccineModerna, "DE-BW", 700));
		
		exampleData.add(new DeliveryDataRow(oneWeekAgo, vaccineModerna, "DE-BW", 100));
		exampleData.add(new DeliveryDataRow(oneWeekAgo, vaccineModerna, "DE-BW", 200));
		
		exampleData.add(new DeliveryDataRow(today, vaccineBiontech, "DE-1", 300));
		exampleData.add(new DeliveryDataRow(today, vaccineBiontech, "DE-2", 400));
		exampleData.add(new DeliveryDataRow(today, vaccineModerna, "DE-3", 500));


		example = new DeliveryDataInterpretation(exampleData);
	}
	
	@Test
	public void getLastWeekDeliveredDosesIsCorrect() {
		assertEquals(300 + 400 + 500, example.getLastWeekDelivieredDoses());
	}
	
	@Test
	public void getLastWeekSuppliersIsCorrect() {
		assertEquals(2, example.getLastWeekNumberOfSuppliers());
	}

	@Test
	public void totalDeliveredDosesIsCorrect() {
		assertEquals(100 + 200 + 300 + 400 + 500 + 600 + 700, example.getTotalDeliveredDoses());
	}

	@Test
	public void shareByVaccineIsCorrect() {
		int totalDeliveredDoses = example.getTotalDeliveredDoses();

		double shareModerna = (double) (100 + 200 + 500 + 700) / totalDeliveredDoses;
		double shareBiontech = (double) (300 + 400 + 600) / totalDeliveredDoses;

		Map<Vaccine, Double> shareMap = example.getShareByVaccine();

		assertEquals(shareModerna, shareMap.get(Vaccine.byDeliveryDataIdentifier(vaccineModerna)), EPSILON);
		assertEquals(shareBiontech, shareMap.get(Vaccine.byDeliveryDataIdentifier(vaccineBiontech)), EPSILON);

	}

	@Test
	public void lastDeliveryCorrect() {
		assertEquals(exampleData.get(exampleData.size() - 1), example.getLatestDelivery());
	}

	@Test
	public void lastNDeliveriesCorrect() {
		List<DeliveryDataRow> lastThreeDeliveries = example.getLastNDeliveries(3);

		assertEquals(3, lastThreeDeliveries.size());
		for (int i = 1; i <= 3; i++)
			assertTrue(lastThreeDeliveries.contains(exampleData.get(exampleData.size() - i)));
	}

	@Test
	public void lastNDeliveriesCorrectForNGreaterThenDataSize() {

		List<DeliveryDataRow> lastFiftyDeliveries = example.getLastNDeliveries(56);

		assertEquals(7, lastFiftyDeliveries.size());
	}

	@Test
	public void dosesByVaccineCorrect() {

		int doesesModerna = 100 + 200 + 500 + 700;
		int dosesBiontech = 300 + 400 + 600;

		Map<Vaccine, Integer> doseMap = example.getDosesDeliveredByVaccine();

		assertEquals(2, doseMap.keySet().size());
		assertEquals(doesesModerna, doseMap.get(Vaccine.byDeliveryDataIdentifier(vaccineModerna)));
		assertEquals(dosesBiontech, doseMap.get(Vaccine.byDeliveryDataIdentifier(vaccineBiontech)));
	}

	@Test
	public void biggestDeliveryCorrect() {
		assertEquals(exampleData.get(1), example.getBiggestDelivery());
	}

	@Test
	public void emptyDatasetRejected() {

		Assertions.assertThrows(Exception.class, () -> {
			new DeliveryDataInterpretation(new ArrayList<>());
		});

	}

	@Test
	public void newVaccineIsCountedEvenIfNoInstanceExists() {

		String newVaccineName = "excitingNewVaccine";

		List<DeliveryDataRow> data = new ArrayList<>();
		data.add(new DeliveryDataRow(LocalDate.now(), newVaccineName, "DE-BW", 100));
		DeliveryDataInterpretation interpretation = new DeliveryDataInterpretation(data);

		assertEquals(100, interpretation.getTotalDeliveredDoses());
		assertEquals(1, interpretation.getDosesDeliveredByVaccine().keySet().size());

		Vaccine newVaccineObject = interpretation.getLatestDelivery().getVaccine();

		assertNull(newVaccineObject.getVaccinationsDataField());
		assertEquals(newVaccineName, newVaccineObject.getDeliveryDataIdentifier());
		assertNotNull(newVaccineObject.getHumamReadableName());

	}

}
