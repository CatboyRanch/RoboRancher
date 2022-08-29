package com.catboyranch.roborancher.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyValueStorage<Key, Value> {
    private Key key;
    private Value value;

    public KeyValueStorage(Key key, Value value) {
        this.key = key;
        this.value = value;
    }
}
