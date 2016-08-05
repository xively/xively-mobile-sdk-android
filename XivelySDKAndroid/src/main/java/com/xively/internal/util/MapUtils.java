package com.xively.internal.util;

import java.util.Map;
import java.util.Map.Entry;

public class MapUtils {
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        if (map == null){
            return null;
        }

	    for (Entry<T, E> entry : map.entrySet()) {
	        if (entry.getValue().equals(value)) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
}
