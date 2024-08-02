package me.about.widget.cache.enums;


import lombok.Getter;

@Getter
public enum LogicRelation {

    AND("AND"),

    OR("OR");

    private final String value;

    LogicRelation(String value) {
        this.value = value;
    }

}
