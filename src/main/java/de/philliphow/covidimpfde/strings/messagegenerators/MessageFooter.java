package de.philliphow.covidimpfde.strings.messagegenerators;

import de.philliphow.covidimpfde.strings.StrUtil;

public class MessageFooter implements MessageStringGenerator {

	private static final String DATA_SOURCE = "https://impfdashboard.de/";
	private static final String GITHUB_LINK = "https://github.com/PhillipHow/CovidImpfDeBot";

	private final boolean isSubbed;
	private final int subCount;

	public MessageFooter(boolean isSubbed, int subCount) {
		this.isSubbed = isSubbed;
		this.subCount = subCount;
	}

	@Override
	public String getTextAsMarkdown() {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("[Datenquelle](%s) | [Github](%s) | /impf | /lieferung\n", DATA_SOURCE,
				GITHUB_LINK));

		if (!isSubbed) {
			sb.append("Tägliche Updates erhalten - /sub ");
		} else {
			sb.append("Tägliches Update deabonnieren - /unsub ");
		}
		
		sb.append("\n");
		
		if (subCount != -1)
			sb.append(String.format("(Aktuelle Bot-Abonnenten: *%s*)", StrUtil.number(subCount)));

		return sb.toString();
	}

}
