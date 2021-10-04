package com.feblic.secure.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class BaseEntityModel {
    @Id
    private long id;

    @Field("created_at")
    @JsonProperty("created_at")
    @CreatedDate
    private Date createdAt;

    @Field("updated_at")
    @JsonProperty("updated_at")
    @LastModifiedDate
    private Date updatedAt;

    @Field("created_by_id")
    @JsonProperty("created_by_id")
    @CreatedBy
    private Long createdById;

    @Field("updated_by_id")
    @JsonProperty("updated_by_id")
    @LastModifiedBy
    private Long updatedById;

    @JsonIgnore
    @Field("deleted")
    private boolean deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public Long getUpdatedById() {
        return updatedById;
    }

    public void setUpdatedById(Long updatedById) {
        this.updatedById = updatedById;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getSequenceName() {
        return "";
    }

    public Set<String> includedFields() {
        return basicIncludedFields();
    }

    protected Set<String> basicIncludedFields() {
        Set<String> allowedFields = new HashSet<String>();
        allowedFields.add("id");
        allowedFields.add("created_by_id");
        allowedFields.add("created_at");
        allowedFields.add("updated_by_id");
        allowedFields.add("updated_at");
        return allowedFields;
    }
}
