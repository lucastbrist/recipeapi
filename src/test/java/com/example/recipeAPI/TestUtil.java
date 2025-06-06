package com.example.recipeAPI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;

public class TestUtil {

    public static byte[] convertObjectToJsonBytes(Object object) {
        // ObjectMapper is used to translate object into JSON
        ObjectMapper mapper = new ObjectMapper();
        // take the object and return the JSON as a byte[]
        try {
            return mapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertObjectToJsonString(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        // write the JSON in the form of a String and return
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertJsonBytesToObject(
            byte[] bytes, Class<T> clazz) {
        // ObjectReader is used to translate JSON to a Java object
        ObjectReader reader = new ObjectMapper()
                // indicate which class the reader maps to
                .readerFor(clazz);

        // read the JSON byte array and translate it into an object.
        try {
            return reader.readValue(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
