package com.xively.internal.rest.blueprint;


public class BlueprintMeta {
    public Integer count;
    public Integer pageSize;
    public Integer page;
    public String sortBy;
    public String sortOrder;

    @Override
    public String toString() {
        return "BlueprintMeta{" +
                "count=" + count +
                ", pageSize=" + pageSize +
                ", page=" + page +
                ", sortBy='" + sortBy + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                '}';
    }
}
