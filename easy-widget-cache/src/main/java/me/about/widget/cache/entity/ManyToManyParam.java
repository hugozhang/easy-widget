package me.about.widget.cache.entity;


import lombok.Data;

import java.util.List;

@Data
public class ManyToManyParam {

    private String[] fieldNames;

    private List<Object> arg;

    private Integer argIndex;

}
