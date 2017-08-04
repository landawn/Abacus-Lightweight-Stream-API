Abacus-Lightweight-Stream-API
======================

[![Maven Central](https://img.shields.io/maven-central/v/com.landawn/abacus-stream-lite.svg)](https://maven-badges.herokuapp.com/maven-central/com.landawn/abacus-stream-lite/)
[![Javadocs](https://www.javadoc.io/badge/com.landawn/abacus-stream-lite.svg)](https://www.javadoc.io/doc/com.landawn/abacus-stream-lite)

Stream API from Java 8 rewritten on iterators for Java 7 and Android - Shared interfaces with [Abacus-Util](https://github.com/landawn/AbacusUtil). Full API documentation is available [here](https://www.javadoc.io/doc/com.landawn/abacus-stream-lite).

### Includes

 + Functional interfaces (`Supplier`, `Function`, `Consumer` etc);
 + `Stream`/`IntStream`/`LongStream`/`DoubleStream` (without parallel processing, but with a variety of additional methods and with custom operators);
 + `Optional`/`OptionalBoolean`/`OptionalInt`/`OptionalLong`/`OptionalDouble` classes;
 + `Fn` and `Comparators`

### Usage

```java
Stream.of(/* array | list | set | map | anything based on Iterator/Iterable interface */)
    .filter(..)
    .map(..)
    ...
    .sorted()
    .forEach(..);
Stream.of(value1, value2, value3)...
IntStream.range(0, 10)...
```
Example project: https://github.com/aNNiMON/Android-Java-8-Stream-Example


## Key features

### Custom operators

Unlike Java 8 streams, Lightweight-Stream-API provides the ability to apply custom operators.

```java
Stream.of(...)
    .chain(new Reverse<>())
    .forEach(...);

public final class Reverse<T> implements UnaryOperator<Stream<T>> {

    @Override
    public Stream<T> apply(Stream<T> stream) {
        final Iterator<? extends T> iterator = stream.getIterator();
        final ArrayDeque<T> deque = new ArrayDeque<T>();
        while (iterator.hasNext()) {
            deque.addFirst(iterator.next());
        }
        return Stream.of(deque.iterator());
    }
}
```

You can find more examples [here](https://github.com/aNNiMON/Lightweight-Stream-API/blob/master/stream/src/test/java/com/annimon/stream/CustomOperators.java).

### Additional operators

In addition to backported Java 8 Stream operators, the library provides:

- `skipNull` - filters only not null elements

  ```java
  Stream.of("a", null, "c", "d", null)
      .skipNull() // [a, c, d]
  ```

- `sortedBy` - sorts by extractor function

  ```java
  // Java 8
  stream.sorted(Comparator.comparing(Person::getName))
  // LSA
  stream.sortedBy(Person::getName)
  ```

- `groupBy` - groups by extractor function

  ```java
  // Java 8
  stream.collect(Collectors.groupingBy(Person::getName)).entrySet().stream()
  // LSA
  stream.groupBy(Person::getName)
  ```

- `chunkBy` - partitions sorted stream by classifier function

  ```java
  Stream.of("a", "b", "cd", "ef", "gh", "ij", "klmnn")
      .chunkBy(String::length) // [[a, b], [cd, ef, gh, ij], [klmnn]]
  ```

- `sliding` - partitions stream into fixed-sized list and sliding over the elements

  ```java
  Stream.rangeClosed(0, 10)
      .sliding(4, 6) // [[0, 1, 2, 3], [6, 7, 8, 9]]
  ```

- `takeWhile` / `dropWhile` - introduced in Java 9, limits/skips stream by predicate function

  ```java
  Stream.of("a", "b", "cd", "ef", "g")
      .takeWhile(s -> s.length() == 1) // [a, b]
  Stream.of("a", "b", "cd", "ef", "g")
      .dropWhile(s -> s.length() == 1) // [cd, ef, g]
  ```

- `scan` - iteratively applies accumulation function and returns Stream

  ```java
  IntStream.range(1, 6)
      .scan((a, b) -> a + b) // [1, 3, 6, 10, 15]
  ```

- `indexed` - adds an index to every element, result is `IntPair`

  ```java
  Stream.of("a", "b", "c")
      .indexed() // [(0 : "a"), (1 : "b"), (2 : "c")]
  ```

## Download

Releases are available in [Maven Central](https://repo1.maven.org/maven2/com/landawn/abacus-stream-lite/)

Maven:

```xml
<dependency>
  <groupId>com.landawn</groupId>
  <artifactId>abacus-stream-lite</artifactId>
  <version>0.8.1</version>
</dependency>
```
or Gradle:

```groovy
dependencies {
  ...
  compile 'com.landawn:abacus-stream-lite:0.8.1'
  ...
}
```

Also included version for **Java ME**. Checkout [javame branch](https://github.com/aNNiMON/Lightweight-Stream-API/tree/javame).

For use lambda expressions in Java 7 or Android, take a look at [Retrolambda](https://github.com/orfjackal/retrolambda) repository.
