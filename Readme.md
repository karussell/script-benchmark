# Result

## TODOs

 * we used jdk 8, so try with latest JDK
 * try graalvm
 * try if "script" performance is still bad when warmed up but with a slightly different script

## With warming-up

java -jar target/benchmarks.jar -f 1 -i 4 -wi 4

```
    Benchmark                           Mode  Cnt        Score         Error  Units
    MyBenchmark.testLoop               thrpt    4  7423071.972 ±  968995.169  ops/s
    MyBenchmark.testMethodChained      thrpt    4  2884162.806 ±   28647.977  ops/s
    MyBenchmark.testRawListAccessLoop  thrpt    4  7765683.412 ±  839786.307  ops/s
    MyBenchmark.testRawValueLoop       thrpt    4  8663791.075 ± 1082613.307  ops/s
    MyBenchmark.testScript             thrpt    4  8655028.579 ±  757423.438  ops/s

```

## Without warming-up

The times for "script" are significant different as it jumps from 6m to over 8.3m in the
third iteration. Unfortunately we need fast initial performance as the
script is ~3 million times maximum.

java -jar target/benchmarks.jar -f 1 -i 4 -wi 0

```
    Benchmark                           Mode  Cnt        Score         Error  Units
    MyBenchmark.testLoop               thrpt    4  7367454.473 ± 1390387.361  ops/s
    MyBenchmark.testMethodChained      thrpt    4  2862724.583 ±  216806.294  ops/s
    MyBenchmark.testRawListAccessLoop  thrpt    4  7803015.459 ±  370282.400  ops/s
    MyBenchmark.testRawValueLoop       thrpt    4  8198234.433 ± 4209543.525  ops/s
    MyBenchmark.testScript             thrpt    4  7328059.123 ± 9177788.975  ops/s
```