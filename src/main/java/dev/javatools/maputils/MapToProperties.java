package dev.javatools.maputils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Get the sorted list of all the properties (full json path and value) in a given Map
 *
 * <pre>
 *     Example:
 *          {
 *              "name": "James Butt",
 *              "age": 25,
 *              "dateOfBirth": "1995-05-15",
 *              "friends": [
 *                  {
 *                      "name": "Josephine Darakjy",
 *                      "age": 22,
 *                      "dateOfBirth": "1997-01-24",
 *                      "associatedAddresses": [
 *                          {
 *                              "street": "25 E 75th St #69",
 *                              "city": "Los Angeles",
 *                              "state": "CA",
 *                              "zip": "90034"
 *                          }
 *                      ]
 *                  }
 *              ],
 *              "primaryAddress": {
 *                  "street": "6649 N Blue Gum St",
 *                  "city": "New Orleans",
 *                  "state": "LA",
 *                  "zip": "70116"
 *              }
 *          }
 *
 *      The above map will generate the below properties
 *
 *          age=25
 *          dateOfBirth=1995-05-15
 *          friends[0].age=22
 *          friends[0].associatedAddresses[0].city=Los Angeles
 *          friends[0].associatedAddresses[0].state=CA
 *          friends[0].associatedAddresses[0].street=25 E 75th St #69
 *          friends[0].associatedAddresses[0].zip=90034
 *          friends[0].dateOfBirth=1997-01-24
 *          friends[0].name=Josephine Darakjy
 *          name=James Butt
 *          primaryAddress.city=New Orleans
 *          primaryAddress.state=LA
 *          primaryAddress.street=6649 N Blue Gum St
 *          primaryAddress.zip=70116
 * </pre>
 */
public final class MapToProperties {

    private MapToProperties() {
    }

    /**
     * @param source Map to process and generate the list of all properties
     * @return Sorted set of all the properties in the Map
     */
    public static Map getProperties(Map source) {
        Map results = new TreeMap<>();
        Map treeMap = MapSort.getSortedMap(source);
        MapToProperties mapToProperties = new MapToProperties();
        mapToProperties.getProperties(treeMap, results, null);
        return results;
    }

    /**
     * @param source


     */
    /**
     * @param source     Map to process and generate the list of all properties
     * @param paths      Sorted set of all the properties in the Map processed till time
     * @param pathPrefix path identified until this step
     */
    private void getProperties(Map source, Map paths, String pathPrefix) {
        String currentPrefix;
        if (pathPrefix == null) {
            currentPrefix = "";
        } else {
            currentPrefix = pathPrefix + ".";
        }

        for (Object item : source.entrySet()) {
            Map.Entry entry = (Map.Entry) item;
            if (entry.getValue() instanceof Map || entry.getValue() instanceof Set || entry.getValue() instanceof List) {
                processNextElement(entry.getValue(), paths, currentPrefix + entry.getKey());
            } else {
                paths.put(currentPrefix + entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * @param source     Map to process and generate the list of all properties
     * @param results    Sorted set of all the properties in the Map processed till time
     * @param pathPrefix path identified until this step
     */
    private void processNextElement(final Object source, final Map results, final String pathPrefix) {
        if (source instanceof Map) {
            getProperties((Map) source, results, pathPrefix);
        } else if (source instanceof List) {
            processList((List) source, results, pathPrefix);
        } else {
            results.put(pathPrefix, source);
        }
    }

    /**
     * @param source     Map to process and generate the list of all properties
     * @param paths      Sorted set of all the properties in the Map processed till time
     * @param pathPrefix path identified until this step
     */
    private void processList(final List source, final Map paths, final String pathPrefix) {
        String currentPrefix;
        if (pathPrefix == null) {
            currentPrefix = "";
        } else {
            currentPrefix = pathPrefix;
        }
        int index = 0;
        if (source instanceof List) {
            for (Object currentListItem : (List) source) {
                processNextElement(currentListItem, paths, currentPrefix + "[" + index++ + "]");
            }
        }
    }

}
