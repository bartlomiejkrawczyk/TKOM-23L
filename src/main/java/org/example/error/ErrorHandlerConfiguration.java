package org.example.error;

import lombok.experimental.UtilityClass;
import org.example.config.Configuration;

@UtilityClass
public class ErrorHandlerConfiguration extends Configuration {

	public static final int MAX_ERRORS = getProperty("max.exceptions", 500);
}
