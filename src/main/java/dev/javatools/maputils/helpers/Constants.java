package dev.javatools.maputils.helpers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_ENUMS_USING_TO_STRING;

public class Constants {
    public static final ObjectMapper jsonMapper = new ObjectMapper();
    public static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    static {
        jsonMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        jsonMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jsonMapper.enable(WRITE_ENUMS_USING_TO_STRING);
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jsonMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        jsonMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        yamlMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        yamlMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        yamlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        yamlMapper.enable(WRITE_ENUMS_USING_TO_STRING);
        yamlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        yamlMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        yamlMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }
}
