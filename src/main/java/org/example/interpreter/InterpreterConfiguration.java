package org.example.interpreter;

import org.example.config.Configuration;

public class InterpreterConfiguration extends Configuration {

	public static final Integer MAX_STACK_SIZE = getProperty("max.stack.size", 100);
}
