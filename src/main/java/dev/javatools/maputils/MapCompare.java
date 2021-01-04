package dev.javatools.maputils;

import java.util.Map;
import java.util.Set;

/**
 * MapCompare utility will compare two maps and returns true if both maps are same. Otherwise it will return false.
 * How does it compare both maps?
 * <pre>
 *     Makes sure both the maps have the exact same keys and same values, it compares all values in the inner objects recursively.
 *     So, no matter how big is your map and what you have in your map, even the custom objects, it will make sure it compare all fields (field by field).
 * </pre>
 */
public final class MapCompare {

    private MapCompare() {
    }

    /**
     * @param left  First map that needs to be compared with Second map
     * @param right Second map that needs to be compared with First map
     * @return returns true if both the maps are equal, otherwise false.
     */
    public static boolean equal(Map left, Map right) {
        if (null == left ^ null == right) {
            return false;
        } else if (null == left && null == right) {
            return true;
        }
        Map leftProperties = MapProperties.getProperties(left);
        Map rightProperties = MapProperties.getProperties(right);
        if (null == rightProperties ^ null == leftProperties) {
            return false;
        } else if (null == leftProperties && null == rightProperties) {
            return true;
        }
        Set rightKeySet = rightProperties.keySet();
        if (leftProperties.size() != rightProperties.size()) {
            return false;
        }
        for (Object property : leftProperties.entrySet()) {
            Map.Entry entry = Map.Entry.class.cast(property);
            if (!rightKeySet.contains(entry.getKey())) {
                return false;
            }
            if (!rightProperties.get(entry.getKey()).equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

}
