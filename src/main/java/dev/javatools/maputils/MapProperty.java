package dev.javatools.maputils;

import dev.javatools.maputils.helpers.MapUtilsException;

import java.util.*;

/**
 * Path format examples
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
 */
public class MapProperty {

    private static final String PATH_DELIMITER_REGEX = "[.]";
    private static final String LIST_MATCH = ".*\\[.*\\]";
    private static final String FILTER_MATCH = "\\{.*\\}";
    private static final String NUMBER_MATCH = "[0-9]+";
    private static final String OPEN_CURLY = "{";
    private static final String CLOSE_CURLY = "}";
    private static final String OPEN_SQUARE = "[";
    private static final String CLOSE_SQUARE = "]";
    private static final String EQUAL = "=";

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
     * @param fieldPath path to the field in Map.
     * @param sourceMap Source map that needs to be searched
     * @return the value at the path specified will be returned, if the path des not exist, it will return null.
     */
    public static Object get(final String fieldPath, final Map sourceMap) {
        if (null != fieldPath) {
            String[] sourcePathArray = fieldPath.split(PATH_DELIMITER_REGEX);
            MapProperty mapProperty = new MapProperty();
            List<String> targetList = Arrays.asList(sourcePathArray);
            Queue<String> paths = new LinkedList<>();
            paths.addAll(targetList);
            return mapProperty.get(paths, sourceMap);
        } else {
            return null;
        }
    }

    private Object get(final Queue<String> pathQueue, final Map sourceMap) {
        Object currentValue = sourceMap;
        while (!pathQueue.isEmpty() && null != currentValue) {
            String currentElement = pathQueue.remove();
            if (currentElement.matches(LIST_MATCH)) {
                currentValue = processList(currentElement, (Map) currentValue);
            } else {
                currentValue = ((Map) currentValue).get(currentElement);
            }
        }
        return currentValue;
    }

    private Object processList(final String currentElement, final Map sourceMap) {
        String filterCriteria = currentElement.substring(currentElement.lastIndexOf(OPEN_SQUARE) + 1, currentElement.lastIndexOf(CLOSE_SQUARE));
        String currentKey = currentElement.substring(0, currentElement.indexOf("["));
        if (null == sourceMap.get(currentKey)) {
            return null;
        }
        if (!(sourceMap.get(currentKey) instanceof List)) {
            throw new MapUtilsException("Did not found list at property: " + currentElement);
        }
        Object listObject = sourceMap.get(currentKey);
        List list = (List) listObject;
        if (list.size() == 0) {
            return null;
        }
        if (filterCriteria.matches(FILTER_MATCH)) {
            List results = processFilterList(filterCriteria, list);
            if (results.size() > 0) {
                return results.get(0);
            } else {
                return null;
            }
        } else if (filterCriteria.matches(NUMBER_MATCH)) {
            return getNumberedElementFromList(list, Integer.parseInt(filterCriteria));
        } else {
            if (filterCriteria.equals("")) {
                return getFirstElementFromList(list);
            } else {
                throw new MapUtilsException("wrong filter criteria on list:" + currentElement);
            }
        }
    }

    private Object getFirstElementFromList(List sourceList) {
        return getNumberedElementFromList(sourceList, 0);
    }

    private Object getNumberedElementFromList(List sourceList, int itemPosition) {
        if (sourceList.size() > itemPosition) {
            return sourceList.get(itemPosition);
        } else {
            return null;
        }
    }

    private List processFilterList(String filterCriteria, List<Map<String, Object>> sourceList) {
        List matchedElements = new LinkedList();
        Map<String, String> filters = getFilters(filterCriteria);
        for (Map<String, Object> currentMap : sourceList) {
            boolean match = true;
            for (Map.Entry filterItem : filters.entrySet()) {
                if (null != currentMap.get(filterItem.getKey())) {
                    if (!currentMap.get(filterItem.getKey()).equals(filterItem.getValue())) {
                        match = false;
                        break;
                    }
                } else {
                    match = false;
                    break;
                }
            }
            if (match) {
                matchedElements.add(currentMap);
            }
        }
        return matchedElements;
    }

    private Map<String, String> getFilters(String originalFilters) {
        Map<String, String> filters = new HashMap<>();
        String[] filterArray = originalFilters.split(CLOSE_CURLY);
        for (String individualFilter : filterArray) {
            if (individualFilter.trim().indexOf(OPEN_CURLY) >= 0) {
                individualFilter = individualFilter.substring(individualFilter.indexOf(OPEN_CURLY) + 1);
                String[] keyValue = individualFilter.split(EQUAL);
                if (keyValue.length != 2) {
                    throw new MapUtilsException("Not a valid filter: " + individualFilter);
                } else {
                    filters.put(keyValue[0], keyValue[1]);
                }
            } else {
                throw new MapUtilsException("Not a valid filter: " + individualFilter);
            }
        }
        return filters;
    }

}
