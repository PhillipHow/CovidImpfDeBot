package de.philliphow.covidimpfde.strings.messagegenerators;

public class MessageFooter implements MessageStringGenerator {

	private static final String DATA_SOURCE = "https://impfdashboard.de/";
	private static final String GITHUB_LINK = "https://stackoverflow.com/questions/50770235/how-to-use-markdown-in-telegram-i-want-to-send-message-with-hyperlink";

	private final boolean isSubbed;

	public MessageFooter(boolean isSubbed) {
		this.isSubbed = isSubbed;
	}

	@Override
	public String getTextAsMarkdown() {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("[Datenquelle](%s) | [Github](%s) | /impf | /letztelieferung\n", DATA_SOURCE,
				GITHUB_LINK));

		if (!isSubbed) {
			sb.append("Tägliche Updates erhalten - /sub");
		} else {
			sb.append("Tägliches Update deabonnieren - /unsub");
		}

		sb.append("\n");

		return sb.toString();
	}

}
