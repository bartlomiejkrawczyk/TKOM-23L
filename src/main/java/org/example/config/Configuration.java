package org.example.config;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Configuration {


	protected static int getProperty(String property, int defaultValue) {
		var value = System.getProperty(property);
		try {
			return Optional.ofNullable(value)
					.map(Integer::parseInt)
					.orElse(defaultValue);
		} catch (IllegalArgumentException exception) {
			log.error("Could not parse value for provided property {}. Interpreter will default to {}", property, defaultValue);
			return defaultValue;
		}
	}
}
