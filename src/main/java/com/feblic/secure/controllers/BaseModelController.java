package com.feblic.secure.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.feblic.secure.models.BaseEntityModel;
import com.feblic.secure.services.BaseModelService;
import com.feblic.secure.util.JsonUtil;
import com.feblic.secure.util.api.Page;
import com.feblic.secure.util.api.UrlParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

public abstract class BaseModelController<T extends BaseEntityModel>{
    @Autowired
    JsonUtil jsonUtil;

    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public abstract BaseModelService<T> getService();

    @GetMapping("{id}")
    public ResponseEntity<T> get(@PathVariable("id") Long id, @RequestParam(value = "include", required = false) String include) {
        try {
            T entity = include==null ? getService().find(id) : getService().find(id, UrlParameters.getInstance().buildInclude(include));
            return new ResponseEntity<>(entity, HttpStatus.OK);
        } catch (RuntimeException rex){
            logger.error("Error Getting " + getService().getTypeClass() + " entity with id: "+id, rex);
            return null;
        }
    }

    @PostMapping()
    public ResponseEntity<T> create(@Valid @RequestBody T requestBody) {
        try {
            return new ResponseEntity<>(getService().save(requestBody), HttpStatus.CREATED);
        } catch (RuntimeException rex){
            logger.error("Error Adding " + getService().getTypeClass() + " entity  :" + requestBody, rex);
            return null;
        }
    }

    @PutMapping()
    public ResponseEntity<T> update(@Valid @RequestBody T requestBody) {
        try {
            return new ResponseEntity<>(getService().update(requestBody), HttpStatus.OK);
        } catch (RuntimeException rex) {
            logger.error("Error Updating " + getService().getTypeClass() + " entity  :" + requestBody, rex);
            return null;
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<T> update(@PathVariable("id") Long id, @RequestBody JsonNode jsonNode) {
        try {
            return new ResponseEntity<>(getService().update(id, jsonNode), HttpStatus.OK);
        } catch (RuntimeException rex) {
            logger.error("Error Updating " + getService().getTypeClass() + " entity  :"+ jsonNode, rex);
            return null;
        }
    }

    @GetMapping()
    public ResponseEntity<Page<T>> getAll(@RequestParam(value = "filter", required = false) String filter, @RequestParam(value = "include", required = false) String include,
                                 @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "start", required = false) Long start,
                                 @RequestParam(value = "count", required = false) Long count, @RequestParam(value = "wild_card", required = false) String wildCardFields ) {
        try {
            UrlParameters urlParameters = new UrlParameters(filter, include, start, count);
            Page<T> page          = getService().getAll(urlParameters);
            return new ResponseEntity<>(page, HttpStatus.OK);
        } catch (RuntimeException rex){
            logger.error("Error Getting " + getService().getTypeClass() + " entity with filters: "+ filter, rex);
            return null;
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        try {
            getService().delete(id);
            return new ResponseEntity<>(getService().getTypeClass().getName() + " with id: "+ id +
                    " Deleted Successfully.",HttpStatus.OK);
        } catch (RuntimeException rex){
            logger.error("Error Deleting " + getService().getTypeClass() + " entity with id: "+id, rex);
            return null;
        }
    }
}
