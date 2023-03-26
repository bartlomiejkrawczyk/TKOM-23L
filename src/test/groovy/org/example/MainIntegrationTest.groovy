package org.example

import spock.lang.Specification


class MainIntegrationTest extends Specification {


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