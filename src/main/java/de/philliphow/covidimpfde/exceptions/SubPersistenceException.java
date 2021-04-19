package de.philliphow.covidimpfde.exceptions;

import java.io.IOException;

/**
 * Exception that is thrown if the {@code SubListPersistence} is not able to
 * read or write the list of subs
 * 
 * @author PhillipHow
 *
 */
@SuppressWarnings("serial")
public class SubPersistenceException extends IOException {

	public SubPersistenceException(IOException cause) {
		super(cause);
	}

}
