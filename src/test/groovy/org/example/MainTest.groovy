package org.example

import spock.lang.Specification

class MainTest extends Specification {

	def 'Sanity Check'() {
		given:
		var a = 2
		var b = 2
		expect:
		a + b == 4
		a - b != 4
	}
}
