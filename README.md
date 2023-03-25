# Techniki Kompilacji

Student:

```
Bartłomiej Krawczyk
```

Numer indeksu:

```
310774
```

## Temat

Język z wbudowanym typem słownika. Możliwe są podstawowe operacje na słowniku
(dodawanie, usuwanie, wyszukiwanie elementów wg klucza, sprawdzanie, czy dany
klucz znajduje się w słowniku itd.), iterowanie po elementach zgodnie z zadaną
kolejnością oraz wykonywanie na słowniku zapytań w stylu LINQ.
Kolejność, w jakiej zwracane są elementy podczas iterowania, jest określana
za pomocą funkcji przekazywanej jako dodatkowy parametr pętli.

**Wariant:** Statycznie typowany

**Typowanie:** Silne typowanie

**Język realizacji interpretera:** Java 17

# Opis zakładanej funkcjonalności

### Przykładowe konstrukcje językowe

# Wymagania

## Funkcjonalne

Analiza - TODO

## Niefunkcjonalne

Analiza - TODO

# Semantyka

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
	- np. TODO
- `Query<value, ...>`
	- struktura służąca do iterowania po krotkach, które są rezultatem zapytania
	- np. `SELECT value FROM map`

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

```groovy
boolean expression = first and(second or third);
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

```groovy
boolean expression = 2 + 2 == 4;

if
expression {
	// do something
}

if
expression doSomething();
```

- konstrukcja `if-else`:

```groovy
boolean expression = 2 + 2 == 4;

if
expression {
	// do something
} else {
	// do something else
}
```

- złożenie `if-else`:

```groovy
boolean first = 2 + 2 == 4;
boolean second = false;

if
first {
	// do something
} else if
second {
	// do something else on condition
}
```

**Pętla warunkowa:**

```groovy
boolean expression = true;
while
expression {
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

Semantyka przekazywania argumentów do funkcji:

- typy proste przekazywane przez wartość
- w przypadku typów złożonych przekazywana jest wartość referencji do obiektu

Semantyka obsługi zmiennych:
- typowanie statyczne vs dynamiczne
- typowanie silne vs słabe
- mutowalność vs stałe (ile razy możemy przypisywać wartość do zmiennej)

**Rekursywność wywoływania funkcji:**

- funkcje mogą być wołane rekursywnie (z ograniczeniem do maksymalnego zagłębienia)

```groovy
fun fibbonaci(n: int): int {
	if
	n == 0
	return 0;
	if
	n == 1
	return 1;
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

# Składnia (EBNF) realizowanego języka

# Formalna specyfikacja plików / strumieni wejściowych

# Formalna specyfikacja danych konfiguracyjnych

# Obsługa błędów

- Źródła
- Lexera
- Parsera
- Interpretera
- Czasu wykonania

## Przykłady komunikatów

# Sposób uruchomienia

Interpreter języka można uruchomić przy pomocy przygotowanego skryptu: `interpreter`

Przykładowe uruchomienie oraz rezultat:

```bash
$ ./interpreter --help
Usage: ./interpreter [OPTION] [FILE]
    -h --help        Display this message
    -c --clean       Re-build project from scratch before running application
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

## Program TODO

# Realizacja

## Moduły

## Obiekty

## Interfejsy

## Tokeny

TODO - jakie rozróżniane

## Sposób przetwarzania - w poszczególnych komponentach

## Wykorzystywane struktury danych

- do jakich struktur danych trafią określone obiekty (opis formalny / tekstowy)

## Wykorzystanie struktur danych

## Formy pośrednie

# Testowanie

- opis testowania

# Biblioteki

- **Vavr core** - to biblioteka dla Javy, która pomaga zmniejszyć ilość kodu oraz zwiększyć niezawodność.
- **Project Reactor** - biblioteka umożliwiająca nie blokującą, wydajną obsługę programu.
- **Apache Commons Lang** - to biblioteka, która udostępnia pre-definiowane operacje na Stringach.
- **Simple Logging Facade for Java** - biblioteka, która umożliwia logowanie komunikatów w jednakowy sposób nie zależny od implementacji.
- **Project Lombok** - procesor anotacji, który generuje podstawową implementację na podstawie zdefiniowanych anotacji w kodzie.
- **Spock Framework** - to framework do testowania aplikacji Java i Groovy. Wyróżnia go bardzo wyrazisty język specyfikacji. Umożliwia proste
  mockowanie oraz stubowanie funkcjonalności.

W dokumentacji wstępnej proszę też podać wszystkie konkrety:

- wszystkie typy wbudowane w Państwa język (w tym też takie, których konieczność obsługi wynikła z analizy tematu i które nie są jawnie podane w
  treści zadania),
- ich pola
- i metody (zachęcam do dodania czegoś od siebie; to, co podałam w treści zadania, to jedynie przykłady),
- użyte biblioteki (w tym te wykorzystywane do testów jednostkowych) itd.