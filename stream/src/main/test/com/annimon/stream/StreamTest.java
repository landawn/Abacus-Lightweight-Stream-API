package com.annimon.stream;

import java.util.Map;

import org.junit.Test;

import com.landawn.abacus.util.Fn;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.stream.Collectors;

public class StreamTest {

    @Test
    public void test_groupBy() {
        Map<Integer, Integer> map = IntStream.range(0, 10).boxed().groupByToEntry(Fn.<Integer> identity(), Collectors.countingInt()).toMap();
        N.println(map);
    }

}
