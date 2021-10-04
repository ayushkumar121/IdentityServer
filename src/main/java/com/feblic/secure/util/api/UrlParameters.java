package com.feblic.secure.util.api;

public class UrlParameters {
    private Filter filter = new Filter(null);
    private Include include = new Include(null);
    private long start = 0;
    private long count = 25;
    private boolean wildCard = true;

    private UrlParameters() {
    }

    private UrlParameters buildUrlParam(String filterString, String includeString, Long start, Long count) {
        this.buildFilter(filterString)
                .buildInclude(includeString)
                .buildStart(start)
                .buildCount(count);
        return this;
    }

    public UrlParameters(String filterString, String includeString, Long start, Long count) {
        this.buildUrlParam(filterString, includeString, start, count);
    }


    public static UrlParameters getInstance() {
        return new UrlParameters();
    }
    // Builder Methods

    public UrlParameters buildFilter(String filter) {
        this.filter = new Filter(filter);
        return this;
    }

    public UrlParameters buildInclude(String include) {
        this.include = new Include(include);
        return this;
    }

    public UrlParameters buildStart(Long start) {
        if (start != null)
            this.start = start;
        return this;

    }

    public UrlParameters buildCount(Long count) {
        if (count != null)
            this.count = count;
        return this;
    }

    // Getters and Setters


    public void setFilter(String filter) {
        this.buildFilter(filter);
    }

    public void setInclude(String include) {
        this.buildInclude(include);
    }

    public void setStart(long start) {
        this.buildStart(start);
    }

    public void setCount(long count) {
        this.buildCount(count);
    }

    public Filter getFilter() {
        return filter;
    }

    public Include getInclude() {
        return include;
    }

    public long getStart() {
        return start;
    }

    public long getCount() {
        return count;
    }

    public boolean isWildCard() {
        return wildCard;
    }

    public void setWildCard(boolean wildCard) {
        this.wildCard = wildCard;
    }


    public static UrlParameters clone(UrlParameters urlParameters) {
        UrlParameters clonedUrlParameters = UrlParameters.getInstance();
        clonedUrlParameters.setFilter(urlParameters.getFilter().getFilterString());
        clonedUrlParameters.setInclude(urlParameters.getInclude().getIncludeString());
        clonedUrlParameters.setCount(urlParameters.getCount());
        clonedUrlParameters.setStart(urlParameters.getStart());
        clonedUrlParameters.setWildCard(urlParameters.wildCard);
        return clonedUrlParameters;
    }
}
