package dev.javatools.maputils;

import dev.javatools.maputils.helpers.Format;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MapToPropertiesTest {

    private ClassLoader classLoader = getClass().getClassLoader();
    private Map inputMap;
    ArrayList<String> allProperties;

    @BeforeEach
    public void setUp() throws IOException {
        Path jsonSampleInputFilePath = Path.of(classLoader.getResource("mapToProperties/sample-input.json").getPath());
        String sampleJsonInput = Files.readString(jsonSampleInputFilePath);
        inputMap = MapCreator.create(sampleJsonInput, Format.JSON);
        Path sampleOutputPropertiesFilePath = Path.of(classLoader.getResource("mapToProperties/sample-output.properties").getPath());
        allProperties = new ArrayList<>(Files.readAllLines(sampleOutputPropertiesFilePath));

    }

    @Test
    public void getPathsTest() {
        Map properties = MapToProperties.getProperties(inputMap);
        assertTrue(properties.size() == allProperties.size());
        for (Object property : properties.entrySet()) {
            Map.Entry entry = (Map.Entry) property;
            String propertyValue = entry.getKey() + "=" + entry.getValue();
            assertTrue(allProperties.contains(propertyValue));
        }
    }

}