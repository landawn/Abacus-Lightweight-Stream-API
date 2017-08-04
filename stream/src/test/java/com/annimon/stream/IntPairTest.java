package com.annimon.stream;

import static org.junit.Assert.*;
import org.junit.Test;

public final class IntPairTest {

    @Test
    public void testGetFirst() {
        final Indexed<String> p = new Indexed<String>(1, "first");
        assertEquals(1, p.index());
    }

    @Test
    public void testGetSecond() {
        final Indexed<String> p = new Indexed<String>(1, "first");
        assertEquals("first", p.value());
    }

    @Test
    public void testEqualsReflexive() {
        final Indexed<String> p = new Indexed<String>(1, "first");
        assertTrue(p.equals(p));
    }

    @Test
    public void testEqualsSymmetric() {
        final Indexed<String> p1 = new Indexed<String>(1, "first");
        final Indexed<String> p2 = new Indexed<String>(1, "first");

        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p1));
    }

    @Test
    public void testEqualsTransitive() {
        final Indexed<String> p1 = new Indexed<String>(1, "first");
        final Indexed<String> p2 = new Indexed<String>(1, "first");
        final Indexed<String> p3 = new Indexed<String>(1, "first");

        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p3));
        assertTrue(p1.equals(p3));
    }

    @Test
    public void testEqualsWithNull() {
        final Indexed<String> p = new Indexed<String>(1, "first");
        assertFalse(p.equals(null));
    }

    @Test
    public void testEqualsWithDifferentTypes() {
        final Indexed<String> p = new Indexed<String>(1, "first");
        assertFalse(p.equals(1));
    }

    @Test
    public void testEqualsWithDifferentGenericTypes() {
        final Indexed<String> p1 = new Indexed<String>(1, "first");
        final Indexed<Integer> p2 = new Indexed<Integer>(1, 1);

        assertFalse(p1.equals(p2));
    }

    @Test
    public void testEqualsWithSwappedValues() {
        final Indexed<Integer> p1 = new Indexed<Integer>(10, 15);
        final Indexed<Integer> p2 = new Indexed<Integer>(15, 10);

        assertFalse(p1.equals(p2));
    }

    @Test
    public void testHashCodeWithSameObject() {
        final Indexed<String> p1 = new Indexed<String>(1, "first");
        final Indexed<String> p2 = new Indexed<String>(1, "first");

        int initial = p1.hashCode();
        assertEquals(initial, p1.hashCode());
        assertEquals(initial, p1.hashCode());
        assertEquals(initial, p2.hashCode());
    }

    @Test
    public void testHashCodeWithDifferentGenericType() {
        final Indexed<String> p1 = new Indexed<String>(1, "first");
        final Indexed<Integer> p2 = new Indexed<Integer>(1, 1);

        assertNotEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void testHashCodeWithSwappedValues() {
        final Indexed<Integer> p1 = new Indexed<Integer>(10, 15);
        final Indexed<Integer> p2 = new Indexed<Integer>(15, 10);

        assertNotEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void testHashCodeWithNullSecondValue() {
        final Indexed<String> p1 = new Indexed<String>(0, null);
        final Indexed<String> p2 = new Indexed<String>(0, "first");

        assertNotEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void testToString() {
        final Indexed<String> p1 = new Indexed<String>(1, "first");
        assertEquals("IntPair[1, first]", p1.toString());
    }
}
