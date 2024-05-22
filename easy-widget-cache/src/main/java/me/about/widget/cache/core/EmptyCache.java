package me.about.widget.cache.core;

import java.util.concurrent.TimeUnit;

public class EmptyCache implements Cache {

    @Override
    public Object get(Object key) {
        return null;
    }

    @Override
    public void put(Object key, Object value, Long expire, TimeUnit timeUnit) {

    }

    @Override
    public void remove(Object key) {

    }
}
