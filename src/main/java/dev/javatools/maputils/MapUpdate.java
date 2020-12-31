package dev.javatools.maputils;


import dev.javatools.maputils.helpers.MapUtilsException;

import java.util.*;

/**
 * You can add, update, delete any element in the map by specifying the path in the following format.
 * <pre>{@code
 * Example 1:
 *  person.address.zip - will perform operations on the element at that location in the map.
 *  Map
 *      person: address
 *          address : zip
 *              zip : <value> -- Performs the operation this field
 * Example 2:
 *  person.address[2].zip - will perform operations on the element at that location in the map.
 *  Map
 *      person: address[]
 *          address[1]: .....
 *          address[2] : zip
 *              zip : <value> -- Performs the operation this field
 * }</pre>
 */
public class MapUpdate {

    private static final String PATH_DELIMITER_REGEX = "[.]";
    private static final String LIST_INDICATOR = "[]";

    /**
     * Currently it has the basic implementation. It can set a value at a specific map path
     * If there is a list in the path, you need to specify it as []
     * <pre>
     *  Example:
     *  name.friends[].address.city
     *  The above path will get the first friend's city address.
     * </pre>
     * TODO: Need to handle objects inside the list, similar to the "set" method implementation.
     *
     * @param mapPath path to the field in Map.
     * @param map Source map that needs to be searched
     * @return the value at the path specified will be returned, if the path des not exist, it will return null.
     */
    public static Object get(final String mapPath, final Map map) {
        if (null != mapPath) {
            String[] sourcePathArray = mapPath.split(PATH_DELIMITER_REGEX);
            MapUpdate mapUpdate = new MapUpdate();
            return mapUpdate.get(sourcePathArray, map);
        }
        return null;
    }

    /**
     * <pre>
     * Possible values for destination path
     * 1. name - field at the root level
     * 2. primaryAddress.street - field in an inner object
     * 3. associatedAddresses[] - add an (any)object to a list, object will be added at the end of the list
     * 4. associatedAddresses[1] - will add associated object at location 1 in the list,
     *    if there is an object that is already existing at that location in that list, then it will be overwritten}
     * 5. associatedAddresses[{state=CA}].street street - address will be updated with the value where associatedAddresses state is california
     * 5. associatedAddresses[{state:CA}, {zip:94599}].street - street address will be updated with the value where associatedAddresses state is california and zip is 94599
     * 6. friends[{name=Art Venere}].age - updates the age of a friend whos name is "Art Venere"
     * 7. friends[{name=Art Venere}].primaryAddress.street
     * 8. friends[{name=Art Venere}].associatedAddresses[] {associated address of the friend should be added at the end of the list, if exists, override}
     * 9. friends[{name=Art Venere}].associatedAddresses[{state=CA}].street
     * 10. friends[{name=Art Venere}].associatedAddresses[{state=CA},{city:Irving}].street
     * Current implementation will assume that the map has 1. map or 2. list or any terminal object (String/Integer/..) as the value for any given key.
     * Current implementation does not support Set or custom objects in the Map for this operation.
     * </pre>
     * <p>
     *
     * @param mapPath Path in the Map
     * @param sourceMap the map that needs to be updated
     * @param value the alue that needs to be updated with in the path specified.
     */
    public static void set(final String mapPath, final Map sourceMap, final Object value) {
        if (null != mapPath && null != sourceMap) {
            String[] sourcePathArray = mapPath.split(PATH_DELIMITER_REGEX);
            List<String> targetList = Arrays.asList(sourcePathArray);
            Queue<String> paths = new LinkedList<>();
            paths.addAll(targetList);
            MapUpdate mapUpdate = new MapUpdate();
            mapUpdate.set(paths, sourceMap, value, null);
        } else {
            throw new MapUtilsException("Not a valid input, mapPath and sourceMap are mandatory fields.");
        }
    }

    /**
     * Currently it has the basic implementation. It can set a value at a specific map path
     * TODO: Need to handle onbects inside the list, similar to the "set" method implementation.
     *
     * @param sourcePathArray path to the field in Map.
     * @param sourceMap Source map that needs to be searched
     * @return the value at the path specified will be returned, if the path des not exist, it will return null.
     */
    private Object get(final String[] sourcePathArray, final Map sourceMap) {
        try {
            if (sourcePathArray.length == 0 || sourceMap.get(sourcePathArray[0]) == null) {
                return null;
            }
            if (sourcePathArray.length > 1) {
                if (sourcePathArray[0].contains(LIST_INDICATOR) && sourceMap.get(sourcePathArray[0]) instanceof List) {
                    return sourceMap.get(sourcePathArray[0]);
                }
                if (!(sourceMap.get(sourcePathArray[0]) instanceof Map)) {
                    return null;
                } else {
                    String[] subArray = Arrays.copyOfRange(sourcePathArray, 1, sourcePathArray.length);
                    return get(subArray, (Map) sourceMap.get(sourcePathArray[0]));
                }
            } else {
                return sourceMap.get(sourcePathArray[0]);
            }
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * @param pathQueue  if the destinationPath is null or empth String, MapUtilsException will be thrown.
     * @param sourceMap  if the destinationMap is null then throw map utils exception
     * @param fieldValue if the value is not, then the field specified in the destinationPath will be removed from the map (if there are no filters in the path).
     */
    private void set(final Queue<String> pathQueue, final Map sourceMap, final Object fieldValue, final String processPath) {
        String originalKey = pathQueue.remove();
        originalKey = originalKey.trim();
        String newProcessPath = (null == processPath ? "" : processPath + ".") + originalKey;
        // Only the following three scenatios handled at this time
        // 1. map inside map
        // 2. list inside map
        // 3. map inside list
        // List inside List, multi dimensional array is not yet supported.
        if (!originalKey.matches(LIST_MATCH)) {
            // Processing Map
            if (pathQueue.size() > 0) {
                // intermediate field
                Object innerElement = sourceMap.get(originalKey);
                if (null != innerElement && innerElement instanceof Map) {
                    // if the map exists, process the next element in the path
                    set(pathQueue, (Map) innerElement, fieldValue, newProcessPath);
                } else if (null == innerElement) {
                    Map newMap = new HashMap<>();
                    sourceMap.put(originalKey, newMap);
                    // if the object is not found, then create the object and continue to process the next element in path
                    set(pathQueue, newMap, fieldValue, newProcessPath);
                } else {
                    // If the object type is not map, then this process is not supported at this time.
                    throw new MapUtilsException(originalKey + " is not a Map. Its object type is " + innerElement.getClass().getName() + ". At this time only maps and lists are supported.");
                }
            } else {
                // terminal field
                if (fieldValue == null) {
                    // if the field value is null, then we need to remove the element from the map.
                    sourceMap.remove(originalKey);
                } else {
                    // update the field with the value provided
                    sourceMap.put(originalKey, fieldValue);
                }
            }
        } else {
            // Processing List
            String actualKey = originalKey.substring(0, originalKey.lastIndexOf(OPEN_SQUARE));
            Object innerElement = sourceMap.get(actualKey);
            String filterCriteria = originalKey.substring(originalKey.lastIndexOf(OPEN_SQUARE), originalKey.lastIndexOf(CLOSE_SQUARE) + 1);
            if (null != innerElement) {
                if (innerElement instanceof List) {
                    set(pathQueue, (List) innerElement, fieldValue, filterCriteria, newProcessPath);
                } else {
                    throw new MapUtilsException(newProcessPath + " is not a list. Its object type is " + innerElement.getClass().getName());
                }
            } else {
                innerElement = new LinkedList<>();
                sourceMap.put(actualKey, innerElement);
                set(pathQueue, (List) innerElement, fieldValue, filterCriteria, newProcessPath);
            }
        }
    }

    private static final String LIST_MATCH = ".*\\[.*\\]";
    private static final String FILTER_MATCH = "\\{.*\\}";
    private static final String NUMBER_MATCH = "[0-9]+";
    private static final String OPEN_CURLY = "{";
    private static final String CLOSE_CURLY = "}";
    private static final String OPEN_SQUARE = "[";
    private static final String CLOSE_SQUARE = "]";
    private static final String EQUAL = "=";


    /**
     *
     * @param pathQueue - elements that are still need to be processed in the path queue
     * @param sourceList - list of input elements used to process the path
     * @param fieldValue - value of the field that needs to be updated
     * @param parameters - list of parameters that are used to filter the map to update the right element
     * @param processPath - the path that is already navigated to process this element
     */
    private void set(final Queue<String> pathQueue, final List sourceList, final Object fieldValue, final String parameters, final String processPath) {
        // handle multi dimension array with object parameters
        String innerParameters = parameters.substring(1, parameters.length() - 1);
        if (innerParameters.matches(FILTER_MATCH)) {
            String[] individualparameters = innerParameters.trim().split(CLOSE_CURLY);
            Map<String, String> filterCriteria = new HashMap<>();
            for (String processCurrentParameter : individualparameters) {
                processCurrentParameter = processCurrentParameter.substring(processCurrentParameter.indexOf(OPEN_CURLY) + 1);
                String[] keyValue = processCurrentParameter.split(EQUAL);
                filterCriteria.put(keyValue[0].trim(), keyValue[1].trim());
            }
            for (Object currentElementObject : sourceList) {
                Map currentListElement = (Map) currentElementObject;
                boolean match = true;
                for (Map.Entry<String, String> currentFilter : filterCriteria.entrySet()) {
                    if (!currentListElement.containsKey(currentFilter.getKey()) || !currentListElement.get(currentFilter.getKey()).toString().equals(currentFilter.getValue())) {
                        match = false;
                    }
                }
                if (match) {
                    if (pathQueue.size() == 0 && null == fieldValue) {
                        // TODO: Remove the element from list, tricky process.
                        // This is the scenario, where we found the element in the path, and we have to remove this element as the value of this element is null.
                    } else if (pathQueue.size() == 0) {
                        throw new MapUtilsException(processPath + ": Found the element in this path, but to assign the value, we also need a key.");
                    } else {
                        set(pathQueue, currentListElement, fieldValue, processPath);
                    }
                }
            }
        } else if (innerParameters.matches(NUMBER_MATCH)) {
            int listLocation = Integer.parseInt(innerParameters);
            if (pathQueue.size() > 0) {
                if (sourceList.size() > listLocation && null != sourceList.get(listLocation)) {
                    set(pathQueue, (Map) sourceList.get(listLocation), fieldValue, processPath);
                } else {
                    if (sourceList.size() <= listLocation) {
                        for (int i = sourceList.size() + 1; i <= listLocation + 1; i++) {
                            sourceList.add(new HashMap());
                        }
                    }
                    set(pathQueue, (Map) sourceList.get(listLocation), fieldValue, processPath);
                }
            } else {
                // TODO: Hanlde multi dimensional array
                sourceList.add(fieldValue);
            }
        } else {
            if (pathQueue.size() > 0) {
                if (sourceList.size() > 0 && null != sourceList.get(0)) {
                    set(pathQueue, (Map) sourceList.get(0), fieldValue, processPath);
                } else {
                    sourceList.add(0, new HashMap<>());
                    set(pathQueue, (Map) sourceList.get(0), fieldValue, processPath);
                }
            } else {
                // TODO: Hanlde multi dimensional array
                if (fieldValue != null) {
                    sourceList.add(fieldValue);
                } else {
                    // TODO: handle null on an open ended list
                }
            }
        }

    }

}

