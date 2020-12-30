package dev.javatools.maputils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.javatools.maputils.helpers.Format;
import dev.javatools.maputils.helpers.MapUtilsException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;


/**
 * Use this for creating a Map from Json/yaml or custom java models.
 * <pre>
 *     1. Json/yaml String or
 *     2. from a custom java models that you build in your project or
 *     3. from json/yaml String that is stored in a file.
 * </pre>
 */
public final class MapCreator {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    private MapCreator() {
    }

    /**
     * Convert json string that is in the file into a Map.
     *
     * @param input, file containing the json String.
     * @param format At this time we are supporting json and yaml types
     * @return returns Map representation of the File content
     */
    public static Map create(final File input, final Format format) {
        try {
            Path tempFilePath = Path.of(input.getAbsolutePath());
            String jsonString = Files.readString(tempFilePath);
            return create(jsonString, format);
        } catch (IOException ioException) {
            throw new MapUtilsException(ioException);
        }
    }

    /**
     * Create a Map from json String
     *
     * @param input  Json String
     * @param format At this time we are supporting json and yaml types
     * @return returns Map representation of the File content
     */
    public static Map create(final String input, final Format format) {
        try {
            Map mapInput;
            if (format == Format.JSON) {
                mapInput = jsonMapper.readValue(input, Map.class);
            } else {
                mapInput = yamlMapper.readValue(input, Map.class);
            }
            return mapInput;
        } catch (JsonProcessingException jsonProcessingException) {
            throw new MapUtilsException(jsonProcessingException);
        }
    }

    /**
     * Convert any custom java model into a Map.
     *
     * @param input And custom model object that you want to convert to Map
     * @return returns Map representation of the File content
     */
    public static Map create(final Object input) {
        try {
            jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String jsonString = jsonMapper.writeValueAsString(input);
            return jsonMapper.readValue(jsonString, Map.class);
        } catch (JsonProcessingException jsonProcessingException) {
            throw new MapUtilsException(jsonProcessingException);
        }
    }

}
