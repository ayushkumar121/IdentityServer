package com.feblic.secure.util.api;

import com.feblic.secure.exceptions.FeblicInternalException;
import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class Filter {
    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Map<String, HashSet<String>> filterMap;
    private String filterString;

    public Filter(String filterString) {
        this.filterString = filterString;
        this.filterMap = new HashMap<>();
        parseFilterMap();
    }

    private void parseFilterMap() {
        if (filterString == null)
            return;
        try {
            String[] filterArray = filterString.split(";");

            for (String filter : filterArray) {
                String[] splitFilter = filter.split(":");
                HashSet<String> filterValues = new HashSet<>(Arrays.asList(splitFilter[1].split(",")));
                this.filterMap.put(splitFilter[0], filterValues);
            }
        } catch (Exception ex) {
            logger.trace("Error parsing filter String to HashMap " , ex);
        }
    }

    public Map<String, HashSet<String>> getFilterMap() {
        return filterMap;
    }

    public void setFilterMap(Map<String, HashSet<String>> filterMap) {
        this.filterMap = filterMap;
    }

    public String getFilterString() {
        return filterString;
    }

    public void setFilterString(String filterString) {
        this.filterString = filterString;
    }

    @Override
    public String toString() {
        StringBuilder filterStringTemp = new StringBuilder();
        for (Map.Entry<String, HashSet<String>> entry : filterMap.entrySet()) {
            filterStringTemp.append(entry.getKey()).append(":");
            filterStringTemp.append(Joiner.on(",").join(entry.getValue()));
            filterStringTemp.append(";");
        }
        this.filterString = filterStringTemp.toString();

        return filterString;
    }

    private String getFilterKey(String entryKey){
        if( entryKey.startsWith("from_"))
            return  entryKey.substring(5);
        if( entryKey.startsWith("to_"))
            return  entryKey.substring(3);
        if( entryKey.startsWith("gte_"))
            return  entryKey.substring(4);
        if( entryKey.startsWith("lte_"))
            return entryKey.substring(4);
        if( entryKey.startsWith("eq_"))
            return entryKey.substring(3);
        if( entryKey.startsWith("neq_"))
            return entryKey.substring(4);
        return entryKey;
    }

    private Class getFieldTypeByValue(Set<String> val) {
        if (val.size() != 1) {
            return String.class;
        }
        String value = val.iterator().next();
        try {
            Double.parseDouble(value);
            return Double.class;
        } catch (NumberFormatException nfe) {
            if ("true".equals(value) || "false".equals(value))
                return Boolean.class;
            return String.class;
        }
    }

    private Class getFieldTypeInAClass(String snakeCaseFieldName, Set<String> val, Class<?> typeClass) {
        String fieldName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, snakeCaseFieldName);
        Field[] fields = typeClass.getDeclaredFields();
        if(fields.length == 0) {
            return getFieldTypeByValue(val);
        }
        Field[] superClassFields = typeClass.getSuperclass().getDeclaredFields();
        for(Field field: fields) {
            if(field.getName().equals(fieldName)) {
                if(field.getGenericType() instanceof ParameterizedType){
                    ParameterizedType pt = (ParameterizedType) field.getGenericType();
                    return (Class) pt.getActualTypeArguments()[0];
                }else{
                    return field.getType();
                }
            }
        }
        for(Field field: superClassFields) {
            if(field.getName().equals(fieldName)) {
                return field.getType();
            }
        }
        return String.class;
    }

    private Class getFieldType(String key, Set<String> val, Class<?> typeClass) {
        if(key.contains(".")) {
            String subField = key.split("\\.")[0];
            String remainingField = key.substring(subField.length()+1);
            Class subFieldClass = getFieldTypeInAClass(subField, val, typeClass);
            return getFieldType(remainingField, val, subFieldClass);
        }else {
            return getFieldTypeInAClass(key, val, typeClass);
        }
    }

    private Set<Object> getStringSet(Set<String> val) {
        return new HashSet<Object>(val);
    }

    private Set<Object> getDoubleSet(Set<String> val) {
        Set<Object> set = new HashSet<Object>();
        for(String value: val) {
            set.add(Double.parseDouble(value));
        }
        return set;
    }

    private Set<Object> getBooleanSet(Set<String> val) {
        Set<Object> set = new HashSet<Object>();
        for(String value: val) {
            set.add(Boolean.parseBoolean(value));
        }
        return set;
    }

    private Set<Object> getLongSet(Set<String> val) {
        Set<Object> set = new HashSet<Object>();
        for(String value: val) {
            set.add(Long.parseLong(value));
        }
        return set;
    }

    private Set<Object> getDateSet(Set<String> val) {
        Set<Object> set = new HashSet<Object>();
        for(String value: val) {
            set.add(new DateTime(value));
        }
        return set;
    }

    private Set<Object> getValuesInProperFormat(Class classType, Set<String> val){
        if(classType.equals(String.class)) {
            return getStringSet(val);
        }
        if(classType.equals(Double.class)) {
            return getDoubleSet(val);
        }
        if(classType.equals(long.class) || classType.equals(Long.class)) {
            return getLongSet(val);
        }
        if(classType.equals(Boolean.class)) {
            return getBooleanSet(val);
        }
        if(classType.equals(Date.class)) {
            return getDateSet(val);
        }
        return getStringSet(val);
    }

    private Criteria getCriteriaForDate(String key, Set<String> val){
        List<Criteria> orCriteria = new ArrayList<>();
        for(String value: val){
            // Here we are creating two date objects and adding criteria of GTE and LT on these two date objects
            DateTime startDate = new DateTime(value);
            DateTime endDate = startDate.plusDays(1);
            Criteria criteria = Criteria.where(key).gte(startDate).lt(endDate);
            orCriteria.add(criteria);
        }
        return new Criteria().orOperator(orCriteria.toArray(new Criteria[orCriteria.size()]));
    }

    private Criteria getCriteriaWithCaseInSensitiveRegex(String key, Class fieldType, Set<String> val){

        Set<Object> values = getValuesInProperFormat(fieldType, val);
        List<Criteria> orCriterion = new ArrayList<Criteria>();
        if(fieldType.equals(Date.class)){
            return getCriteriaForDate(key, val);
        }
        if(!fieldType.equals(String.class)){
            return Criteria.where(key).in(values);
        }
        // if field name present in wild_card param we will do only partial search. By default it will be exact search
        for(Object object: values){
            String regex = "";

            regex = String.format("^%s$",object.toString());
            orCriterion.add(
                    Criteria.where(key).regex(regex,"i")
            );
        }
        return new Criteria().orOperator(orCriterion.toArray(new Criteria[orCriterion.size()]));
    }

    private Criteria buildCriteria(String key, Set<String> val, Class<?> typeClass) {
        Class fieldType = getFieldType(key, val, typeClass);
        if(key.startsWith("from_")) {
            return Criteria.where(getFilterKey(key)).gte(new DateTime(val.iterator().next()));
        }else if ( key.startsWith("to_")){
            return Criteria.where(getFilterKey(key)).lte(new DateTime(val.iterator().next()));
        }else if ( key.startsWith("gte_")){
            return Criteria.where(getFilterKey(key)).gte(Double.parseDouble(val.iterator().next()));
        }else if ( key.startsWith("lte_")){
            return Criteria.where(getFilterKey(key)).lte(Double.parseDouble(val.iterator().next()));
        }else {
            return getCriteriaWithCaseInSensitiveRegex(key, fieldType, val);
        }
    }

    public Criteria getCriteria(Class<?> typeClass) {
        try{
            Criteria criteria = new Criteria();
            List<Criteria> criteriaArrayList = new ArrayList<Criteria>();
            criteriaArrayList.add(Criteria.where("deleted").is(false));
            if(filterMap.size() == 0) {
                // adding only deleted flag... in case filterMap size is zero.
                return criteriaArrayList.get(0);
            }
            for(String key: filterMap.keySet()) {
                criteriaArrayList.add(buildCriteria(key, filterMap.get(key), typeClass));
            }
            criteria.andOperator(criteriaArrayList.toArray(new Criteria[criteriaArrayList.size()]));
            return criteria;
        }catch (Exception e){
            throw new FeblicInternalException("Error Occurred while querying the entity "+typeClass.getName(),e);
        }
    }
}
