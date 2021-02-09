package dev.javatools.maputils;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.javatools.maputils.helpers.Constants;
import dev.javatools.maputils.helpers.Format;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapSortTest {

    private ClassLoader classLoader = getClass().getClassLoader();
    private Map emptyArrayTestRequestMap;
    private String emptyArrayTestResponseString;
    private String complexExpectedOutputString;

    private Map<String, String> listFilters;

    @BeforeEach
    public void initTests() throws IOException {
        Path sampleInputFilePath = Path.of(classLoader.getResource("mapSort/sample-input.json").getPath());
        String sampleInputString = Files.readString(sampleInputFilePath);
        emptyArrayTestRequestMap = MapCreator.create(sampleInputString, Format.JSON);

        Path simpleExpectedOutputFilePath = Path.of(classLoader.getResource("mapSort/simple-expected-output.json").getPath());
        emptyArrayTestResponseString = Files.readString(simpleExpectedOutputFilePath);

        Path complexExpectedOutputFilePath = Path.of(classLoader.getResource("mapSort/complex-expected-output.json").getPath());
        complexExpectedOutputString = Files.readString(complexExpectedOutputFilePath);

        listFilters = new HashMap<>();
        listFilters.put("associatedAddresses[]", "city");
        listFilters.put("friends[]", "name");
        listFilters.put("friends[].associatedAddresses[]", "city");
    }

    @Test
    public void getSortedMapTest() throws JsonProcessingException {
        Map sortedMap = MapSort.getSortedMap(emptyArrayTestRequestMap);
        String sortedMapString = Constants.jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sortedMap);
        assertEquals(sortedMapString, emptyArrayTestResponseString);
    }

    @Test
    public void getSortedMapWithListKeyTest() throws JsonProcessingException {
        Map sortedMap = MapSort.getSortedMap(emptyArrayTestRequestMap, listFilters);
        String sortedMapString = Constants.jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sortedMap);
        assertEquals(sortedMapString, complexExpectedOutputString);
    }

    @Test
    public void emptyListTest() throws IOException {
        Path emptyArrayTestRequest = Path.of(classLoader.getResource("mapSort/empty-array-test-request.json").getPath());
        String emptyArrayTestRequestString = Files.readString(emptyArrayTestRequest);
        emptyArrayTestRequestMap = MapCreator.create(emptyArrayTestRequestString, Format.JSON);

        Path emptyArrayTestResponsePath = Path.of(classLoader.getResource("mapSort/empty-array-test-response.json").getPath());
        emptyArrayTestResponseString = Files.readString(emptyArrayTestResponsePath);

        listFilters = new HashMap<>();
        listFilters.put("associatedAddresses[]", "city");
        listFilters.put("friends[]", "name");
        listFilters.put("friends[].associatedAddresses[]", "city");
        Map sortedMap = MapSort.getSortedMap(emptyArrayTestRequestMap, listFilters);
        String sortedMapString = Constants.jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sortedMap);
        assertEquals(sortedMapString, emptyArrayTestResponseString);
    }

    @Test
    public void fieldTypeTest() throws IOException {
        Path emptyArrayTestRequest = Path.of(classLoader.getResource("mapSort/sort-on-nexted-field-in-list-input.json").getPath());
        String emptyArrayTestRequestString = Files.readString(emptyArrayTestRequest);
        emptyArrayTestRequestMap = MapCreator.create(emptyArrayTestRequestString, Format.JSON);

        Path emptyArrayTestResponsePath = Path.of(classLoader.getResource("mapSort/sort-on-nexted-field-in-list-output.json").getPath());
        emptyArrayTestResponseString = Files.readString(emptyArrayTestResponsePath);

        listFilters = new HashMap<>();
        listFilters.put("friends[]", "primaryAddress.street");
        listFilters.put("friends[].associatedAddresses[]", "city");
        Map sortedMap = MapSort.getSortedMap(emptyArrayTestRequestMap, listFilters);
        String sortedMapString = Constants.jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sortedMap);
        assertEquals(sortedMapString, emptyArrayTestResponseString);
    }
}