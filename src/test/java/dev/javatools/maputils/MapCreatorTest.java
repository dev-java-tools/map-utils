package dev.javatools.maputils;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.javatools.maputils.helpers.Constants;
import dev.javatools.maputils.helpers.Format;
import dev.javatools.maputils.model.Person;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


class MapCreatorTest {

    private ClassLoader classLoader = getClass().getClassLoader();

    private static String sampleJsonInput;
    private static String expectedJsonFileOutput;
    private static String expectedJsonModelOutput;
    private static File jsonInputFile;

    private static String sampleYamlInput;
    private static String expectedYamlFileOutput;
    private static String expectedYamlModelOutput;
    private static File yamlInputFile;

    private static Person person;

    @BeforeAll
    public static void beforeAll() throws IOException {
        Path jsonSampleInputFilePath = Path.of(MapCreatorTest.class.getClassLoader().getResource("mapCreator/sample-input.json").getPath());
        sampleJsonInput = Files.readString(jsonSampleInputFilePath);
        jsonInputFile = new File(jsonSampleInputFilePath.toAbsolutePath().toString());

        Path expectedJsonFileOutputPath = Path.of(MapCreatorTest.class.getClassLoader().getResource("mapCreator/expected-json-file-output.json").getPath());
        expectedJsonFileOutput = Files.readString(expectedJsonFileOutputPath);
        Path expectedJsonModelOutputPath = Path.of(MapCreatorTest.class.getClassLoader().getResource("mapCreator/expected-json-model-output.json").getPath());
        expectedJsonModelOutput = Files.readString(expectedJsonModelOutputPath);


        Path yamlSampleInputFilePath = Path.of(MapCreatorTest.class.getClassLoader().getResource("mapCreator/sample-input.yml").getPath());
        sampleYamlInput = Files.readString(yamlSampleInputFilePath);
        yamlInputFile = new File(jsonSampleInputFilePath.toAbsolutePath().toString());

        Path expectedYamlFileOutputPath = Path.of(MapCreatorTest.class.getClassLoader().getResource("mapCreator/expected-yaml-file-output.yml").getPath());
        expectedYamlFileOutput = Files.readString(expectedYamlFileOutputPath);
        Path expectedYamlModelOutputPath = Path.of(MapCreatorTest.class.getClassLoader().getResource("mapCreator/expected-yaml-model-output.yml").getPath());
        expectedYamlModelOutput = Files.readString(expectedYamlModelOutputPath);

        person = Constants.jsonMapper.readValue(sampleJsonInput, Person.class);
    }

    @Test
    void testCreateWithJsonFile() throws JsonProcessingException {
        Map response = MapCreator.create(jsonInputFile, Format.JSON);
        assertEquals(expectedJsonFileOutput, Constants.jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
    }

    @Test
    void testCreateWithJsonString() throws JsonProcessingException {
        Map response = MapCreator.create(sampleJsonInput, Format.JSON);
        assertEquals(expectedJsonFileOutput, Constants.jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
    }

    @Test
    void testCreateWithYamlFile() throws JsonProcessingException {
        Map response = MapCreator.create(yamlInputFile, Format.YAML);
        assertEquals(expectedYamlFileOutput, Constants.yamlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
    }

    @Test
    void testCreateWithYamlString() throws JsonProcessingException {
        Map response = MapCreator.create(sampleYamlInput, Format.YAML);
        assertEquals(expectedYamlFileOutput, Constants.yamlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
    }

    @Test
    void testCreateWithObjectJson() throws JsonProcessingException {
        Map response = MapCreator.create(person);
        assertEquals(expectedJsonModelOutput, Constants.jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
    }

    @Test
    void testCreateWithObjectYaml() throws JsonProcessingException {
        Map response = MapCreator.create(person);
        assertEquals(expectedYamlModelOutput, Constants.yamlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
    }

}