package com.ir.loader.parsers;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by shabbirhussain on 4/7/17.
 */
public class JSONParser implements Parser {
     public Map<String, XContentBuilder> parseFile(Path file) throws JSONException, IOException {
        Map<String, XContentBuilder> result = new HashMap<>();

        String content = new String(Files.readAllBytes(file));
        JSONObject jFile = new JSONObject(content);

        boolean isTest = true;
        if(jFile.has("TrainingData")){
            jFile = jFile.getJSONObject("TrainingData");
            isTest = false;
        }else{
            jFile = jFile.getJSONObject("TestData");
            isTest = true;
        }

        int i=0;
        for(String key: jFile.keySet()) {
            JSONObject jObj = jFile.getJSONObject(key);
            //System.out.println((i++) + jObj.toString());
            XContentBuilder builder = jsonBuilder()
                    .startObject()
                    .field("docNo", key)
                    .field("webPublicationDate" 	, jObj.get("webPublicationDate"))
                    .field("topics"				, jObj.get("topics"))
                    .field("bodyText"				, jObj.get("bodyText"))
                    .field("isTest"               , isTest)
                    .endObject();
            result.put(key, builder);
        }
        return result;
    }
}
