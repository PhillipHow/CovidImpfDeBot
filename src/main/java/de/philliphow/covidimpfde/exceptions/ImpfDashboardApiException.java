package de.philliphow.covidimpfde.exceptions;

import java.io.IOException;

/**
 * Exception the is thrown if the data from the impfdashboard.de API could not
 * be reached or read
 * 
 * @author PhillipHow
 *
 */
@SuppressWarnings("serial")
public class ImpfDashboardApiException extends IOException {

	public ImpfDashboardApiException(Exception cause) {
		super(cause);
	}

}
