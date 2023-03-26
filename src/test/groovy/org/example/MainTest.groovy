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

	def 'Should parse working programs without exceptions'() {
		when:
		Main.main("./src/main/resources/" + file)

		then:
		noExceptionThrown()

		where:
		file << ["query.txt", "test.txt"]
	}

	def 'Should parse not working program without exceptions'() {
		when:
		Main.main("./src/test/resources/" + file)

		then:
		noExceptionThrown()

		where:
		file << ["error.txt"]
	}
}
