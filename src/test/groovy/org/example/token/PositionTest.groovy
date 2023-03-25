package org.example.token

import spock.lang.Specification


class PositionTest extends Specification {

	def 'Should compare positions correctly'() {
		expect:
		first.compareTo(second) == result

		where:
		first              | second             || result
		new Position(0, 0) | new Position(0, 0) || 0
		new Position(0, 1) | new Position(0, 0) || 1
		new Position(1, 0) | new Position(0, 0) || 1
		new Position(0, 0) | new Position(1, 0) || -1
		new Position(0, 0) | new Position(0, 1) || -1
	}
}