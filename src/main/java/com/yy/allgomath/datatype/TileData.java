package com.yy.allgomath.datatype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class TileData {
    private final double[][] values;

    @JsonCreator
    public TileData(@JsonProperty("values") double[][] values) {
        this.values = values;
    }

    public double[][] getValues() {
        return values;
    }

    // 디버깅용 메서드
    public int getWidth() {
        return values[0].length;
    }

    public int getHeight() {
        return values.length;
    }
}