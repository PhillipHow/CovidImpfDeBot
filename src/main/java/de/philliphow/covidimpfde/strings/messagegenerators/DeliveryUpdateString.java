package de.philliphow.covidimpfde.strings.messagegenerators;

import de.philliphow.covidimpfde.api.models.DeliveryDataRow;
import de.philliphow.covidimpfde.api.models.Vaccine;
import de.philliphow.covidimpfde.logic.DeliveryDataInterpretation;
import de.philliphow.covidimpfde.strings.StrUtil;

/**
 * Class for constructing a vaccination update String from vaccination date.
 * Note that this class only does string formatting, any calculations on the
 * data are done by a {@link DeliveryDataInterpretation}
 * 
 * @author PhillipHow
 *
 */
public class DeliveryUpdateString implements MessageStringGenerator {

	/**
	 * How many deliveries should appear in the lastDeliveries section
	 */
	private final static int LAST_DELIVERIES_SHOW_N = 10;
	/**
	 * the data interpreter containing the vaccination data
	 */
	private final DeliveryDataInterpretation data;
	/**
	 * true if the user getting the update has subscribed to daily Updates
	 */
	private final boolean isSubbed;

	public DeliveryUpdateString(DeliveryDataInterpretation deliveryDataInterpretation, boolean isSubbed) {
		this.data = deliveryDataInterpretation;
		this.isSubbed = isSubbed;
	}

	@Override
	public String getTextAsMarkdown() {

		StringBuilder sb = new StringBuilder();
		sb.append(getHeader());
		sb.append(getVaccinesOverview());
		sb.append(getBiggestDelivery());
		sb.append(getLastDeliveries());
		sb.append(getFooter());

		return sb.toString();
	}

	private String getHeader() {
		String deliveryDate = StrUtil.date(data.getLatestDelivery().getDate());
		String numberOfDoses = StrUtil.number(data.getLatestDelivery().getDoses());
		String vaccine = data.getLatestDelivery().getVaccine().getHumamReadableName();

		return String.format(
				"*Neuer Stoff ist da!*\n----------------------\nEine neue Lieferung wurden registriert - *%s* wurden *%s* Dosen von *%s* geliefert!\n\n",
				deliveryDate, numberOfDoses, vaccine);
	}

	private String getVaccinesOverview() {
		StringBuilder sb = new StringBuilder();
		data.getDosesDeliveredByVaccine().entrySet().forEach(vaccineDeliveries -> sb
				.append(getVaccineUpdate(vaccineDeliveries.getKey(), vaccineDeliveries.getValue())));

		String vaccineShareString = sb.toString();
		String totalDoses = StrUtil.number(data.getTotalDeliveredDoses());

		return String.format("%sInsgesamt -- *%s*\n\n", vaccineShareString, totalDoses);

	}

	private String getVaccineUpdate(Vaccine vaccine, int doses) {
		String vaccineName = vaccine.getHumamReadableName();
		String totalDoses = StrUtil.number(doses);
		String share = StrUtil.percent(data.getShareByVaccine().get(vaccine));

		return String.format("%s: *%s* (*%s*)\n", vaccineName, totalDoses, share);
	}

	private String getBiggestDelivery() {
		DeliveryDataRow biggestDelivery = data.getBiggestDelivery();
		String deliveryVaccineName = biggestDelivery.getVaccine().getHumamReadableName();
		String deliveryDoses = StrUtil.number(biggestDelivery.getDoses());
		String date = StrUtil.date(biggestDelivery.getDate());

		return String.format("Die größte Lieferung bis jetzt --\n*%s* Dosen %s von *%s*\n\n", deliveryDoses, date,
				deliveryVaccineName);
	}

	private String getLastDeliveries() {
		StringBuilder sb = new StringBuilder();
		data.getLastNDeliveries(LAST_DELIVERIES_SHOW_N).forEach(delivery -> sb.append(getOneDeliveryRow(delivery)));
		String deliveryListString = sb.toString();

		return String.format("Letzte Lieferungen --\n%s\n", deliveryListString);

	}

	private String getOneDeliveryRow(DeliveryDataRow delivery) {
		String doses = StrUtil.number(delivery.getDoses());
		String vaccine = delivery.getVaccine().getHumamReadableName();
		String date = StrUtil.date(delivery.getDate());

		return String.format("*%s* von %s (*%s*)\n", doses, vaccine, date);
	}

	private String getFooter() {
		return new MessageFooter(isSubbed).getTextAsMarkdown();
	}

}
