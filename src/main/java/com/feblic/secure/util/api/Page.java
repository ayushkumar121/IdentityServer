package com.feblic.secure.util.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Page<T> {
    private long totalCount;
    private long size;
    private List<T> records;
    private long start;

    public Page(long start, long totalCount, List<T> records) {
        this.totalCount = totalCount;
        if (records != null) {
            this.records = records;
            this.size = records.size();
        }
        this.start = start;
    }

    @JsonProperty("_size")
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    @JsonProperty("_total_count")
    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    @JsonProperty("_start")
    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }
}