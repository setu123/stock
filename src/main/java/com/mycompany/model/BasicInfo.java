package com.mycompany.model;

import java.util.Objects;

/**
 * @date Apr 22, 2015
 * @author Setu
 */

public class BasicInfo {
    private String code;
    private float low;
    private float high;

    public BasicInfo() {
    }

    public BasicInfo(String code) {
        this.code = code;
    }

    public BasicInfo(String code, float low, float high) {
        this.code = code;
        this.low = low;
        this.high = high;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.code);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BasicInfo other = (BasicInfo) obj;
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        return true;
    }

    

    @Override
    public String toString() {
        return "YearStatistics{" + "code=" + code + ", low=" + low + ", high=" + high + '}';
    }

}
