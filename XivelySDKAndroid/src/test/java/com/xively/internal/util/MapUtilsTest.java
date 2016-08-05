package com.xively.internal.util;

import junit.framework.TestCase;

import java.util.EnumMap;
import java.util.Map;

public class MapUtilsTest extends TestCase {

    private Map<testValues, String> testMap;
    private enum  testValues {ONE, TWO, THREE, FOUR};

    @Override
    protected void setUp() throws Exception {
        testMap = new EnumMap<>(testValues.class);
        testMap.put(testValues.ONE, "one");
        testMap.put(testValues.TWO, "two");
        testMap.put(testValues.THREE, "three");
        testMap.put(testValues.FOUR, "four");
    }

    public void testGetKeyByValue() throws Exception {
        assertEquals(MapUtils.getKeyByValue(testMap, "one"), testValues.ONE);
        assertEquals(MapUtils.getKeyByValue(testMap, "two"), testValues.TWO);
        assertEquals(MapUtils.getKeyByValue(testMap, "three"), testValues.THREE);
        assertEquals(MapUtils.getKeyByValue(testMap, "four"), testValues.FOUR);
    }

    public void testGetKeyByValueIsNullForNullValue() throws Exception {
        assertEquals(MapUtils.getKeyByValue(testMap, null), null);
    }

    public void testGetKeyByValueIsNullForNotFoundValue() throws Exception {
        assertEquals(MapUtils.getKeyByValue(testMap, "something else"), null);
    }

    public void testGetKeyByValueIsNullForNullMap() throws Exception {
        assertEquals(MapUtils.getKeyByValue(null, "something"), null);
    }
}