package dev.javatools.maputils;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.javatools.maputils.helpers.Format;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapPropertyTest {

    private final ClassLoader classLoader = getClass().getClassLoader();
    ObjectMapper objectMapper = new ObjectMapper();
    private Map sampleInput;

    @BeforeEach
    void setUp() throws IOException {
        Path jsonSampleInputFilePath = Path.of(classLoader.getResource("mapPath/sample-input.json").getPath());
        String sampleJsonInput = Files.readString(jsonSampleInputFilePath);
        sampleInput = MapCreator.create(sampleJsonInput, Format.JSON);

    }

    @Test
    public void stringFilterTest() {
        final String LIST_MATCH = ".*\\[.*\\]";
        final String OPEN_SQUARE = "[";
        final String CLOSE_SQUARE = "]";
        String currentValue1 = "friends[{name=Josephine Darakjy}]";
        assertTrue(currentValue1.matches(LIST_MATCH));
        assertEquals("friends", currentValue1.substring(0, currentValue1.indexOf("[")));
        assertEquals("{name=Josephine Darakjy}", currentValue1.substring(currentValue1.lastIndexOf(OPEN_SQUARE) + 1, currentValue1.lastIndexOf(CLOSE_SQUARE)));
        String currentValue11 = "friends[{name=Josephine Darakjy}, {age=25}]";
        assertTrue(currentValue11.matches(LIST_MATCH));
        String currentValue2 = "friends[1]";
        assertTrue(currentValue2.matches(LIST_MATCH));
        assertEquals("1", currentValue2.substring(currentValue2.lastIndexOf(OPEN_SQUARE) + 1, currentValue2.lastIndexOf(CLOSE_SQUARE)));
        assertTrue(currentValue2.matches(LIST_MATCH));
        String currentValue3 = "friends[]";
        assertEquals("", currentValue3.substring(currentValue3.lastIndexOf(OPEN_SQUARE) + 1, currentValue3.lastIndexOf(CLOSE_SQUARE)));
    }

    @Test
    void getTest_01() {
        Object value = MapProperty.get("friends[].associatedAddresses[].city", sampleInput);
        assertEquals("Los Angeles", value);
    }

    @Test
    void getTest_02() {
        Object value = MapProperty.get("name", sampleInput);
        assertEquals("James Butt", value);
    }

    @Test
    void getTest_03() {
        Object value = MapProperty.get("age", sampleInput);
        assertEquals(25, value);
    }

    @Test
    void getTest_04() {
        Object value = MapProperty.get("friends[].name", sampleInput);
        assertEquals("Josephine Darakjy", value);
    }

    @Test
    void getTest_05() {
        Object value = MapProperty.get("friends[1].name", sampleInput);
        assertEquals("Art Venere", value);
    }

    @Test
    void getTest_06() {
        Object value = MapProperty.get("friends[{name=Lenna Paprocki}].associatedAddresses[].city", sampleInput);
        assertEquals("Aston", value);
    }

    @Test
    void getTest_07() {
        Object value = MapProperty.get("friends[{name=Lenna Paprocki}].associatedAddresses[2].zip", sampleInput);
        assertEquals("75062", value);
    }

    @Test
    void getTest_08() {
        Object value = MapProperty.get("friends[].associatedAddresses[].city", sampleInput);
        assertEquals("Los Angeles", value);
    }

    @Test
    void getTest_09() {
        Object value = MapProperty.get("friends[].associatedAddresses[{state=TX}].city", sampleInput);
        assertEquals("Laredo Webb", value);
    }

    @Test
    void getTest_10() {
        Object value = MapProperty.get("friends[3].associatedAddresses[2].city", sampleInput);
        assertEquals("Shawnee", value);
    }

    @Test
    void getTest_11() {
        Object value = MapProperty.get("primaryAddress.city", sampleInput);
        assertEquals("New Orleans", value);
    }

    @Test
    void getTest_12() {
        Object value = MapProperty.get("primaryAddress.city", sampleInput);
        assertEquals("New Orleans", value);
    }

    @Test
    void getTest_13() {
        Object value = MapProperty.get("spouse.children.spouse.name", sampleInput);
        assertEquals("Francine Vocelka", value);
    }

    @Test
    void getTest_14() {
        Object value = MapProperty.get("spouse.children.spouse.name", sampleInput);
        assertEquals("Francine Vocelka", value);
    }

    @Test
    void getTest_15() {
        Object value = MapProperty.get("friends[{name=Lenna Paprocki}].spouse.children.spouse.name", sampleInput);
        assertEquals("Jose Stockham", value);
    }

    @Test
    void getTest_16() {
        Object value = MapProperty.get("friends[2].spouse.children.spouse.age", sampleInput);
        assertEquals(5, value);
    }
}
