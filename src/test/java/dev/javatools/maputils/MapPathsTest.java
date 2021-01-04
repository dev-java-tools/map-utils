package dev.javatools.maputils;

import dev.javatools.maputils.helpers.Format;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MapPathsTest {
    private ClassLoader classLoader = getClass().getClassLoader();
    private Map inputMap;
    ArrayList<String> allPaths;
    ArrayList<String> allUniquePaths;

    @BeforeEach
    void setUp() throws IOException {

        Path jsonSampleInputFilePath = Path.of(classLoader.getResource("mapToPaths/sample-input.json").getPath());
        String sampleJsonInput = Files.readString(jsonSampleInputFilePath);
        inputMap = MapCreator.create(sampleJsonInput, Format.JSON);

        Path sampleOutputPropertiesFilePath = Path.of(classLoader.getResource("mapToPaths/sample-output.txt").getPath());
        allPaths = new ArrayList<>(Files.readAllLines(sampleOutputPropertiesFilePath));

        Path sampleUniquePathsOutputPropertiesFilePath = Path.of(classLoader.getResource("mapToPaths/sample-unique-paths-output.txt").getPath());
        allUniquePaths = new ArrayList<>(Files.readAllLines(sampleUniquePathsOutputPropertiesFilePath));
    }

    @Test
    void getPaths() {
        Set<String> paths = MapPaths.getPaths(inputMap);
        assertTrue(paths.size() == allPaths.size());
        for (String path : paths) {
            assertTrue(allPaths.contains(path));
        }
    }

    @Test
    void getUniquePaths() {
        Set<String> paths = MapPaths.getUniquePaths(inputMap);
        assertTrue(paths.size() == allUniquePaths.size());
        for (String path : paths) {
            assertTrue(allUniquePaths.contains(path));
        }
    }

}