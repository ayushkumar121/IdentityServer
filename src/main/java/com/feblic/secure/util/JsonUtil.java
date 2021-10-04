package com.feblic.secure.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.feblic.secure.exceptions.FeblicBadRequestException;
import com.feblic.secure.exceptions.FeblicUnprocessableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class JsonUtil {

    @Autowired
    ObjectMapper objectMapper;

    public JsonNode toJson(Object object) {
        try {
            return objectMapper.convertValue(object, JsonNode.class);
        } catch (IllegalArgumentException iae) {
            throw new FeblicUnprocessableException("Error converting to Json: ", iae);
        }
    }

    public String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException jpe) {
            throw new FeblicUnprocessableException("Error converting to Json: ", jpe);
        }
    }

    public JsonNode parse(String jsonData) {
        try {
            return objectMapper.readValue(jsonData, JsonNode.class);
        } catch (IOException ex) {
            throw new FeblicUnprocessableException("Error Parsing Json String to JsonNode.", ex);
        }
    }

    public <T> T fromJson(JsonNode jsonNode, Class<T> clazz) {
        try {
            return objectMapper.treeToValue(jsonNode, clazz);
        } catch (JsonProcessingException jpe) {
            throw new FeblicUnprocessableException("Error Parsing JsonNode to Class" + clazz.getName(), jpe);
        }
    }

    public ObjectNode newObject() {
        return objectMapper.createObjectNode();
    }

    /**
     * The following method will iterate over all values of newJson and if we find a
     * property whose type is array and the same property is provided in the input array
     * then we will overwrite those array properties.
     *
     * @param updatedJson JsonNode object from which we need to remove array fields.
     * @param newJson     The JsonNode object which came as input from API
     * @param filteredMap
     */
    public void iterate(JsonNode updatedJson, JsonNode newJson, Map<String, Object> filteredMap) {
        for (Iterator<Map.Entry<String, JsonNode>> it = updatedJson.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> elt = it.next();
            if (elt.getValue().getNodeType().equals(JsonNodeType.OBJECT) && newJson.has(elt.getKey())) {
                Map<String, Object> subMap = new HashMap<String, Object>();
                iterate(elt.getValue(), newJson.get(elt.getKey()), subMap);
                filteredMap.put(elt.getKey(), subMap);
                continue;
            }
            if (elt.getValue().isArray() && newJson.has(elt.getKey())) {
                filteredMap.put(elt.getKey(), newJson.get(elt.getKey()));
            } else {
                filteredMap.put(elt.getKey(), elt.getValue());
            }
        }
    }

    public JsonNode overwriteJson(JsonNode oldJson, JsonNode newJson) {
        try {
            objectMapper.setDefaultMergeable(false);
            ObjectReader objectReader = objectMapper.readerForUpdating(oldJson);
            JsonNode updatedNodeWithMergeArray = objectReader.readValue(newJson);
            Map<String, Object> mapWithOverwrittenValues = new HashMap<String, Object>();
            iterate(updatedNodeWithMergeArray, newJson, mapWithOverwrittenValues);
            JsonNode result = objectMapper.convertValue(mapWithOverwrittenValues, JsonNode.class);
            return result;
        } catch (Exception e) {
            throw new FeblicUnprocessableException("Error overwriting new updated object", e);
        }
    }

    public <T> T getObjectFromJsonString(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            throw new FeblicBadRequestException("Error Parsing JsonString to Class" + clazz.getName(), e);
        }
    }
}
