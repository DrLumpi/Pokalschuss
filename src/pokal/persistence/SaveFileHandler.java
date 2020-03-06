package pokal.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import pokal.model.Zug;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SaveFileHandler {
    private static ObjectMapper mapper = new ObjectMapper();

    private SaveFileHandler() {}

    public static void init() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    public static void save(final File file, final List<Zug> zuege) throws IOException {
        mapper.writeValue(file, zuege);
    }

    public static ArrayList<Zug> loadSaveFile(final File file) throws IOException {
        ArrayList<Zug> ret = mapper.readValue(file, new TypeReference<ArrayList<Zug>>() {});
        return ret == null ? new ArrayList<>() : ret;
    }
}
