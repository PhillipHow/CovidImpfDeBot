package de.philliphow.covidimpfde.api.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.pmw.tinylog.Logger;

/***
 * An abstraction for a parsed tsv (tab separated values) row. One row consists
 * of multiple raw String values that each are identified by a String key
 * (defined in the first line of a tsv file)
 * 
 * @author PhillipHow
 *
 */
public class AbstractTsvRow {

	private Map<String, String> values = new HashMap<>();

	/***
	 * Constructs an AbstractTsvRow by parsing a raw tsv data row in combination
	 * with the raw file header row (first row which contains the identifiers for
	 * the data row values). Both strings need to have an equal number of individual
	 * tokens.
	 * 
	 * @param rowAsString A tsv row of data records
	 * @param headerRow   A tsv row of header values
	 */
	public AbstractTsvRow(String rowAsString, String headerRow) {
		Scanner scanRow = new Scanner(rowAsString);
		Scanner scanTitle = new Scanner(headerRow);

		while (scanTitle.hasNext()) {
			this.values.put(scanTitle.next(), scanRow.next());
		}

		scanRow.close();
		scanTitle.close();
	}

	/***
	 * Constructs a AbstractTsvRow from a given key value map. The keys are the
	 * entries in the header row of a tsv file.
	 * 
	 * @param values
	 */
	public AbstractTsvRow(Map<String, String> values) {
		if (values.size() == 0)
			Logger.warn("AbstractTsvRow with no elements constructed!");
		this.values.putAll(values);
	}

	protected boolean hasField(String key) {
		return values.containsKey(key);
	}

	protected String getRawStringField(String key) {
		return values.get(key);
	}

	protected int getIntField(String key) {
		return Integer.parseInt(this.getRawStringField(key));
	}

	protected double getDoubleField(String key) {
		return Double.parseDouble(this.getRawStringField(key));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.values.forEach((k, v) -> sb.append(k + ":" + v + " "));
		return sb.toString();
	}

}
