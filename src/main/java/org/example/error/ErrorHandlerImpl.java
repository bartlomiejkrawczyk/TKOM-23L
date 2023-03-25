package org.example.error;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.example.lexer.PositionalReaderImpl;
import org.example.lexer.error.LexerException;
import org.example.parser.error.ParserException;
import reactor.core.publisher.Flux;

@Slf4j
public class ErrorHandlerImpl implements ErrorHandler {

	private static final int MAX_ERRORS = 500;

	private final List<LexerException> lexerExceptions = new ArrayList<>();
	private final List<ParserException> parserExceptions = new ArrayList<>();

	@Override
	public void showExceptions(Reader reader) throws IOException {
		var positionalReader = new PositionalReaderImpl(reader);
		var result = Flux.concat(
						Flux.fromIterable(lexerExceptions),
						Flux.fromIterable(parserExceptions)
				)
				.map(it -> Map.entry(it.getPosition(), it))
				.groupBy(Map.Entry::getKey)
				.flatMap(groupedExceptions ->
						groupedExceptions.map(Map.Entry::getValue)
								.collectList()
								.map(exceptions -> Map.entry(groupedExceptions.key(), exceptions))
				)
				.collectMap(Map.Entry::getKey, Map.Entry::getValue)
				.blockOptional()
				.orElse(Map.of());

		while (true) {
			var read = positionalReader.read();
			if (read == -1) {
				break;
			}

			var currentPosition = positionalReader.getPosition();
			var exceptions = result.getOrDefault(currentPosition, null);

			if (exceptions == null) {
				continue;
			}

			for (var exception : exceptions) {
				log.error(exception.getMessage());
			}
		}
	}

	@Override
	public void handleLexerException(LexerException exception) {
		lexerExceptions.add(exception);
		if (lexerExceptions.size() > MAX_ERRORS) {
			throw new TooManyExceptionsException();
		}
	}

	@Override
	public void handleParserException(ParserException exception) {
		parserExceptions.add(exception);
		if (parserExceptions.size() > MAX_ERRORS) {
			throw new TooManyExceptionsException();
		}
	}
}
