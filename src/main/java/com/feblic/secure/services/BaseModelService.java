package com.feblic.secure.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.feblic.secure.exceptions.FeblicBadRequestException;
import com.feblic.secure.exceptions.FeblicInternalException;
import com.feblic.secure.exceptions.FeblicNotFoundException;
import com.feblic.secure.models.BaseEntityModel;
import com.feblic.secure.util.JsonUtil;
import com.feblic.secure.util.api.Page;
import com.feblic.secure.util.api.UrlParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class BaseModelService<T extends BaseEntityModel> {

    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    JsonUtil jsonUtil;

    public abstract MongoRepository<T, Long> getRepository();

    public abstract Class<T> getTypeClass();

    @Transactional
    public T find(Long id) {
        return (T) getRepository().findById(id).get();
    }

    @Transactional
    public T save(T entity) {
        return (T) getRepository().save(entity);
    }

    @Transactional
    public T update(T entity) {
        return (T) getRepository().save(entity);
    }

    @Transactional
    public T update(JsonNode jsonData) {


        if (!jsonData.has("id")) {
            throw new FeblicBadRequestException("'id' is required for updating entity.");
        }
        try {
            T oldEntity = find(jsonData.get("id").asLong());
            if (oldEntity == null) {
                throw new FeblicNotFoundException("Entity corresponding to id" + jsonData.get("id").asLong() + " doesn't exist.");
            }
            JsonNode oldJson = jsonUtil.toJson(oldEntity);
            JsonNode updatedJson = jsonUtil.overwriteJson(oldJson, jsonData);
            T updatedEntity = jsonUtil.fromJson(updatedJson, getTypeClass());
            return update(updatedEntity);
        } catch (Exception e) {
            logger.error("Error updating the entity", e);
            throw new FeblicInternalException("Error updating the entity");
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            T entity = find(id);
            entity.setDeleted(true);
            update(entity);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error in deleting entity with id " + id + " ." + e.toString());
            throw new FeblicInternalException("error in deleting entity with id " + id + ".");
        }
    }

    @Transactional
    public void permanentDelete(Long id) {
        try {
            T entity = find(id);
            getRepository().delete(entity);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error in deleting entity with id " + id + " ." + e.toString());
            throw new FeblicInternalException("error in deleting entity with id " + id + ".");
        }
    }

    @Transactional
    public T find(Long id, UrlParameters urlParameters) {
        try {
            T instance = find(id);
            attachEntity(instance, urlParameters);
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error in finding entity with id " + id + ". " + e.toString());
            throw new FeblicInternalException("error in finding entity with id " + id + ".");
        }
    }

    @Transactional
    public Page<T> getAll(UrlParameters urlParameters) {
        try {
            Page page;
            if (urlParameters.getFilter().getFilterMap().containsKey("entity_class")
                    && urlParameters.getFilter().getFilterMap().get("entity_class").size() > 0) {
                List<String> classes = new ArrayList<>(urlParameters.getFilter().getFilterMap().get("entity_class"));
                urlParameters.getFilter().getFilterMap().remove("entity_class");
                page = getAll((Class<? extends BaseEntityModel>) Class.forName(classes.get(0)), urlParameters);
            } else {
                page = getAll(getTypeClass(), urlParameters);
            }
            attachEntityBulk(page, urlParameters);
            return page;
        } catch (Exception e) {
            logger.error("Error Occured while fetching entities and attaching entities", e);
            throw new FeblicInternalException("Error fetching entities");
        }
    }

    @Transactional
    public T update(Long id, JsonNode jsonData) {
        try {
            T oldEntity = find(id);
            if (oldEntity == null) {
                throw new FeblicNotFoundException("Entity corresponding to id" + id + " doesn't exist.");
            }
            JsonNode oldJson = jsonUtil.toJson(oldEntity);
            JsonNode updatedJson = jsonUtil.overwriteJson(oldJson, jsonData);
            T updatedEntity = jsonUtil.fromJson(updatedJson, getTypeClass());
            return update(updatedEntity);
        } catch (Exception e) {
            logger.error("Error updating the entity", e);
            throw new FeblicNotFoundException("Error updating entity with id " + id + ".");
        }
    }

    @Transactional
    public Page getAll(Class<? extends BaseEntityModel> entityClass, UrlParameters urlParameters) {
        try {
            if (entityClass == null) {
                throw new FeblicInternalException("Entity class is required for performing query over DB.");
            }
            Query filterQuery = new Query();
            Query countQuery = new Query();
            Criteria criteria;

            criteria = urlParameters.getFilter().getCriteria(getTypeClass());

            //fetching total count
            if (criteria != null) {
                countQuery.addCriteria(criteria);
                filterQuery.addCriteria(criteria);
            }
            Long count = mongoTemplate.count(countQuery, entityClass);

            // Adding sort to query parameters....
//            urlParameters.getSortOrder().addSortObject(filterQuery);

            // Adding include to query....
            Set<String> allowedFields;
//            try {
//                allowedFields = entityClass.newInstance().includedFields();
//            } catch (Exception e) {
//                logger.error(e.toString());
//                throw new RuntimeException(e);
//            }
//            urlParameters.getInclude().addIncludeFields(filterQuery, allowedFields);

            filterQuery.limit((int) urlParameters.getCount());
            filterQuery.skip(urlParameters.getStart());

            return new Page<>(urlParameters.getStart(), count, mongoTemplate.find(filterQuery, entityClass));
        } catch (Exception e) {
            logger.error("Error occured while doing filtering", e);
            throw new FeblicBadRequestException("Error fetching entities");
        }
    }

    protected void attachEntity(T entity, UrlParameters urlParameters) {
    }

    protected void attachEntityBulk(Page<T> page, UrlParameters urlParameters) {
        for (T records : page.getRecords()) {
            attachEntity(records, urlParameters);
        }
    }
}
