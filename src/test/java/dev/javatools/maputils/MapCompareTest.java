package dev.javatools.maputils;

import dev.javatools.maputils.helpers.Format;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapCompareTest {

    private ClassLoader classLoader = getClass().getClassLoader();
    Map sampleInput;
    Map sameAsSampleInput;
    Map notSameAsSampleInput;

    @BeforeEach
    void setUp() throws IOException {
        Path jsonSampleInputFilePath = Path.of(classLoader.getResource("mapCompare/sample-input.json").getPath());
        String sampleJsonInputString = Files.readString(jsonSampleInputFilePath);
        sampleInput = MapCreator.create(sampleJsonInputString, Format.JSON);

        Path jsonSameAsSampleInputFilePath = Path.of(classLoader.getResource("mapCompare/same-as-sample-input.json").getPath());
        String sameAsSampleJsonInputString = Files.readString(jsonSameAsSampleInputFilePath);
        sameAsSampleInput = MapCreator.create(sameAsSampleJsonInputString, Format.JSON);

        Path jsonNotSameAsSampleInputFilePath = Path.of(classLoader.getResource("mapCompare/not-same-as-sample-input.json").getPath());
        String notSameAsSampleJsonInputString = Files.readString(jsonNotSameAsSampleInputFilePath);
        notSameAsSampleInput = MapCreator.create(notSameAsSampleJsonInputString, Format.JSON);
    }

    @Test
    void equal() {
        assertTrue(MapCompare.equal(sampleInput, sameAsSampleInput));
        assertFalse(MapCompare.equal(sampleInput, notSameAsSampleInput));
    }
}