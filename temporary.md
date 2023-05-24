# Opis zakładanej funkcjonalności

[//]: # (TODO: refactor this description)

Język z wbudowanym typem słownika. Możliwe są podstawowe operacje na słowniku
(dodawanie, usuwanie, wyszukiwanie elementów wg klucza, sprawdzanie, czy dany
klucz znajduje się w słowniku itd.), iterowanie po elementach zgodnie z zadaną
kolejnością oraz wykonywanie na słowniku zapytań w stylu LINQ.
Kolejność, w jakiej zwracane są elementy podczas iterowania, jest określana
za pomocą funkcji przekazywanej jako dodatkowy parametr pętli.

**Wariant:** Statycznie typowany

**Typowanie:** Silne typowanie

**Język realizacji interpretera:** Java 17

# Wymagania

[//]: # (TODO: refactor this)

- pozwala na "typowe" operacje
- udostępnia typ słownika
- możliwe są zapytania w stylu LINQ
- iterowanie po słowniku może przyjmować funkcję, która ustali kolejność iteracji
- interpreter nie kończy się błędem, gdy interpretowany program kończy się błędem

# Formalna specyfikacja plików / strumieni wejściowych

# Formalna specyfikacja danych konfiguracyjnych

Wszystkie konfigurowalne dane są dostępne z poziomu [interpretera](./interpreter).

Aby zmodyfikować konfigurację należy ustawić odpowiednie wartości za pomocą flag interpretera.

# Obsługa błędów

- Źródła
	- użytkownikowi wyświetlony zostaje odpowiedni komunikat
	- program interpretera kończy się
- Lexera
	- generalnie - przekazanie błędu do obsługi przez wydzielony obiekt
	- długie identyfikatory / komentarze / stringi - przycięcie identyfikatora do maksymalnej długości
	- długa liczba całkowita - podział liczby na dwie kolejne liczby
	- długa część ułamkowa liczby zmienno przecinkowej - zignorowanie nadmiarowych cyfr po przecinku
	- nierozpoznany znak - pominięcie go
- Parsera
	- przekazanie błędu do obsługi przez wydzielony obiekt
- Interpretera
	- TODO
- Czasu wykonania
	- TODO

TODO: Jakie inne błędy będą tolerowane, jak radzę sobie z błędami

## Komunikaty

Błędy występujące w kodzie są zbierane i ostatecznie wyświetlane użytkownikowi.

Każda linia zawierająca błędy jest najpierw printowana z numerem lini,
a następnie w kolejnych liniach wyświetlane są błędy znalezione przez program.

**Błędny kod:**

```groovy
String 🙁 = "Ala ma kota";
```

**Rezultat:**

```bash
[INFO]    1: String đź™? = "Ala ma kota";
[ERROR] Unexpected character ™ at position line 1, character 10
[ERROR] Unexpected character ? at position line 1, character 11
```

# Realizacja

## Moduły

1. [Analizator leksykalny](./src/main/java/org/example/lexer)
1. [Analizator składniowy](./src/main/java/org/example/parser)
1. [Interpreter](./src/main/java/org/example/interpreter)
1. [Obsługa błędów](./src/main/java/org/example/error)

## Obiekty

**Obsługa błędów:** [error handler](./src/main/java/org/example/error/ErrorHandler.java)

```java
public interface ErrorHandler {
	void handleLexerException(LexerException exception);

	void handleParserException(ParserException exception);

	void showExceptions(Reader reader) throws IOException;
}
```

## Sposób przetwarzania - w poszczególnych komponentach

TODO

## Wykorzystywane struktury danych

TODO - do jakich struktur danych trafią określone obiekty (opis formalny / tekstowy)

[//]: # (TODO: napisz w jakich strukturach trzymam - rzeczy generowane przez parser - abstract syntax tree)

## Formy pośrednie

TODO

# Testowanie

Testy do każdego modułu wydzieliłem testy jednostkowe `*Test` oraz testy integracyjne `*IntegrationTest`.

Testy jednostkowe charakteryzują się tym, że zamiast docelowego obiektu wstawiam mock obiektu i upewniam się, że
wstawiony obiekt zwróci dokładnie to
czego oczekiwaliśmy.

Testy integracyjne polegają na tym

[//]: # (TODO: Copy here test cases 1:1 when ready)

