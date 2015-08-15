package com.mycompany.model;

import java.util.Date;
import java.util.Objects;

/**
 * @date Apr 22, 2015
 * @author Setu
 */

public class YearStatistics {
    private String code;
    private Date date;
    private float low;
    private float high;

    public YearStatistics() {
    }

    public YearStatistics(String code) {
        this.code = code;
    }

    public YearStatistics(String code, Date date, float low, float high) {
        this.code = code;
        this.date = date;
        this.low = low;
        this.high = high;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
        hash = 97 * hash + Objects.hashCode(this.date);
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
        final YearStatistics other = (YearStatistics) obj;
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        return true;
    }

    

    @Override
    public String toString() {
        return "YearStatistics{" + "code=" + code + ", date=" + date + ", low=" + low + ", high=" + high + '}';
    }

}
