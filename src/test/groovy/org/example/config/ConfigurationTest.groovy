package org.example.config

import org.example.lexer.LexerConfiguration
import spock.lang.Specification


class ConfigurationTest extends Specification {

	def 'Should handle errors while parsing properties correctly'() {
		given:
		System.setProperty(property, value)

		when:
		var result = LexerConfiguration.getProperty(property, 0)

		then:
		noExceptionThrown()
		result == 0

		where:
		property | value
		"abc"    | "abc"
		"123"    | "1.1.1"
	}
}