package dev.javatools.maputils;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.javatools.maputils.helpers.Constants;
import dev.javatools.maputils.helpers.MapUtilsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;


/**
 * This class has all the APIs needs to sort a Map.
 * <pre>
 * How to handle objects inside a list? Answer to that question is listKeys.
 * If the Map has a list of objects and if you need to sort the list based on a
 * field of an object, this feature will help.
 * Here is how it works.
 * Take the below Map as an example
 * {
 *      "members": [
 *          {
 *              "name": "John",
 *              "age": 22
 *          },
 *          {
 *              "name": "Bob",
 *              "age": 18
 *          }
 *      ]
 *  }
 * }
 * </pre>
 * If you need the sort the above Map based on the member name. You need to pass
 * listKey.put("members[]", "name");
 * If you need the sort the above Map based on the member age. You need to pass
 * listKey.put("members[]", "age");
 */
public final class MapSort {

    private static final String EMPTY_STRING = "";
    private static final String PERIOD = ".";
    private static final String LIST_SYMBOL = "[]";

    private Logger logger = LoggerFactory.getLogger(MapSort.class);

    private MapSort() {
    }

    /**
     * Creates a new map that has all the fields from the input, sorts them and respond the newly created Map.
     *
     * @param input Map that needs to be sorted
     * @return Map that is sorted on the keys
     */
    public static Map getSortedMap(final Map input) {
        if (null == input) {
            return new HashMap();
        }
        Map<String, String> listKeys = new HashMap<>();
        return getSortedMap(input, listKeys);
    }


    /**
     * @param input    Map that needs to be sorted
     * @param listKeys List of field names that needs to be sorted, see the class documentation for more details
     * @return Map that is sorted on the keys
     */
    public static Map getSortedMap(final Map input, final Map<String, String> listKeys) {
        try {
            if (null == input) {
                return new HashMap();
            }
            Map<String, String> listKeysInternal = new HashMap<>();
            if (null != listKeys) {
                listKeysInternal.putAll(listKeys);
            }
            String jsonString = Constants.jsonMapper.writeValueAsString(input);
            Map sanitizedMap = Constants.jsonMapper.readValue(jsonString, Map.class);
            return getSortedMap(sanitizedMap, listKeysInternal, null);
        } catch (JsonProcessingException jsonProcessingException) {
            throw new MapUtilsException(jsonProcessingException);
        }
    }

    /**
     * @param input    Map that needs to be sorted
     * @param listKeys List of field names that needs to be sorted, see the class documentation for more details
     * @param prefix
     * @return Map that is sorted on the keys
     */
    private static Map getSortedMap(final Map input, final Map<String, String> listKeys, final String prefix) {
        TreeMap treeMap = new TreeMap();
        MapSort mapSort = new MapSort();
        for (Object item : input.entrySet()) {
            Map.Entry entry = (Map.Entry) item;
            if (entry.getValue() instanceof Map) {
                mapSort.processMap(entry, treeMap, listKeys, prefix);
            } else if (entry.getValue() instanceof List) {
                mapSort.processList(entry, treeMap, listKeys, prefix);
            } else {
                treeMap.put(entry.getKey(), entry.getValue());
            }
        }
        return treeMap;
    }

    /**
     * Process map inside map
     *
     * @param entry
     * @param treeMap
     * @param listKeys
     * @param prefix
     */
    private void processMap(final Map.Entry entry, final TreeMap treeMap, final Map<String, String> listKeys,
                            final String prefix) {
        String updatedPrefix = (null == prefix) ? EMPTY_STRING : prefix + PERIOD;
        Map innerMap = getSortedMap((Map) entry.getValue(), listKeys, updatedPrefix + entry.getKey());
        treeMap.put(entry.getKey(), innerMap);
    }

    /**
     * Process List or Set inside Map.
     *
     * @param entry
     * @param treeMap
     * @param listKeys
     * @param prefix
     */
    private void processList(final Map.Entry entry, final Map treeMap, final Map<String, String> listKeys, final String prefix) {
        String updatedPrefix = (null == prefix) ? entry.getKey() + LIST_SYMBOL : prefix + PERIOD + entry.getKey() + LIST_SYMBOL;
        List sortedSet;
        sortedSet = getSortedList(((List) entry.getValue()), listKeys, updatedPrefix);
        treeMap.put(entry.getKey(), sortedSet);
    }

    /**
     * Process List or Set inside List or Set
     *
     * @param entry
     * @param treeSet
     * @param listKeys
     * @param prefix
     */
    private void processList(final Object entry, final Set<Object> treeSet, final Map<String, String> listKeys, final String prefix) {
        String updatedPrefix = (null == prefix) ? LIST_SYMBOL : prefix + LIST_SYMBOL;
        List sortedSet;
        sortedSet = getSortedList(((List) entry), listKeys, updatedPrefix);
        treeSet.add(sortedSet);
    }

    /**
     * Process List that is inside Map
     *
     * @param input
     * @param listKeys
     * @param prefix
     * @return
     */
    private List getSortedList(final List input, final Map<String, String> listKeys, final String prefix) {
        Set<Object> response = null;
        for (Object currentItem : input) {
            logger.debug(currentItem.getClass().getName());
            if (currentItem instanceof Map) {
                if (response == null) {
                    response = new HashSet();
                }
                response.add(getSortedMap((Map) currentItem, listKeys, prefix));
            } else if (currentItem instanceof List) {
                if (response == null) {
                    response = new HashSet();
                }
                processList(currentItem, response, listKeys, prefix);
            } else {
                if (response == null) {
                    response = new TreeSet();
                }
                response.add(currentItem);
            }
        }
        if (null == response) {
            response = new TreeSet();
        }
        if (listKeys.containsKey(prefix) && response instanceof HashSet) {
            return response.stream().sorted(Comparator.comparing(innerMap -> ((String) ((Map) innerMap).get(listKeys.get(prefix))))).collect(Collectors.toList());
        } else {
            return response.stream().collect(Collectors.toList());

        }
    }
}
