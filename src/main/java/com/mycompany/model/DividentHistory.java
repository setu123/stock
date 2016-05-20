package com.mycompany.model;

import java.util.Date;

/**
 * @date May 22, 2015
 * @author Setu
 */
public class DividentHistory implements Comparable<DividentHistory>{
    private String code;
    private Date date;
    private DividentType type;
    private float percent;
    private float issuePrice;

    public DividentType getType() {
        return type;
    }

    public void setType(DividentType type) {
        this.type = type;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    /**
     * @return the issuePrice
     */
    public float getIssuePrice() {
        return issuePrice;
    }

    /**
     * @param issuePrice the issuePrice to set
     */
    public void setIssuePrice(float issuePrice) {
        this.issuePrice = issuePrice;
    }
    
    public enum DividentType{
        CASH, STOCK, RIGHT, SPLIT
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

    @Override
    public int compareTo(DividentHistory history) {
        if(this.getDate().equals(history.getDate())){
            //give priority to STOCK
            return history.getType().equals(DividentType.STOCK)? 1: -1;
        }
        
        boolean before = this.getDate().before(history.getDate());
        return before?-1:1;
    }

    @Override
    public String toString() {
        return "DividentHistory{" + "code=" + code + ", date=" + date + ", type=" + type + ", percent=" + percent + '}';
    }
    
    
}
