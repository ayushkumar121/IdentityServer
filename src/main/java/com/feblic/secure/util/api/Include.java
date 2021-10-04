package com.feblic.secure.util.api;

import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Include {
    private String includeString;
    private Set<String> includeFields;

    public Include(String includeString) {
        this.includeString = includeString;
        includeFields = getIncludeFields(includeString);
    }

    private Set<String> getIncludeFields(String include) {
        Set<String> result = new HashSet<>();
        if (include == null)
            return result;
        result.addAll(Arrays.asList(include.split(",")));
        return result;
    }

    public String getIncludeString() {
        return includeString;
    }

    public void setIncludeString(String includeString) {
        this.includeString = includeString;
    }

    public Set<String> getIncludeFields() {
        return includeFields;
    }

    public void setIncludeFields(Set<String> includeFields) {
        this.includeFields = includeFields;
    }

    public void addIncludeFields(Query q, Set<String> allowedFields) {
        for (String field : allowedFields) {
            q.fields().include(field);
        }
        for (String field : includeFields) {
            if (field.trim().length() > 0) q.fields().include(field);
        }

    }
}
