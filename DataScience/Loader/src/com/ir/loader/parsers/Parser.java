package com.ir.loader.parsers;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.json.JSONException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Created by shabbirhussain on 4/7/17.
 */
public interface Parser {
    /**
     * Parses the given file to return XContentBuilder
     * @param filePath is the file to load
     * @return a map of documents to load
     * @throws IOException
     * @throws Exception
     */
    Map<String, XContentBuilder> parseFile(Path filePath) throws Exception, IOException;
}
