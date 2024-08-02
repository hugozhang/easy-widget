package me.about.widget.cache.entity;


import lombok.Data;

@Data
public class ManyToManyParam {

    private String[] fieldNames;

    private Object argValue;

    private Integer argIndex;

}
