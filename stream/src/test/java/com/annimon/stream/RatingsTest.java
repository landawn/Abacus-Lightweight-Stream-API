package com.annimon.stream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;

/**
 * Real example of calculate ratings.
 */
public class RatingsTest {

    private static Map<String, String> fileContents;

    @BeforeClass
    public static void setUpData() {
        fileContents = new HashMap<>(5);
        fileContents.put("ahilla.txt", "LongFlight     3 2 2 1 2\n" + "Units2D        2 1 1 0 1\n" + "SpaceCatcher   1 1 2 1 1\n" + "Galaxy         1 0 0 0 0\n"
                + "SonicTime3D    3 2 2 0 3\n" + "aPlatformer    1 1 0 1 0");
        fileContents.put("bogdan.txt", "LongFlight     1 2 0 2 1\n" + "Units2D        0 1 0 1 0\n" + "SpaceCatcher   0 1 0 1 1\n" + "Galaxy         0 1 0 0 0\n"
                + "SonicTime3D    0 3 0 1 1\n" + "aPlatformer    1 1 0 1 1");
        fileContents.put("holdfast.txt", "LongFlight     1 2 4 2 2\n" + "Units2D        0 2 1 2 1\n" + "SpaceCatcher   1 2 2 1 2\n"
                + "Galaxy         0 3 1 1 1\n" + "SonicTime3D    0 3 3 2 1\n" + "aPlatformer    1 0 0 1 1");
        fileContents.put("senderman.txt", "LongFlight     1 3 2 3 2\n" + "Units2D        0 2 3 2 1\n" + "SpaceCatcher   0 2 5 3 1\n"
                + "Galaxy         0 3 0 0 0\n" + "SonicTime3D    0 3 1 1 0\n" + "aPlatformer    0 0 5 1 1");
    }

    //    @Test
    //    public void testRatings() {
    //        String ratings = Stream.of(fileContents.keySet()) // list files
    //                // read content of files
    //                .flatMap(Function.Util.safe(new ThrowableFunction<String, Stream<String>, Throwable>() {
    //                    @Override
    //                    public Stream<String> apply(String filename) throws IOException {
    //                        return readLines(filename);
    //                    }
    //                }))
    //                // split line by whitespaces
    //                .map(new Function<String, String[]>() {
    //                    @Override
    //                    public String[] apply(String line) {
    //                        return line.split("\\s+");
    //                    }
    //                })
    //                // calculate sum by line and store in pair <int, string>
    //                .map(new Function<String[], Indexed<String>>() {
    //                    @Override
    //                    public Indexed<String> apply(String[] arr) {
    //                        // <sum of marks, name>
    //                        return new Indexed<String>(
    //                                Stream.of(arr)
    //                                        .skip(1)
    //                                        .mapToInt(new ToIntFunction<String>() {
    //                                            @Override
    //                                            public int applyAsInt(String t) {
    //                                                return Integer.parseInt(t);
    //                                            }
    //                                        })
    //                                        .sum(),
    //                                arr[0]
    //                        );
    //                    }
    //                })
    //                // Group by name
    //                .groupBy(new Function<Indexed<String>, String>() {
    //                    @Override
    //                    public String apply(Indexed<String> t) {
    //                        return t.value();
    //                    }
    //                })
    //                // Calculate summary ratings
    //                .map(new Function<Map.Entry<String, List<Indexed<String>>>, Indexed<String>>() {
    //                    @Override
    //                    public Indexed<String> apply(Map.Entry<String, List<Indexed<String>>> entry) {
    //                        final String name = entry.getKey();
    //                        final int ratings = Stream.of(entry.getValue())
    //                                .mapToInt(Functions.<String>intPairIndex())
    //                                .sum();
    //                        return new Indexed<String>(ratings, name);
    //                    }
    //                })
    //                // Sort by total rating descending
    //                .sortBy(new Function<Indexed<String>, Integer>() {
    //                    @Override
    //                    public Integer apply(Indexed<String> value) {
    //                        return -value.index();
    //                    }
    //                })
    //                // Convert to formatted string
    //                .map(new Function<Indexed<String>, String>() {
    //                    @Override
    //                    public String apply(Indexed<String> value) {
    //                        return String.format("%12s: %d", value.value(), value.index());
    //                    }
    //                })
    //                // lines to string
    //                .collect(Collectors.joining("\n"));
    //        
    //        /* Scala analogue
    //        .flatMap(filename => Source.fromFile(filename, "UTF-8").getLines)
    //        .map(s => s.split("\\s+"))
    //        .map { arr => (arr(0), arr
    //            .drop(1)
    //            .map(_.toInt)
    //            .sum) }
    //        .groupBy(_._1)
    //        .map { case (name, ratings) => (name, ratings.map(_._2).sum)  }
    //        .toSeq
    //        .sortBy(- _._2)
    //        .map { case (name, rating) => "%12s: %d".format(name, rating) }
    //        .foreach(println);
    //        */
    //        
    //        Assert.assertEquals(
    //                "  LongFlight: 38\n" +
    //                " SonicTime3D: 29\n" +
    //                "SpaceCatcher: 28\n" +
    //                "     Units2D: 21\n" +
    //                " aPlatformer: 17\n" +
    //                "      Galaxy: 11", ratings);
    //    }

    /*
     * Emulates read lines from file
     */
    private static Stream<String> readLines(String filename) throws IOException {
        return Stream.of(fileContents.get(filename).split("\n"));
    }
}
