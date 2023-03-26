package org.example.config;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Configuration {


	protected static int getProperty(String property, int defaultValue) {
		var value = System.getProperty(property);
		return Optional.ofNullable(value)
				.map(Integer::parseInt)
				.orElse(defaultValue);
	}
}
