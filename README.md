# Techniki Kompilacji

Student:

```
Bartłomiej Krawczyk
```

Numer indeksu:

```
310774
```

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

## Funkcjonalne

- pozwala na "typowe" operacje
- udostępnia typ słownika
- możliwe są zapytania w stylu LINQ
- iterowanie po słowniku może przyjmować funkcję, która ustali kolejność iteracji
- interpreter nie kończy się błędem, gdy interpretowany program kończy się błędem

## Niefunkcjonalne

[//]: # (TODO: write something meaningful eg. security - protection before buffer overflow)

- wydajność?
- szybkość?
- bezpieczeństwo?
- wsparcie?
- użyteczność?

# Typy Danych

**Proste typy danych:**

- `int`
	- liczby całkowite z zakresu `[-2147483648; 2147483647]`
	- np. `123`, `4`, `56`
- `double`
	- liczby zmienno przecinkowe z zakresu `(-2147483649; 2147483648)` mieszczące się na 64 bitach
	- np. `123.456`, `0.78`, `9.0`
- `boolean`
	- wartość reprezentująca prawdę lub fałsz
	- np. `true`, `false`

Dla typów prostych są zdefiniowane operacje matematyczne oraz operacje logiczne / porówniania.

**Złożone typy danych:**

- `String`
	- sekwencja znaków, reprezentująca tekst
	- np. `'Hello, World!''`, `"Olek pisze w papierowym zeszycie,\na Ala ma kota o imieniu \"Puszek\"!""`
- `Map<key, value>`
	- słownik klucz - wartość
	- `key` oraz `value` to mogą być dowolne inne typy występujące w języku
	- np. `{"a": 1, "b": 2, "c": 3}`
- `Tuple<value, ...>`
	- nie-mutowalna krotka
	- krotka może składać się z wielu wartości różnego typu
	- np. `value1 AS name1, value2 AS name2`
- `Comparator<value>`
	- funkcja, która pozwala na porównanie dwóch wartości
	- zwraca 1 w przypadku, gdy pierwsza wartość jest większa
	- zwraca 0 w przypadku, gdy obie wartości są równe
	- zwraca -1 w przypadku, gdy druga z wartości jest większa
- `Iterable<value, ...>`
	- struktura służąca do iterowania po krotkach, które są rezultatem zapytania
	- np. `SELECT value FROM map`

## "Interfejs" typów złożonych oraz wbudowane funkcje:

**String**

```
class String {
	fun concat(other: String): String;
}

fun print(messsage: String);
```

**Comparator<value>**

```
class Comparator<V> {
	fun compare(this:V, other:V): int;
}

Comparator<int> compareValues = fun(this: int, other: int): int{
	return 1;
}
```

**Tuple<value, ...>**

```
class Tuple<V1, V2, V3, ...>{
	fun $name1: V1;
	fun $name2: V2;
	fun $name3: V3;
	...
}

Tuple<String, int, double> tuple = (
	value1 AS name1,
	value2 AS name2,
	value3 AS name3,
);
```

**Map<key, value>**

```
class Map<K, V> {
	fun operator[](key: K): V;
	fun put(key: K, value: V);
	fun contains(key: K): boolean;
	fun remove(key: K);
	fun iterable(): Iterable<K, V>;
	fun iterable(comparator: Comparator<K, V>): Iterable<K, V>;
}

fun operator{}: Map<key, value>;

Map<String, int> map = {
	"id_1": 1,
	"id_2": 2
};
```

**Iterable<value, ...>**

```
class Iterable<VALUE, ...> {
    fun hasNext(): boolean;
    fun next(): Tuple<VALUE, ...>;
}

Map<String, int> map = prepareMap(); // user defined function
Iterable<String, int> query =
	SELECT
		entry.key AS key,
		entry.value AS value
	FROM map AS entry
	WHERE entry.key != "abc" AND entry.value > 0
	ORDER BY entry.value, entry.key;
```

# Semantyka

[//]: # (Opis wyrażeń - hmm co tu napisać xD)

**Operacje arytmetyczne:**

- `+` - dodawanie
- `-` - odejmowanie
- `*` - mnożenie
- `/` - dzielenie

[//]: # (Mamy pamiętać o priorytetach oraz łączności operatorów)

**Złożenie wyrażeń algebraicznych:**

```groovy
int value = first * (second + third);
```

**Operacje logiczne:**

- `and` - koniunkcja
- `or` - alternatywa
- `not` - negacja

**Złożenie wyrażeń logicznych:**

```
boolean expression = first and (second or third);
```

**Rzutowanie zmiennych liczbowych:**
Wspieram rzutowanie wartości liczbowych:

- int na double - bez stratna
- double na int - zaokrąglenie w dół (obcięcie cyfr po przecinku)

```
int a = 1;
double b = 2.5;
double value = (double) a + b;
```

**Widoczność zmiennych:**

- zmienne widoczne jedynie w bloku w którym zostały powołane

```groovy
fun main() {
	if (true) {
		int a = 1;
	}
	int b = a; // błąd - `a` nie widoczne poza blokiem
}
```

**Instrukcje warunkowe:**

- instrukcja warunkowa `if`:

```
boolean expression = 2 + 2 == 4;

if expression {
	// do something
}

if expression doSomething();
```

- konstrukcja `if-else`:

```
boolean expression = 2 + 2 == 4;

if expression {
	// do something
} else {
	// do something else
}
```

- złożenie `if-else`:

```
boolean first = 2 + 2 == 4;
boolean second = false;

if first {
	// do something
} else if second {
	// do something else on condition
}
```

**Pętla warunkowa:**

```
boolean expression = true;
while expression {
	// do something
}
```

**Iteracja po iterowalnych strukturach:**

```groovy
Map<String, String> map = {};
for (Tuple<String, String> entry : map) {
	// do something
}
```

**Funkcje:**

- parametryzowana

```groovy
fun add(first: int, second: int): int {
	return first + second;
} 
```

- bez zwracania wartości

```groovy
fun log(message: String) {
	print(message);
} 
```

- bez argumentowa

```groovy
fun getOne(): int {
	return 1;
}
```

**Przypisanie wartości zmiennej:**

```groovy
int a = 1;
int b = 2;
int tmp = a;
a = b;
b = tmp;
```

Semantyka przekazywania argumentów do funkcji:

- typy proste przekazywane przez wartość
- w przypadku typów złożonych przekazywana jest wartość referencji do obiektu

Semantyka obsługi zmiennych:

- typowanie statyczne
	- typy zmiennym są nadawane w czasie kompilacji, poprzez deklarację.
- typowanie silne
	- każde wyrażenie ma ustalony typ i nie można go używać w kontekście przeznaczonym dla innych typów
- mutowalność
	- do zmiennych może być przypisywana nowa wartość, tego samego typu

**Rekursywność wywoływania funkcji:**

- funkcje mogą być wołane rekursywnie (z ograniczeniem do maksymalnego zagłębienia)

```
fun fibbonaci(n: int): int {
	if n == 0 return 0;
	if n == 1 return 1;
	return fibbonaci(n - 1) + fibbonaci(n - 2);
}
```

**Komentarze:**

```groovy
// Komentarz jedno-liniowy
```

```groovy
/*
	Komentarz wielo-liniowy
 */
```

# Formalna specyfikacja

# Gramatyka - Składnia (EBNF) realizowanego języka

## Symbole terminalne

[//]: # (Te obsługiwane przez lekser)

[//]: # (Wyrażenia regularne)

```
// Wyrażenia regularne
letter                  = [a-zA-Z]; (* także inne znaki krajowe *)
non_zero_digit          = [1-9];
digit                   = [0-9];
zero                    = "0";
character               = ?;
relation_operator       = "<" | "<=" | "==" | ">" | ">=" | "!="
addition_operator       = "+" | "-";
multiplication_operator = "*" | "/";

// EBNF
INTEGER                 = zero
                        | non_zero_digit, {digit};
FLOATING_POINT          = INTEGER, ".", digit, {digit};
NUMBER                  = INTEGER
                        | FLOATING_POINT;

IDENTIFIER              = letter, {letter | digit};

CHARACTERS              = {character};
STRING_DOUBLE_QUOTE     = "'", CHARACTERS, "'";
STRING_DOUBLE_QUOTE     = '"', CHARACTERS, "'";

STRING                  = STRING_DOUBLE_QUOTE
                        | STRING_SINGLE_QUOTE;

COMMENT_SINGLE_LINE     = "//", CHARACTERS , "\n";
COMMENT_MULTI_LINE      = "/*", CHARACTERS , "*/";

COMMENT                 = COMMENT_SINGLE_LINE
                        | COMMENT_MULTI_LINE;

SIMPLE_TYPE             = "int" 
                        | "double" 
                        | "boolean" 
                        | "String";

COMPLEX_TYPE            = "Map"
                        | "Comparator"
                        | "Iterable"
                        | "Tuple";
```

## Symbole złożone

[//]: # (Te obsługiwane przez parser)

[//]: # (EBNF)

[//]: # (TODO: reduce types to beginning with the same token)

```
TYPE_DECLARATION        = SIMPLE_TYPE
                        | COMPLEX_TYPE, "<", TYPE_DECLARATION, {",", TYPE_DECLARATION} ,">";
                        
EXPLICIT_CAST           = "(", ("int" | "double"), ")", FACTOR;

FACTOR                  = "(", ARITHMETIC_EXPRESSION ")"
                        | ["-"], NUMBER
                        | IDENTIFIER
                        | FUNCTION_CALL
                        | METHOD_CALL
                        | EXPLICIT_CAST;

TERM                    = FACTOR, {multiplication_operator, FACTOR};

ARITHMETIC_EXPRESSION   = TERM, {addition_operator, TERM};

LOGICAL_VALUE           = IDENTIFIER
                        | FUNCTION_CALL
                        | METHOD_CALL
                        | ARITHMETIC_EXPRESSION, relation_operator, ARITHMETIC_EXPRESSION;

LOGICAL_AND_EXPRESSION  = LOGICAL_VALUE, {"and", LOGICAL_VALUE};

LOGICAL_OR_EXPRESSION   = LOGICAL_AND_EXPRESSION, {"or", LOGICAL_AND_EXPRESSION};


LOGICAL_EXPRESSION      = LOGICAL_OR_EXPRESSION
                        | "not", LOGICAL_EXPRESSION
                        | "(", LOGICAL_EXPRESSION, ")";


ARGUMENT_DECLARATION    = IDENTIFIER, ":", TYPE_DECLARATION;
ARGUMENT_LIST           = ARGUMENT_DECLARATION, {",", ARGUMENT_DECLARATION};
FUNCTION_DECLARATION    = "fun", IDENTIFIER, "(", [ARGUMENT_LIST], ")", [":", TYPE_DECLARATION], BLOCK;

LAMBDA_DECLARATION      = "fun", "(", [ARGUMENT_LIST], ")", [":", TYPE_DECLARATION], BLOCK; (* Reconsider this*)

FUNCTION_CALL           = IDENTIFIER, "(", [EXPRESSION, {",", EXPRESSION}], ")";

TUPLE_CALL              = EXPRESSION, ".", IDENTIFIER;

METHOD_CALL             = EXPRESSION, ".", FUNCTION_CALL
                        | EXPRESSION, "[", EXPRESSION, "]";

EXPRESSION              = IDENTIFIER
                        | ARITHMETIC_EXPRESSION
                        | LOGICAL_EXPRESSION
                        | FUNCTION_CALL
                        | METHOD_CALL
                        | TUPLE_CALL
                        | SELECT_EXPRESSION
                        | TUPLE_EXPRESSION
                        | MAP_EXPRESSION
                        | "(", EXPRESSION, ")";

IF_STATEMENT            = "if", LOGICAL_EXPRESSION, STATEMENT,
                          ["else", STATEMENT];

WHILE_STATEMENT         = "while", LOGICAL_EXPRESSION, STATEMENT;

RETURN_STATEMENT        = "return", EXPRESSION, ";";

FOR_EACH_EXPRESSION     = TYPE_DECLARATION, IDENTIFIER, ":", EXPRESSION
                        | "(", TYPE_DECLARATION, IDENTIFIER, ":", EXPRESSION, ")";
FOR_STATEMENT           = "for", FOR_EACH_EXPRESSION, STATEMENT;

DECLARATION             = TYPE_DECLARATION, IDENTIFIER, ["=", EXPRESSION], ";";

ASSIGNMENT              = IDENTIFIER, "=", EXPRESSION, ";";

STATEMENT               = IF_STATEMENT
                        | WHILE_STATEMENT
                        | FOR_STATEMENT
                        | DECLARATION
                        | ASSIGNMENT
                        | EXPRESSION, ";"
                        | BLOCK
                        | ";";

BLOCK                   = "{", {STATEMENT} "}";

MAP_ELEMENT             = EXPRESSION, ":", EXPRESSION;
MAP_EXPRESSION          = "{", [MAP_ELEMENT, {",", MAP_ELEMENT}];

TUPLE_ELEMENT           = EXPRESSION, "AS", IDENTIFIER
TUPLE_EXPRESSION        = TUPLE_ELEMENT, {",", TUPLE_ELEMENT};

ORDER_BY_EXPRESSION     = EXPRESSION, ["ASCENDING" | "DESCENDING"];

SELECT_EXPRESSION       = "SELECT", TUPLE_EXPRESSION, "FROM", TUPLE_ELEMENT,
                          {"JOIN", TUPLE_ELEMENT, ["ON", EXPRESSION]},
                          ["WHERE", EXPRESSION],
                          ["GROUP", "BY", EXPRESSION, {",", EXPRESSION}, ["HAVING", EXPRESSION]],
                          ["ORDER", "BY", ORDER_BY_EXPRESSION, {"," ORDER_BY_EXPRESSION}];
```

## Symbol startowy

```
PROGRAM = {COMMENT | FUNCTION_DECLARATION | DECLARATION | ";"};
```

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

# Sposób uruchomienia

Interpreter języka można uruchomić przy pomocy przygotowanego skryptu: `interpreter`

Przykładowe uruchomienie oraz rezultat:

```bash
$ ./interpreter --help
Usage: ./interpreter [OPTION] [FILE]
    -h --help               Display this message
    -c --clean              Re-build project from scratch before running application
    -i --identifier [VALUE] Set the maximum number of characters in a identifier
                            Default: 100
    -s --string [VALUE]     Set the maximum number of characters in a string
                            Default: 1000
    -e --exception [VALUE]  Set the maximum number of exceptions before stopping execution of a program
                            Default: 500
$ ./interpreter hello_world_program.txt
Hello, World!
```

Wymaganiem, aby uruchomić skrypt jest posiadanie zainstalowanego języka Java w wersji 17.

Interpreter wraz z bibliotekami jest pakowany do jednego "Fat Jar-a", przez co możliwe jest uruchomienie programu także za pomocą programu `java`:

```bash
$ java -jar "$FAT_JAR" "$FILE"
```

# Przykładowe programy

## Program hello world

**Wejście**

```groovy
fun main() {
	print("Hello, World!");
}
```

**Rezultat**

```markdown
Hello, World!
```

[//]: # (## Program TODO)

[//]: # (TODO: Copy already written programs and describe them)

# Realizacja

## Moduły

1. [Analizator leksykalny](./src/main/java/org/example/lexer)
1. [Analizator składniowy](./src/main/java/org/example/parser)
1. [Interpreter](./src/main/java/org/example/interpreter)
1. [Obsługa błędów](./src/main/java/org/example/error)

## Obiekty

## Interfejsy

**Analizator leksykalny:** [lexer](./src/main/java/org/example/lexer/Lexer.java)

```java
public interface Lexer {
	Token nextToken();
}
```

**Analizator semantyczny:** [parser](./src/main/java/org/example/parser/Parser.java)

```java
public interface Parser {
	Expression nextExpression();
}
```

**Interpreter:** [interpreter](./src/main/java/org/example/interpreter/Interpreter.java)

```java
public interface Interpreter {
	// TODO: implement me!
}
```

**Obsługa błędów:** [error handler](./src/main/java/org/example/error/ErrorHandler.java)

```java
public interface ErrorHandler {
	void handleLexerException(LexerException exception);

	void handleParserException(ParserException exception);

	void showExceptions(Reader reader) throws IOException;
}
```

## Tokeny

Obsługiwane typy tokenów: [token types](./src/main/java/org/example/token/TokenType.java)

```
END_OF_FILE,

FUNCTION_DEFINITION("fun"),
RETURN("return"),
WHILE("while"),
FOR("for"),
IF("if"),
ELSE("else"),

SELECT("select", false),
FROM("from", false),
JOIN("join", false),
ON("on", false),
WHERE("where", false),
GROUP("group", false),
HAVING("having", false),
ORDER("order", false),
BY("by", false),
ASCENDING("ascending", false),
DESCENDING("descending", false),

AS("as", false),

OPEN_CURLY_PARENTHESES("{"),
OPEN_ROUND_PARENTHESES("("),
OPEN_SQUARE_PARENTHESES("["),
CLOSED_CURLY_PARENTHESES("}"),
CLOSED_ROUND_PARENTHESES(")"),
CLOSED_SQUARE_PARENTHESES("]"),

SEMICOLON(";"),
COLON(":"),
COMMA(","),
DOT("."),

AND("and"),
NOT("not"),
OR("or"),

EQUALITY("=="),
INEQUALITY("!="),
GREATER(">"),
LESS("<"),
GREATER_EQUAL(">="),
LESS_EQUAL("<="),

EQUALS("="),

PLUS("+"),
MINUS("-"),
TIMES("*"),
DIVIDE("/"),

SINGLE_LINE_COMMENT("//", "\n"),
MULTI_LINE_COMMENT("/*", "*/"),

IDENTIFIER,
INTEGER_CONSTANT,
FLOATING_POINT_CONSTANT,
STRING_DOUBLE_QUOTE_CONSTANT("\"", "\""),
STRING_SINGLE_QUOTE_CONSTANT("'", "'"),

BOOLEAN_TRUE("true"),
BOOLEAN_FALSE("false"),

INT("int"),
DOUBLE("double"),
BOOLEAN("boolean"),

STRING("String"),

MAP("Map"),
ITERABLE("Iterable"),
TUPLE("Tuple"),
COMPARATOR("Comparator"),
;
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

Testy jednostkowe charakteryzują się tym, że zamiast docelowego obiektu wstawiam mock obiektu i upewniam się, że wstawiony obiekt zwróci dokładnie to
czego oczekiwaliśmy.

Testy integracyjne polegają na tym

[//]: # (TODO: Copy here test cases 1:1 when ready)

# Biblioteki

- **Vavr core** - to biblioteka dla Javy, która pomaga zmniejszyć ilość kodu oraz zwiększyć niezawodność.
- **Project Reactor** - biblioteka umożliwiająca nie blokującą, wydajną obsługę programu.
- **Apache Commons Lang** - to biblioteka, która udostępnia pre-definiowane operacje na Stringach.
- **Simple Logging Facade for Java** - biblioteka, która umożliwia logowanie komunikatów w jednakowy sposób nie zależny od implementacji.
- **Project Lombok** - procesor anotacji, który generuje podstawową implementację na podstawie zdefiniowanych anotacji w kodzie.
- **Spock Framework** - to framework do testowania aplikacji Java i Groovy. Wyróżnia go bardzo wyrazisty język specyfikacji. Umożliwia proste
  mockowanie oraz stubowanie funkcjonalności.
