package de.philliphow.de.philliphow.covidimpfde;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.philliphow.covidimpfde.api.DeliveryApiManager;
import de.philliphow.covidimpfde.api.VaccinationsApiManager;
import de.philliphow.covidimpfde.logic.DeliveryUpdateBuilder;
import de.philliphow.covidimpfde.logic.VaccinationUpdateBuilder;

public class ApiTest {

	@Test
	public void deliveryApiResultCanBeBuildToMessage() throws IOException {

		DeliveryApiManager.getInstance(true).getNewDataIfNecessary();
		assertFalse(DeliveryApiManager.getInstance(true).getCurrentData().isEmpty());
		assertDoesNotThrow(() -> {
			new DeliveryUpdateBuilder().setChatId("aChatId")
					.setContentData(DeliveryApiManager.getInstance(true).getCurrentData()).build();
		});

	}

	@Test
	public void vaccinationApiResultCanBeBuildToMessage() throws IOException {

		VaccinationsApiManager.getInstance(true).getNewDataIfNecessary();
		assertFalse(VaccinationsApiManager.getInstance(true).getCurrentData().isEmpty());
		assertDoesNotThrow(() -> {
			new VaccinationUpdateBuilder().setChatId("aChatId")
					.setContentData(VaccinationsApiManager.getInstance(true).getCurrentData()).build();
		});
	}

}
