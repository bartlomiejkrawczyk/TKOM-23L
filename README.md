# Techniki Kompilacji

Student:

```
Bartłomiej Krawczyk - 310774
```

# Funkcjonalność Języka

Tworzony obecnie język programowania ma wbudowany typ danych - słownik,
który umożliwia przechowywanie i manipulowanie danymi w formie par klucz-wartość.
W tym języku możemy wykonywać podstawowe operacje na słowniku, takie jak dodawanie,
usuwanie, wyszukiwanie elementów na podstawie klucza oraz sprawdzanie, czy dany
klucz znajduje się w słowniku.

Ważnym aspektem języka jest możliwość iterowania po elementach słownika
w określonej kolejności. Ta kolejność jest ustalana za pomocą funkcji przekazywanej
jako dodatkowy parametr metody, dzięki czemu możemy kontrolować, w jakiej sekwencji
elementy zostaną zwrócone. To umożliwia bardziej precyzyjne zarządzanie
i przetwarzanie danych w słowniku.

Ponadto, język oferuje możliwość wykonywania zapytań w stylu LINQ (Language Integrated Query)
na słownikach. LINQ jest potężnym narzędziem programistycznym, które pozwala na wyszukiwanie,
filtrowanie i transformację danych w sposób deklaratywny. Dzięki integracji LINQ w języku,
możemy skorzystać z tych zaawansowanych technik przetwarzania danych bez konieczności
pisania dużej ilości kodu.

Język ten jest statycznie typowany. Oznacza to, że wszystkie typy danych muszą być zadeklarowane
przez programistę, co pozwala na wykrywanie błędów związanych z typowaniem.

Język ten posiada silne typowanie, co oznacza, że konwersje między różnymi typami danych wymagają
jawnego określenia i nie są wykonywane automatycznie. To zwiększa bezpieczeństwo
i czytelność kodu, eliminując niejednoznaczne zachowania i nieoczekiwane wyniki.

Interpreter dla tego języka programowania jest realizowany w języku Java 17.
Wybór Javy jako języka realizacji interpretera pozwala na korzystanie z bogatej
i rozwiniętej ekosystemu Javy oraz wykorzystanie silnego typowania i możliwości
statycznej analizy kodu.

# Konstrukcje językowe

**Operacje arytmetyczne:**

[Przykładowy program](src/test/resources/math.txt)

- `+` - dodawanie
- `-` - odejmowanie
- `*` - mnożenie
- `/` - dzielenie

Złożenie wyrażeń algebraicznych:

```groovy
int value = first * (second + third);
```

**Operacje logiczne:**

[Przykładowy program](src/test/resources/logic.txt)

- `and` - koniunkcja
- `or` - alternatywa
- `not` - negacja

Złożenie wyrażeń logicznych:

```
boolean expression = first and (second or third);
```

**Rzutowanie zmiennych liczbowych:**

[Przykładowy program](src/test/resources/explicit-cast.txt)

- int na double - bez stratna
- double na int - zaokrąglenie w dół (obcięcie cyfr po przecinku)

```
int a = 1;
double b = 2.5;
double value = (@double a) + b;
```

**Widoczność zmiennych:**

[Przykładowy program](src/test/resources/visibility.txt)

- zmienne widoczne jedynie w bloku w którym zostały powołane

```groovy
fun main() {
	if (true) {
		int a = 1;
	}
	int b = a; # błąd - ` a ` nie widoczne poza blokiem
}
```

**Instrukcje warunkowe:**

[Przykładowy program](src/test/resources/if.txt)

- instrukcja warunkowa `if`:

```
boolean expression = 2 + 2 == 4;

if expression {
	# do something
}

if expression doSomething();
```

- konstrukcja `if-else`:

```
boolean expression = 2 + 2 == 4;

if expression {
	# do something
} else {
	# do something else
}
```

- złożenie `if-else`:

```
boolean first = 2 + 2 == 4;
boolean second = false;

if first {
	# do something
} else if second {
	# do something else on condition
}
```

**Pętla warunkowa:**

[Przykładowy program](src/test/resources/while.txt)

```
boolean expression = true;
while expression {
	# do something
}
```

**Iteracja po iterowalnych strukturach:**

[Przykładowy program](src/test/resources/map.txt)

```groovy
Map<String, String> map = [];
for (Tuple<String, String> entry : map.iterator()) {
	# do something
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

- Przekazywanie argumentów do funkcji w tym języku odbywa się przez **referencję**
- Oznacza to, że obiekty są przekazywane do funkcji poprzez referencję do ich miejsca w pamięci,
  a nie przez kopiowanie ich wartości
- Dzięki temu zmiany dokonane na obiekcie wewnątrz funkcji są widoczne poza nią

Semantyka obsługi zmiennych:

- **typowanie statyczne**
	- typy zmiennych są ustalone i muszą być zadeklarowane przed użyciem
	- nie ma możliwości dynamicznego zmieniania typu zmiennej w trakcie działania programu
	- to zapewnia większą pewność typów i pomaga wykrywać błędy w trakcie kompilacji
- **typowanie silne**
	- każde wyrażenie ma ustalony typ i nie można go używać w kontekście przeznaczonym dla innych typów
	- nie ma automatycznej konwersji między różnymi typami danych
	- pomaga to uniknąć niejednoznaczności i nieoczekiwanych wyników
- **mutowalność**
	- do zmiennych może być przypisywana nowa wartość, pod warunkiem że jest tego samego typu

**Rekursywność wywoływania funkcji:**

[Przykładowy program](./src/test/resources/error-recursion-limit.txt)

- funkcje mogą być wywoływane rekursywnie
	- funkcja może bezpośrednio lub pośrednio wywołać samą siebie w trakcie swojego wykonywania
	- istnieje ustalone ograniczenie na maksymalną liczbę zagłębionych wywołań rekurencyjnych
	- ograniczenie to ma na celu zapobieganie nieskończonym rekursjom i przekroczeniu dostępnej pamięci
	- parametr określający to ograniczenie może być skonfigurowany i dostosowany do konkretnych wymagań programu lub
	  środowiska

```
fun fibbonaci(n: int): int {
	if n == 0 return 0;
	if n == 1 return 1;
	return fibbonaci(n - 1) + fibbonaci(n - 2);
}
```

**Komentarze:**

```py
# Komentarz jedno-liniowy
```

```groovy
/*
	Komentarz wielo-liniowy
 */
```

## Typy Danych

**Proste typy danych:**

- `int`
	- reprezentuje liczby całkowite z zakresu `[-2147483648; 2147483647]`
	- np. `123`, `4`, `56`
- `double`
	- reprezentuje liczby zmienno przecinkowe o podwójnej precyzji
	  z zakresu `(-2147483649; 2147483648)` mieszczące się na 64 bitach
	- np. `123.456`, `0.78`, `9.0`
- `boolean`
	- wartość reprezentująca prawdę lub fałsz
	- np. `true`, `false`

Dla typów prostych są zdefiniowane operacje matematyczne oraz operacje logiczne i porówniania.

**Złożone typy danych:**

- `String`
	- reprezentuje sekwencję znaków, czyli tekst
	- może zawierać dowolne znaki
	- np. `'Hello, World!''`, `"Olek pisze w papierowym zeszycie,\na Ala ma kota o imieniu \"Puszek\"!""`
- `Map<key, value>`
	- słownik, gdzie każdemu kluczowi przypisana jest wartość
	- zarówno klucz, jak i wartość mogą być dowolnego innego typu występującego w języku
	- np. `["a": 1, "b": 2, "c": 3]`
- `Tuple<value, ...>`
	- reprezentuje niezmienialną krotkę, czyli sekwencję wartości różnych typów
	- krotka może składać się z dowolnej liczby wartości
	- np. `|value1 AS name1, value2 AS name2|`
- `Comparator<value>`
	- funkcja, która pozwala na porównanie dwóch wartości
	- zwraca 1 w przypadku, gdy pierwsza wartość jest większa
	- zwraca 0 w przypadku, gdy obie wartości są równe
	- zwraca -1 w przypadku, gdy druga z wartości jest większa
- `Iterable<value>`
	- struktura służąca do iterowania po krotkach, które są rezultatem zapytania
	- np. `SELECT value FROM map`

## Pseudo-interfejs typów złożonych oraz funkcje wbudowane:

**String**

[Przykładowy program](./src/test/resources/hello.txt)

[Implementacja](./src/main/java/org/example/interpreter/model/value/StringValue.java)

```
fun print(messsage: String);
```

**Comparator<value>**

[Przykładowy program](./src/test/resources/map.txt)

[Implementacja](./src/main/java/org/example/interpreter/model/value/ComparatorValue.java)

```
class Comparator<V> {
	fun compare(this:V, other:V): int;
}

fun compareValues(this: int, other: int): int {
	return 1;
}
```

**Tuple<value, ...>**

[Przykładowy program](./src/test/resources/tuple.txt)

[Implementacja](./src/main/java/org/example/interpreter/model/value/TupleValue.java)

```
class Tuple<V1, V2, V3, ...> {
	fun $name1: V1;
	fun $name2: V2;
	fun $name3: V3;
	...
}

Tuple<String, int, double> tuple = |
	value1 AS name1,
	value2 AS name2,
	value3 AS name3,
|;
```

**Map<key, value>**

[Przykładowy program](./src/test/resources/map.txt)

[Implementacja](./src/main/java/org/example/interpreter/model/value/MapValue.java)

```
class Map<K, V> {
	fun operator[](key: K): V;
	fun put(key: K, value: V);
	fun contains(key: K): boolean;
	fun remove(key: K);
	fun iterable(): Iterable<K, V>;
	fun sortedIterable(comparator: Comparator<K, V>): Iterable<K, V>;
}

fun operator[]: Map<key, value>;

Map<String, int> map = [
	"id_1": 1,
	"id_2": 2
];
```

**Iterable<value, ...>**

[Przykładowy program 1](./src/test/resources/sql.txt)

[Przykładowy program 2](./src/test/resources/nestedSql.txt)

[Implementacja](./src/main/java/org/example/interpreter/model/value/IterableValue.java)

```
class Iterable<VALUE, ...> {
    fun hasNext(): boolean;
    fun next(): Tuple<VALUE, ...>;
}

Map<String, int> map = prepareMap(); # user defined function
Iterable<String, int> query =
	SELECT
		entry.key AS key,
		entry.value AS value
	FROM map AS entry
	WHERE entry.key != "abc" AND entry.value > 0
	ORDER BY entry.value, entry.key;
```

# Składnia

## Symbole terminalne

```
(* Wyrażenia regularne *)
letter                  = [a-zA-Z]; (* także inne znaki krajowe *)
non_zero_digit          = [1-9];
digit                   = [0-9];
zero                    = "0";
character               = ?;
relation_operator       = "<" | "<=" | "==" | ">" | ">=" | "!="
addition_operator       = "+" | "-";
multiplication_operator = "*" | "/";

(* EBNF *)
INTEGER                 = zero
                        | non_zero_digit, {digit};
FLOATING_POINT          = INTEGER, ".", digit, {digit};
NUMBER                  = INTEGER
                        | FLOATING_POINT;
                        
BOOLEAN                 = "true"
                        | "false";

IDENTIFIER              = letter, {letter | digit};

CHARACTERS              = {character};
STRING_DOUBLE_QUOTE     = '"', CHARACTERS, '"';
STRING_SINGLE_QUOTE     = "'", CHARACTERS, "'";

STRING                  = STRING_DOUBLE_QUOTE
                        | STRING_SINGLE_QUOTE;

COMMENT_SINGLE_LINE     = "#", CHARACTERS , "\n";
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

```
FUNCTION_DEFINITION    = "fun", IDENTIFIER, "(", [ARGUMENT_LIST], ")", [":", TYPE_DECLARATION], BLOCK;
ARGUMENT_LIST           = ARGUMENT_DECLARATION, {",", ARGUMENT_DECLARATION};
ARGUMENT_DECLARATION    = IDENTIFIER, ":", TYPE_DECLARATION;

TYPE_DECLARATION        = SIMPLE_TYPE
                        | COMPLEX_TYPE, "<", TYPE_DECLARATION, {",", TYPE_DECLARATION} ,">";

DECLARATION             = TYPE_DECLARATION, IDENTIFIER, "=", EXPRESSION, ";";

// STATEMENTS

BLOCK                   = "{", {STATEMENT} "}";

STATEMENT               = IF_STATEMENT
                        | WHILE_STATEMENT
                        | FOR_STATEMENT
                        | DECLARATION
                        | ASSIGNMENT_OR_IDENTIFIER_EXPRESSION
                        | RETURN_STATEMENT
                        | EXPRESSION, ";"
                        | BLOCK
                        | ";";

IF_STATEMENT            = "if", LOGICAL_EXPRESSION, STATEMENT,
                          ["else", STATEMENT];

WHILE_STATEMENT         = "while", LOGICAL_EXPRESSION, STATEMENT;

FOR_STATEMENT           = "for", "(", TYPE_DECLARATION, IDENTIFIER, ":", EXPRESSION, ")", STATEMENT;

ASSIGNMENT_OR_IDENTIFIER_EXPRESSION = [IDENTIFIER, "="], EXPRESSION, ";";

RETURN_STATEMENT        = "return", EXPRESSION, ";";

// EXPRESSION

EXPRESSION              = LOGICAL_EXPRESSION;

LOGICAL_EXPRESSION      = LOGICAL_OR_EXPRESSION;

LOGICAL_OR_EXPRESSION   = LOGICAL_AND_EXPRESSION, {"or", LOGICAL_AND_EXPRESSION};

LOGICAL_AND_EXPRESSION  = RELATION, {"and", RELATION};

RELATION                = ["not"], (BOOLEAN | ARITHMETIC_EXPRESSION, [relation_operator, ARITHMETIC_EXPRESSION]);

ARITHMETIC_EXPRESSION   = FACTOR, {addition_operator, FACTOR};

FACTOR                  = TERM, {multiplication_operator, TERM};

TERM                    = ["-"], (SIMPLE_TYPE | TUPLE_OR_METHOD_CALL);

SIMPLE_TYPE             = NUMBER | STRING;

TUPLE_METHOD_MAP_CALL   = SIMPLE_EXPRESSION, { (".", IDENTIFIER, [FUNCTION_ARGUMENTS] | "[", EXPRESSION,"]" ) };
FUNCTION_ARGUMENTS      = "(", [EXPRESSION, {",", EXPRESSION}], ")";

SIMPLE_EXPRESSION       = IDENTIFIER_OR_FUNCTION_CALL
                        | SELECT_EXPRESSION
                        | STANDALONE_TUPLE_EXP
                        | MAP_EXPRESSION
                        | EXPLICIT_CAST
                        | PARENTHESES_EXPRESSION;

IDENTIFIER_OR_FUNCTION_CALL = IDENTIFIER, [FUNCTION_ARGUMENTS];

SELECT_EXPRESSION       = "SELECT", TUPLE_EXPRESSION, "FROM", TUPLE_ELEMENT,
                          {"JOIN", TUPLE_ELEMENT, ["ON", EXPRESSION]},
                          ["WHERE", EXPRESSION],
                          ["GROUP", "BY", EXPRESSION, {",", EXPRESSION}, ["HAVING", EXPRESSION]],
                          ["ORDER", "BY", EXPRESSION, ["ASC" | "DESC"], {"," ORDER_BY_EXPRESSION}];

MAP_EXPRESSION          = "[", [EXPRESSION, ":", EXPRESSION, {",", EXPRESSION, ":", EXPRESSION}], "]";

STANDALONE_TUPLE_EXP    = "|", TUPLE_EXPRESSION, "|";
TUPLE_EXPRESSION        = TUPLE_ELEMENT, {",", TUPLE_ELEMENT};
TUPLE_ELEMENT           = EXPRESSION, "AS", IDENTIFIER;

EXPLICIT_CAST           = "@", TYPE_DECLARATION, EXPRESSION;

PARENTHESES_EXPRESSION  = "(", EXPRESSION, ")";
```

## Symbol startowy

```
PROGRAM = {COMMENT | FUNCTION_DEFINITION | DECLARATION | ";"};
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
    -f --function [VALUE]   Set the maximum number of nested function calls before stopping execution of program
                            Default: 100
$ ./interpreter hello.txt
Hello, World!
```

Wymaganiem, aby uruchomić skrypt jest posiadanie zainstalowanego języka Java w wersji 17.

Interpreter wraz z bibliotekami jest pakowany do jednego "Fat Jar-a",
przez co możliwe jest uruchomienie programu także za pomocą programu `java`:

```bash
$ java -jar "$FAT_JAR" "$FILE"
```

### Alternatywa

Możliwe jest także uruchomienia programu bezpośrednio z konsoli. W takim przypadku należy najpierw dodać do pliku
`shebang` z path do interpretera:

```shell
#!/mnt/c/Users/Public/Documents/TKOM/interpreter

fun main() {
    print("Hello, World!");
}
```

Uruchamianie następuje poprzez bezpośrednie wywołanie pliku z konsoli:

```shell
$ ./hello.txt
Hello, World!
```

## Konfiguracja

Język udostępnia kilka konfigurowalnych parametórw:

- maksymalna długość identyfikatorów wykorzystywanych w języku

```shell
    -i --identifier [VALUE] Set the maximum number of characters in a identifier
                            Default: 100
```

- maksymalna długość typu String

```shell
    -s --string [VALUE]     Set the maximum number of characters in a string
                            Default: 1000
```

- maksymalna ilość niekrytycznych błędów, po których należy zatrzymać przetwarzanie programu

```shell
    -e --exception [VALUE]  Set the maximum number of exceptions before stopping execution of a program
                            Default: 500
```

- maksymalne zagłębienie w wywołaniach funkcji

```shell
    -f --function [VALUE]   Set the maximum number of nested function calls before stopping execution of program
                            Default: 100
```

# Realizacja

## Moduły

1. [Wrapper źródła](./src/main/java/org/example/lexer/PositionalReaderImpl.java)
1. [Analizator leksykalny](./src/main/java/org/example/lexer/LexerImpl.java)
1. [Analizator składniowy](./src/main/java/org/example/parser/ParserImpl.java)
1. [Interpreter](./src/main/java/org/example/interpreter/InterpretingVisitor.java)
1. [Obsługa błędów](./src/main/java/org/example/error/ErrorHandlerImpl.java)

## Analizator leksykalny

### Interfejs

```java
public interface Lexer {
	Token nextToken();
}
```

Lexer jest kluczowym elementem w procesie analizy leksykalnej.
Jego głównym zadaniem jest przetworzenie ciągu znaków na sekwencję tokenów,
które reprezentują dozwolone wyrazy w języku.

Podczas tworzenia instancji Lexera, jako argument przekazywany jest Reader,
który dostarcza strumień znaków do analizy. Lexer udostępnia metodę umożliwiającą
odczyt kolejnych tokenów.

Ważną cechą Lexera jest tryb leniwy (lazy). Oznacza to, że lexer czyta kolejne znaki
dopiero wtedy, gdy zostanie poproszony o dostarczenie nowego tokenu.
To umożliwia wydajne przetwarzanie strumienia danych, ponieważ nie wymaga
wczytywania i analizowania całego tekstu na raz.

Kiedy metoda do odczytu kolejnych tokenów jest wywoływana, Lexer pobiera odpowiednią
liczbę znaków ze strumienia wejściowego i dokonuje analizy leksykalnej.
Na podstawie tych znaków, Lexer generuje i zwraca kolejny token reprezentujący
zidentyfikowany wyraz w języku.

Implementacja dostępna: [lexer](./src/main/java/org/example/lexer/LexerImpl.java)

### Tokeny

Obsługiwane typy tokenów: [token types](./src/main/java/org/example/token/TokenType.java)

```java
public enum TokenType {
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
	ASCENDING("asc", false),
	DESCENDING("desc", false),

	AS("as", false),

	OPEN_CURLY_PARENTHESES("{"),
	OPEN_ROUND_PARENTHESES("("),
	OPEN_SQUARE_PARENTHESES("["),
	CLOSED_CURLY_PARENTHESES("}"),
	CLOSED_ROUND_PARENTHESES(")"),
	CLOSED_SQUARE_PARENTHESES("]"),
	VERTICAL_BAR_PARENTHESES("|"),

	SEMICOLON(";"),
	COLON(":"),
	COMMA(","),
	DOT("."),
	EXPLICIT_CAST("@"),

	AND("and", false),
	NOT("not", false),
	OR("or", false),

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

	SINGLE_LINE_COMMENT("#", "\n"),
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
	VOID("void"),

	STRING("String"),

	MAP("Map"),
	ITERABLE("Iterable"),
	TUPLE("Tuple"),
	COMPARATOR("Comparator"),
	;
	...
}
```

### Testy

Testy Lexera polegają głównie na przygotowaniu ciągu znaków
przekazywanego do Lexera oraz sprawdzeniu czy wygenerowane
tokeny zgadzają się z oczekiwanymi.

Przykład:

```groovy
def 'Should detect all the boolean tokens correctly'() {
	given:
	var lexer = toLexer(content)

	expect:
	var token = lexer.nextToken()
	token.getType() == type
	token.<Boolean> getValue() == value

	where:
	content   || value | type
	" true "  || true  | TokenType.BOOLEAN_TRUE
	" false " || false | TokenType.BOOLEAN_FALSE
}
```

Testy dla innych rodzajów tokenów są opracowane na podobnej zasadzie,
dostosowując wartości oczekiwane do danego rodzaju tokenów, takich jak liczby,
ciągi znaków, identyfikatory, itp.

Więcej przykładów testów jest dostępne [tutaj](./src/test/groovy/org/example/lexer/LexerTest.groovy).
Tam są dostępne różne scenariusze testowe, które sprawdzają różne przypadki analizy leksykalnej
i porównują wyniki z oczekiwanymi tokenami. Testy te są pomocne w zapewnieniu,
że Lexer działa zgodnie z oczekiwaniami i poprawnie rozpoznaje różne wyrazy w języku.

## Analizator składniowy

### Interfejs

```java
public interface Parser {
	Program parseProgram() throws CriticalParserException;
}
```

Analizator składniowy (parser) to kluczowy komponent w procesie interpretacji języka programowania.
Jego głównym zadaniem jest analiza struktury syntaktycznej tekstu źródłowego i generowanie drzewa składniowego
(abstract syntax tree - AST).

Drzewo składniowe jest hierarchicznym modelem struktury języka programowania. Składa się z węzłów,
które reprezentują różne konstrukcje językowe, takie jak instrukcje, wyrażenia, deklaracje, itp.
Węzły są połączone relacjami rodzic-dziecko, które odzwierciedlają zależności między nimi.

Drzewo składniowe jest ważne, ponieważ pozwala na reprezentację struktury języka w sposób uporządkowany
i zrozumiały dla interpretera. Jest również łatwiejsze do analizy i manipulacji niż sam tekst źródłowy.

Implementacja dostępna: [parser](./src/main/java/org/example/parser/ParserImpl.java)

### Testy

Testy parsera mają na celu sprawdzenie, czy parser poprawnie analizuje prawidłowe konstrukcje językowe
i generuje oczekiwane drzewo AST.

Jeśli wygenerowane drzewo AST jest zgodne z oczekiwanym, to test jest uważany za zaliczony.
W przeciwnym przypadku, jeśli istnieje rozbieżność między wygenerowanym a oczekiwanym drzewem AST, test jest uznawany
za niepowodzenie.

Testy parsera obejmują różne konstrukcje językowe, takie jak instrukcje, wyrażenia, deklaracje, wywołania funkcji,
operatory, kontrola przepływu, itp. Dla każdej konstrukcji dostarcza się prawidłowy kod jako dane wejściowe
i oczekiwane drzewo AST jako wynik.

Dodatkowo występują testy sytuacji wyjątkowych, takich jak błędy składniowe w programie.
One są równie istotne jak testy poprawnych konstrukcji. Mają one na celu sprawdzenie,
czy parser poprawnie obsługuje sytuacje, w których program zawiera błędy składniowe lub niezgodności.

Przykład:

```groovy
    def 'Should be able to parse single expression statement'() {
	given:
	var parser = toParser(program)

	expect:
	parser.parseProgram() == result

	where:
	program                                       || result
	"fun main() {functionCall();}"                || wrapStatements(List.of(new FunctionCallExpression("functionCall", List.of(), position)))
	"fun main() {i[mapCall];}"                    || wrapStatements(List.of(new MethodCallExpression(new IdentifierExpression("i", position), new FunctionCallExpression("operator[]", List.of(new IdentifierExpression("mapCall", position)), position), position)))
	"fun main() {i[mapCall1][mapCall2];}"         || wrapStatements(List.of(new MethodCallExpression(new MethodCallExpression(new IdentifierExpression("i", position), new FunctionCallExpression("operator[]", List.of(new IdentifierExpression("mapCall1", position)), position), position), new FunctionCallExpression("operator[]", List.of(new IdentifierExpression("mapCall2", position)), position), position)))
	"fun main() {i[mapCall1].methodCall();}"      || wrapStatements(List.of(new MethodCallExpression(new MethodCallExpression(new IdentifierExpression("i", position), new FunctionCallExpression("operator[]", List.of(new IdentifierExpression("mapCall1", position)), position), position), new FunctionCallExpression("methodCall", List.of(), position), position)))
	"fun main() {i.methodCall();}"                || wrapStatements(List.of(new MethodCallExpression(new IdentifierExpression("i", position), new FunctionCallExpression("methodCall", List.of(), position), position)))
	"fun main() {i.methodCall1().methodCall2();}" || wrapStatements(List.of(new MethodCallExpression(new MethodCallExpression(new IdentifierExpression("i", position), new FunctionCallExpression("methodCall1", List.of(), position), position), new FunctionCallExpression("methodCall2", List.of(), position), position)))
	"fun main() {i.methodCall1().tupleCall2;}"    || wrapStatements(List.of(new TupleCallExpression(new MethodCallExpression(new IdentifierExpression("i", position), new FunctionCallExpression("methodCall1", List.of(), position), position), "tupleCall2", position)))
	"fun main() {i.tupleCall1.methodCall2();}"    || wrapStatements(List.of(new MethodCallExpression(new TupleCallExpression(new IdentifierExpression("i", position), "tupleCall1", position), new FunctionCallExpression("methodCall2", List.of(), position), position)))
	"fun main() {i.tupleCall;}"                   || wrapStatements(List.of(new TupleCallExpression(new IdentifierExpression("i", position), "tupleCall", position)))
	"fun main() {i.tupleCall1.tupleCall2;}"       || wrapStatements(List.of(new TupleCallExpression(new TupleCallExpression(new IdentifierExpression("i", position), "tupleCall1", position), "tupleCall2", position)))
}
```

Więcej testów dostępne jest [tutaj](src/test/groovy/org/example/parser/ParserIntegrationTest.groovy)
Tam znajdują się różne scenariusze testowe, które sprawdzają różne przypadki analizy składniowej
i porównują wyniki z oczekiwanymi drzewami AST. Testy te pomagają w zapewnieniu, że parser działa zgodnie
z oczekiwaniami i poprawnie analizuje różne konstrukcje językowe.

## Interpreter

### Interfejs

```java
public interface Interpreter {
	void execute(Program program);
}
```

```java
public interface Visitor {
	void visit(Program program);

	void visit(FunctionDefinitionStatement statement);

	void visit(DeclarationStatement statement);

	void visit(IfStatement statement);

	void visit(WhileStatement statement);

	void visit(ForStatement statement);

	void visit(AssignmentStatement statement);

	void visit(ReturnStatement statement);

	void visit(BlockStatement statement);

	void visit(BinaryLogicalExpression expression);

	void visit(NegateLogicalExpression expression);

	void visit(BooleanExpression value);

	void visit(RelationLogicalExpression expression);

	void visit(EqualityRelationLogicalExpression expression);

	void visit(BinaryArithmeticExpression expression);

	void visit(NegationArithmeticExpression expression);

	void visit(IntegerExpression value);

	void visit(FloatingPointExpression value);

	void visit(StringExpression value);

	void visit(TupleCallExpression expression);

	void visit(MethodCallExpression expression);

	void visit(IdentifierExpression expression);

	void visit(FunctionCallExpression expression);

	void visit(SelectExpression expression);

	void visit(TupleExpression expression);

	void visit(TupleElement expression);

	void visit(MapExpression expression);

	void visit(ExplicitCastExpression expression);

	void visit(PrintFunction expression);
}
```

`InterpretingVisitor` implementuje interfejs `Visitor`, który zawiera metody do odwiedzania różnych typów węzłów
w drzewie AST. Każda metoda `visit()` odpowiada za interpretację danego typu węzła.

Dodatkowo, `InterpretingVisitor` implementuje interfejs `Interpreter`, który zawiera metodę `interpret()`
służącą do rozpoczęcia interpretacji programu. Metoda `interpret()` przekazuje korzeń drzewa AST do metody `accept()`
w celu rozpoczęcia procesu interpretacji.

```java
public class InterpretingVisitor implements Visitor, Interpreter {
	...
}
```

Dzięki zastosowaniu wzorca projektowego Visitor, interpreter może łatwo przechodzić po strukturze drzewa AST
i interpretować kolejne węzły zgodnie z ich typami. Każdy typ węzła może mieć inną logikę interpretacji,
co umożliwia elastyczne przetwarzanie różnych konstrukcji językowych.

Implementacja dostępna: [interpreter](./src/main/java/org/example/interpreter/InterpretingVisitor.java)

### Testy

Testy interpretera polegają na przeprowadzeniu automatycznych testów, które polegają na wczytaniu przykładowego
programu, wykonaniu go przy użyciu interpretera, a następnie porównaniu standardowego wyjścia programu
z oczekiwanym standardowym wyjściem.

Przykładowy program:

```kotlin
#!/mnt/c/Users/Public/Documents/TKOM/interpreter

fun main() {
	print("Hello, World!");
}
```

Przykładowy test:

```groovy
def 'Should evaluate program correctly'() {
	expect:
	transform(interpret(program)) == result

	where:
	program     || result
	"hello.txt" || "Hello, World!\n"
}
```

Więcej testów dostępne jest [tutaj](src/test/groovy/org/example/interpreter/InterpretingVisitorIntegrationTest.groovy)
Te testy sprawdzają różne aspekty interpretacji, takie jak operacje matematyczne, instrukcje warunkowe,
pętle, obsługę zmiennych itp. Dzięki tym testom można zweryfikować poprawność działania interpretera
dla różnych przypadków i upewnić się, że interpretuje programy zgodnie z oczekiwaniami.

## Obsługa błędów

Obsługa błędów w implementacji interpretera obejmuje różne aspekty i ma na celu zapewnienie odpowiedniej
obsługi sytuacji awaryjnych oraz informowanie użytkownika o wystąpieniu błędów. Oto opis sposobu obsługi
błędów w poszczególnych komponentach:

**Źródło**

- Jeżeli wystąpi błąd, użytkownikowi wyświetlany jest odpowiedni komunikat błędu
- Program interpretera kończy się po wystąpieniu błędu

**Lexer**

- Ogólnie, błędy są przekazywane do wydzielonego obiektu obsługującego błędy
- W przypadku napotkania długich identyfikatorów, komentarzy lub stringów, identyfikator jest przycinany do maksymalnej
  długości
- Jeśli napotkana liczba całkowita jest zbyt długa, pomijana jest jej wykraczająca część
- Jeśli napotkana liczba zmienno przecinkowa ma zbyt długą część ułamkową, zignorowane są nadmiarowe cyfry po przecinku
- Jeżeli napotkany znak nie jest rozpoznawany, jest pomijany

**Parser**

- Błędy są przekazywane do wydzielonego obiektu obsługującego błędy
- W parserze wyróżnia się dwa rodzaje błędów: krytyczne i niekrytyczne
- Błędy krytyczne powodują zakończenie programu
- Niekrytyczne błędy, takie jak pominięcie średnika, są zgłaszane do ErrorHandlera

**Interpreter**

- Wszystkie błędy interpretera są krytyczne
- Błędy interpretera są zgłaszane do ErrorHandlera

Dzięki temu podejściu, błędy w różnych komponentach są odpowiednio obsługiwane, co pozwala na wykrycie
i raportowanie błędów użytkownikowi oraz umożliwia kontrolowane zakończenie programu w przypadku wystąpienia
błędów krytycznych.

### Komunikaty o błędach

Komunikaty o błędach są zbierane i wyświetlane użytkownikowi w czytelnej formie.
Każda linia zawierająca błędy jest poprzedzona numerem linii, a następnie wyświetlane są konkretne komunikaty
błędów znalezione przez program.

Taki format komunikatów o błędach ułatwia użytkownikowi zidentyfikowanie miejsc, w których występują błędy
i umożliwia skoncentrowanie się na ich rozwiązaniu. Numeracja linii pozwala na precyzyjne lokalizowanie błędów w kodzie,
co jest szczególnie przydatne w przypadku większych programów.

**Przykładowy kod:**

```groovy
String 🙁 = "Ala ma kota";
```

**Rezultat:**

```bash
[INFO]    1: String đź™? = "Ala ma kota";
[ERROR] Unexpected character ™ at position line 1, character 10
[ERROR] Unexpected character ? at position line 1, character 11
```

**Przykładowy kod:**

```kotlin
#!/mnt/c/Users/Public/Documents/TKOM/interpreter

fun recursive() {
	print("recursion");
	recursive();
}

fun main() {
	recursive();
}
```

**Rezultat:**

```shell
$ ./src/test/resources/error-recursion-limit.txt --function 5 
recursion
recursion
[INFO]    4:    print("recursion");
[ERROR] MaxFunctionStackSizeReachedException(super=CriticalInterpreterException(position=Position(line=4, characterNumber=8), contextStack=[~~main~~: Position(line=1, characterNumber=1), main: Position(line=1, characterNumber=1), recursive: Position(line=9, characterNumber=2), recursive: Position(line=5, characterNumber=2), recursive: Position(line=5, characterNumber=2), print: Position(line=4, characterNumber=2)])) - org.example.interpreter.error.MaxFunctionStackSizeReachedException:
~~main~~: Position(line=1, characterNumber=1)
main: Position(line=1, characterNumber=1)
recursive: Position(line=9, characterNumber=2)
recursive: Position(line=5, characterNumber=2)
recursive: Position(line=5, characterNumber=2)
print: Position(line=4, characterNumber=2)
```

W przypadku błędów interpretacji program jest zatrzymywany, a następnie wyświetlane są kolejne błędy związane z daną
linią kodu.

Dodatkowo, interpreter wykonuje zrzut stosu wywołań funkcji, co oznacza, że wyświetlane są informacje
o kolejnych funkcjach, które zostały wywołane przed wystąpieniem błędu. Jest to przydatne
w celu zrozumienia kontekstu, w którym błąd wystąpił. Dodatkowo, interpreter również podaje miejsce wywołania
w kodzie, co ułatwia zlokalizowanie konkretnego fragmentu kodu, który jest powiązany z błędem.

Dzięki tym informacjom użytkownik ma pełniejszy obraz o błędach i może skoncentrować się
na ich naprawie w odpowiednich miejscach kodu.

# Biblioteki

- **Vavr core** - to biblioteka dla Javy, która pomaga zmniejszyć ilość kodu oraz zwiększyć niezawodność.
- **Apache Commons Lang** - to biblioteka, która udostępnia pre-definiowane operacje na Stringach.
- **Simple Logging Facade for Java** - biblioteka, która umożliwia logowanie komunikatów w jednakowy sposób nie zależny
  od implementacji.
- **Project Lombok** - procesor anotacji, który generuje podstawową implementację na podstawie zdefiniowanych anotacji w
  kodzie.
- **Spock Framework** - to framework do testowania aplikacji Java i Groovy. Wyróżnia go bardzo wyrazisty język
  specyfikacji. Umożliwia proste
  mockowanie oraz stubowanie funkcjonalności.
