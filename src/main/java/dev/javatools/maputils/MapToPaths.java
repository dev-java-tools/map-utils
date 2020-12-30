package dev.javatools.maputils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


/**
 * Get the sorted list of all the paths (full json path) in a given Map
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
 *          age
 *          dateOfBirth
 *          friends[0].age
 *          friends[0].associatedAddresses[0].city
 *          friends[0].associatedAddresses[0].state
 *          friends[0].associatedAddresses[0].street
 *          friends[0].associatedAddresses[0].zip
 *          friends[0].dateOfBirth
 *          friends[0].name
 *          name
 *          primaryAddress.city
 *          primaryAddress.state
 *          primaryAddress.street
 *          primaryAddress.zip
 * </pre>
 */
public final class MapToPaths {

    private MapToPaths() {
    }

    /**
     * @param input Map to process
     * @return Sorted set of all the paths in the Map
     * In this the paths repeat with the position number if the objects are within the list/set/array.
     * To get unique paths, use getUniquePaths.
     */
    public static Set<String> getPaths(final Map input) {
        Set<String> results = new TreeSet<>();
        Map treeMap = MapSort.getSortedMap(input);
        MapToPaths mapToPaths = new MapToPaths();
        mapToPaths.getPaths(treeMap, results, null, false);
        return results;
    }

    /**
     * @param input Map to process
     * @return Sorted set of all the unique paths in the map.
     * What is Unique?
     * Paths related to the objects that are in the list/set/array of items are considered same.
     */
    public static Set<String> getUniquePaths(final Map input) {
        Set<String> results = new TreeSet<>();
        Map treeMap = MapSort.getSortedMap(input);
        MapToPaths mapToPaths = new MapToPaths();
        mapToPaths.getPaths(treeMap, results, null, true);
        return results;
    }


    /**
     * @param source     Map to process
     * @param paths      Paths that are already processed
     * @param pathPrefix prefix of the current inner element
     * @param unique     specifies if the path has to be unique or not, this will identify whether to aggregate the list items or not.
     */
    private void getPaths(final Map source, final Set<String> paths, final String pathPrefix, final boolean unique) {
        String currentPrefix;
        if (pathPrefix == null) {
            currentPrefix = "";
        } else {
            currentPrefix = pathPrefix + ".";
        }
        for (Object item : source.entrySet()) {
            Map.Entry entry = (Map.Entry) item;
            if (entry.getValue() instanceof Map || entry.getValue() instanceof Set || entry.getValue() instanceof List) {
                processNextElement(entry.getValue(), paths, currentPrefix + entry.getKey(), unique);
            } else {
                paths.add(currentPrefix + entry.getKey());
            }
        }
    }

    /**
     * @param source     Map to process
     * @param paths      Paths that are already processed
     * @param pathPrefix prefix of the current inner element
     * @param unique     specifies if the path has to be unique or not, this will identify whether to aggregate the list items or not.
     */
    private void processList(final List source, final Set<String> paths, final String pathPrefix, final boolean unique) {
        String currentPrefix;
        if (pathPrefix == null) {
            currentPrefix = "";
        } else {
            currentPrefix = pathPrefix;
        }
        int index = 0;
        if (source instanceof List) {
            for (Object currentListItem : source) {
                if (unique) {
                    processNextElement(currentListItem, paths, currentPrefix + "[]", true);
                } else {
                    processNextElement(currentListItem, paths, currentPrefix + "[" + index++ + "]", false);

                }
            }
        }
    }

    /**
     * @param source     Map to process
     * @param paths      Paths that are already processed
     * @param pathPrefix prefix of the current inner element
     * @param unique     specifies if the path has to be unique or not, this will identify whether to aggregate the list items or not.
     */
    private void processNextElement(final Object source, final Set<String> paths, final String pathPrefix, final boolean unique) {
        if (Map.class.isInstance(source)) {
            getPaths(Map.class.cast(source), paths, pathPrefix, unique);
        } else if (source instanceof List) {
            processList((List) source, paths, pathPrefix, unique);
        } else {
            paths.add(pathPrefix);
        }
    }

}
