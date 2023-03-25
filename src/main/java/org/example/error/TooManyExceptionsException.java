package org.example.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class TooManyExceptionsException extends RuntimeException {

	public TooManyExceptionsException() {
		super("You have reached the maximum number of exceptions allowed! Please fix your program before running interpreter again!");
	}
}
