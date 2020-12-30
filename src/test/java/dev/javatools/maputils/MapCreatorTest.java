package dev.javatools.maputils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.javatools.maputils.helpers.Format;
import dev.javatools.maputils.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


class MapCreatorTest {

    private ClassLoader classLoader = getClass().getClassLoader();
    private ObjectMapper jsonObjectMapper = new ObjectMapper();
    private ObjectMapper yamlObjectMapper = new ObjectMapper(new YAMLFactory());

    private String sampleJsonInput;
    private String expectedJsonFileOutput;
    private String expectedJsonModelOutput;
    private File jsonInputFile;

    private String sampleYamlInput;
    private String expectedYamlFileOutput;
    private String expectedYamlModelOutput;
    private File yamlInputFile;

    private Person person;

    @BeforeEach
    public void setUp() throws IOException {
        Path jsonSampleInputFilePath = Path.of(classLoader.getResource("mapCreator/sample-input.json").getPath());
        sampleJsonInput = Files.readString(jsonSampleInputFilePath);
        jsonInputFile = new File(jsonSampleInputFilePath.toAbsolutePath().toString());

        Path expectedJsonFileOutputPath = Path.of(classLoader.getResource("mapCreator/expected-json-file-output.json").getPath());
        expectedJsonFileOutput = Files.readString(expectedJsonFileOutputPath);
        Path expectedJsonModelOutputPath = Path.of(classLoader.getResource("mapCreator/expected-json-model-output.json").getPath());
        expectedJsonModelOutput = Files.readString(expectedJsonModelOutputPath);


        Path yamlSampleInputFilePath = Path.of(classLoader.getResource("mapCreator/sample-input.yml").getPath());
        sampleYamlInput = Files.readString(yamlSampleInputFilePath);
        yamlInputFile = new File(jsonSampleInputFilePath.toAbsolutePath().toString());

        Path expectedYamlFileOutputPath = Path.of(classLoader.getResource("mapCreator/expected-yaml-file-output.yml").getPath());
        expectedYamlFileOutput = Files.readString(expectedYamlFileOutputPath);
        Path expectedYamlModelOutputPath = Path.of(classLoader.getResource("mapCreator/expected-yaml-model-output.yml").getPath());
        expectedYamlModelOutput = Files.readString(expectedYamlModelOutputPath);

        person = jsonObjectMapper.readValue(sampleJsonInput, Person.class);
    }

    @Test
    void testCreateWithJsonFile() throws JsonProcessingException {
        Map response = MapCreator.create(jsonInputFile, Format.JSON);
        assertEquals(expectedJsonFileOutput, jsonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
    }

    @Test
    void testCreateWithJsonString() throws JsonProcessingException {
        Map response = MapCreator.create(sampleJsonInput, Format.JSON);
        assertEquals(expectedJsonFileOutput, jsonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
    }

    @Test
    void testCreateWithYamlFile() throws JsonProcessingException {
        Map response = MapCreator.create(yamlInputFile, Format.YAML);
        assertEquals(expectedYamlFileOutput, yamlObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
    }

    @Test
    void testCreateWithYamlString() throws JsonProcessingException {
        Map response = MapCreator.create(sampleYamlInput, Format.YAML);
        assertEquals(expectedYamlFileOutput, yamlObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
    }

    @Test
    void testCreateWithObjectJson() throws JsonProcessingException {
        Map response = MapCreator.create(person);
        assertEquals(expectedJsonModelOutput, jsonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
    }

    @Test
    void testCreateWithObjectYaml() throws JsonProcessingException {
        Map response = MapCreator.create(person);
        assertEquals(expectedYamlModelOutput, yamlObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
    }

}