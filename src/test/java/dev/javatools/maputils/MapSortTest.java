package dev.javatools.maputils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private ObjectMapper objectMapper = new ObjectMapper();
    private Map sampleInputMap;
    private String simpleExpectedOutputString;
    private String complexExpectedOutputString;

    private Map<String, String> listFilters;

    @BeforeEach
    public void initTests() throws IOException {
        Path sampleInputFilePath = Path.of(classLoader.getResource("mapSort/sample-input.json").getPath());
        String sampleInputString = Files.readString(sampleInputFilePath);
        sampleInputMap = MapCreator.create(sampleInputString, Format.JSON);

        Path simpleExpectedOutputFilePath = Path.of(classLoader.getResource("mapSort/simple-expected-output.json").getPath());
        simpleExpectedOutputString = Files.readString(simpleExpectedOutputFilePath);

        Path complexExpectedOutputFilePath = Path.of(classLoader.getResource("mapSort/complex-expected-output.json").getPath());
        complexExpectedOutputString = Files.readString(complexExpectedOutputFilePath);

        listFilters = new HashMap<>();
        listFilters.put("associatedAddresses[]", "city");
        listFilters.put("friends[]", "name");
        listFilters.put("friends[].associatedAddresses[]", "city");
    }

    @Test
    public void getSortedMapTest() throws JsonProcessingException {
        Map sortedMap = MapSort.getSortedMap(sampleInputMap);
        String sortedMapString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sortedMap);
        assertEquals(sortedMapString, simpleExpectedOutputString);
    }

    @Test
    public void getSortedMapWithListKeyTest() throws JsonProcessingException {
        Map sortedMap = MapSort.getSortedMap(sampleInputMap, listFilters);
        String sortedMapString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sortedMap);
        assertEquals(sortedMapString, complexExpectedOutputString);
    }

}