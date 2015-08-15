package com.mycompany.model;

import java.util.Date;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @date Apr 20, 2015
 * @author Setu
 */
public abstract class ItemMixin {

    @JsonIgnore
    abstract int getTrade();
    
    @JsonIgnore
    abstract int getBuyVolume();
    
    @JsonIgnore
    abstract int getSellVolume();
    
    @JsonIgnore
    abstract float getLastPrice();
    
    @JsonIgnore
    abstract float getOpenPrice();
    
    @JsonIgnore
    abstract float getClosePrice();
    
    @JsonIgnore
    abstract int getLow();
    
    @JsonIgnore
    abstract float getHigh();
    
    @JsonIgnore
    abstract Date getDate();
    
    @JsonIgnore
    abstract int getVolume();
    
    @JsonIgnore
    abstract int getValue();
    
    @JsonIgnore
    abstract float getDayHigh();
    
    @JsonIgnore
    abstract float getDayLow();
    
    @JsonIgnore
    abstract boolean isValid();
    
    @JsonIgnore
    abstract Date getLastUpdated();
    
    @JsonIgnore
    abstract Map<Integer, Float> getEmaList();
    
    @JsonIgnore
    abstract float getAdjustedClosePrice();
    
    @JsonIgnore
    abstract float getYesterdayClosePrice();
    
    @JsonIgnore 
    abstract int getIssuePrice();
    
    @JsonIgnore 
    abstract int getAdjustedVolume();
    
    @JsonIgnore 
    abstract float getAdjustedYesterdayClosePrice();
}
