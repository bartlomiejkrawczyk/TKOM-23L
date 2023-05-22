package org.example.interpreter.error;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.token.Position;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public abstract class CriticalInterpreterException extends InterpreterException {

	private Position position;
	private List<String> contextStack;

	@Override
	public String getMessage() {
		return this + " - " + getClass().getName() + ":\n" + String.join("\n", contextStack);
	}
}
