package com.mycompany.model;

import com.mycompany.service.FloatSerializer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @date Apr 16, 2015
 * @author Setu
 */
public class Item extends BasicInfo implements Comparable<Item>{
    private int trade;
    private float pressure;
    private int buyVolume;
    private int sellVolume;
    private float lastPrice;
    private float openPrice;
    private float closePrice;
    private int volume;
    private float value;
    private float dayHigh;
    private float dayLow;
    private float hammer;
    private float volumeChange;
    private float cLengthChange;
    private boolean consecutiveGreen;
    private float tradeChange;
    private Date lastUpdated;
    private float RSI;
    private float yesterdayClosePrice;
    private float adjustedClosePrice;
    private Map<Integer, Float> emaList;
    private int divergence;
    private int issuePrice;
    private int adjustedVolume;
    private float adjustedYesterdayClosePrice;
    private SignalType signal;
    private float vtcRatio;
    private SignalType vtcSignal;
    private Date date;
    private float volumePerTradeChange;
    private Map<Integer, Float> smaList;
    private float yesterdayRSI;
    private float dayBeforeYesterdayRSI;
    private Map<Integer, Float> volumeChanges;
    private float gain;
    private boolean potentiality;
    private float dividentYield;
    private boolean bottom;

    public SignalType getSignal() {
        return signal;
    }

    public void setSignal(SignalType signal) {
        this.signal = signal;
    }

    public float getVtcRatio() {
        return vtcRatio;
    }

    public void setVtcRatio(float vtcRatio) {
        this.vtcRatio = vtcRatio;
    }

    public SignalType getVtcSignal() {
        return vtcSignal;
    }

    public void setVtcSignal(SignalType vtcSignal) {
        this.vtcSignal = vtcSignal;
    }

    /**
     * @return the volumePerTradeChange
     */
    public float getVolumePerTradeChange() {
        return volumePerTradeChange;
    }

    /**
     * @param volumePerTradeChange the volumePerTradeChange to set
     */
    public void setVolumePerTradeChange(float volumePerTradeChange) {
        this.volumePerTradeChange = volumePerTradeChange;
    }

    /**
     * @return the smaList
     */
    public Map<Integer, Float> getSmaList() {
        if(smaList == null)
            smaList = new HashMap<>();
        return smaList;
    }

    /**
     * @param smaList the smaList to set
     */
    public void setSmaList(Map<Integer, Float> smaList) {
        this.smaList = smaList;
    }

    /**
     * @return the yesterdayRSI
     */
    public float getYesterdayRSI() {
        return yesterdayRSI;
    }

    /**
     * @param yesterdayRSI the yesterdayRSI to set
     */
    public void setYesterdayRSI(float yesterdayRSI) {
        this.yesterdayRSI = yesterdayRSI;
    }

    /**
     * @return the dayBeforeYesterdayRSI
     */
    public float getDayBeforeYesterdayRSI() {
        return dayBeforeYesterdayRSI;
    }

    /**
     * @param dayBeforeYesterdayRSI the dayBeforeYesterdayRSI to set
     */
    public void setDayBeforeYesterdayRSI(float dayBeforeYesterdayRSI) {
        this.dayBeforeYesterdayRSI = dayBeforeYesterdayRSI;
    }

    /**
     * @return the volumeChanges
     */
    public Map<Integer, Float> getVolumeChanges() {
        if(volumeChanges == null)
            volumeChanges = new HashMap<>();
        return volumeChanges;
    }

    /**
     * @param volumeChanges the volumeChanges to set
     */
    public void setVolumeChanges(Map<Integer, Float> volumeChanges) {
        this.volumeChanges = volumeChanges;
    }

    /**
     * @return the gain
     */
    public float getGain() {
        return gain;
    }

    /**
     * @param gain the gain to set
     */
    public void setGain(float gain) {
        this.gain = gain;
    }

    /**
     * @return the potentiality
     */
    public boolean isPotentiality() {
        return potentiality;
    }

    /**
     * @param potentiality the potentiality to set
     */
    public void setPotentiality(boolean potentiality) {
        this.potentiality = potentiality;
    }

    /**
     * @return the dividentYield
     */
    public float getDividentYield() {
        return dividentYield;
    }

    /**
     * @param dividentYield the dividentYield to set
     */
    public void setDividentYield(float dividentYield) {
        this.dividentYield = dividentYield;
    }

    /**
     * @return the bottom
     */
    public boolean isBottom() {
        return bottom;
    }

    /**
     * @param bottom the bottom to set
     */
    public void setBottom(boolean bottom) {
        this.bottom = bottom;
    }
    
    public enum SignalType{
        BUY, SELL, HOLD, AVG, NA
    }

    public Item() {
    }
    
    public Item(String code) {
        super(code);
    }
    
    public int getTrade() {
        return trade;
    }

    public void setTrade(int trade) {
        this.trade = trade;
    }

    @Override
    public int compareTo(Item o) {
//        if(o instanceof Item){
//            Item anItem = (Item) o;
//            return (this.trade - anItem.trade);
//        }
//        return 0;
        if(o == null || o.getDate()==null || this.getDate()==null)
            return -1;
        
//        int returnValue;
//        if(this.getDate()!=null)
//            returnValue = (int)(this.getDate().getTime() > o.getDate().getTime()); //Sort by date
//        else
//            returnValue = this.getCode().compareTo(o.getCode());
        
        if(this.getDate().getTime()>o.getDate().getTime())
            return 1;
        else if (this.getDate().getTime()<o.getDate().getTime())
            return -1;
        
        return 0; //Sort by date
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public int getBuyVolume() {
        return buyVolume;
    }

    public void setBuyVolume(int buyVolume) {
        this.buyVolume = buyVolume;
    }

    public int getSellVolume() {
        return sellVolume;
    }

    public void setSellVolume(int sellVolume) {
        this.sellVolume = sellVolume;
    }

    public float getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(float lastPrice) {
        this.lastPrice = lastPrice;
    }

    public float getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(float openPrice) {
        this.openPrice = openPrice;
    }

    public float getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(float closePrice) {
        this.closePrice = closePrice;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Item{" + "code=" + getCode() + ", date=" + getDate() + ", trade=" + trade + ", pressure=" + pressure + ", buyVolume=" + buyVolume + ", sellVolume=" + sellVolume + ", lastPrice=" + lastPrice + ", openPrice=" + openPrice + ", closePrice=" + closePrice + ", adjustedClosePrice= " + adjustedClosePrice + ", volume=" + volume + ", value=" + value + ", dayHigh=" + dayHigh + ", dayLow=" + dayLow + ", hammer=" + hammer + ", volumeChange=" + volumeChange + ", cLengthChange=" + cLengthChange + ", consecutiveGreen=" + consecutiveGreen + ", tradeChange=" + tradeChange + ", lastUpdated=" + lastUpdated + ", RSI=" + RSI + ", yesterdayClosePrice=" + yesterdayClosePrice + '}';
    }
    
    public String getYearStatistics(){
        return super.toString();
    }

    public float getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(float dayHigh) {
        this.dayHigh = dayHigh;
    }

    public float getDayLow() {
        return dayLow;
    }

    public void setDayLow(float dayLow) {
        this.dayLow = dayLow;
    }

    public float getHammer() {
        return hammer;
    }

    public void setHammer(float hammer) {
        this.hammer = hammer;
    }

    @JsonSerialize(using = FloatSerializer.class)
    public float getVolumeChange() {
        return volumeChange;
    }

    public void setVolumeChange(float volumeChange) {
        this.volumeChange = volumeChange;
    }

    public float getcLengthChange() {
        return cLengthChange;
    }

    public void setcLengthChange(float cLengthChange) {
        this.cLengthChange = cLengthChange;
    }

    public boolean isConsecutiveGreen() {
        return consecutiveGreen;
    }

    public void setConsecutiveGreen(boolean consecutiveGreen) {
        this.consecutiveGreen = consecutiveGreen;
    }
    
    public boolean isValid(){
        if(lastPrice ==0 )
            return false;
        return true;
    }

    @JsonSerialize(using = FloatSerializer.class)
    public float getTradeChange() {
        return tradeChange;
    }

    public void setTradeChange(float tradeChange) {
        this.tradeChange = tradeChange;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public float getRSI() {
        return RSI;
    }

    public void setRSI(float RSI) {
        this.RSI = RSI;
    }

    public float getYesterdayClosePrice() {
        return yesterdayClosePrice;
    }

    public void setYesterdayClosePrice(float yesterdayClosePrice) {
        this.yesterdayClosePrice = yesterdayClosePrice;
    }

    public float getAdjustedClosePrice() {
        return adjustedClosePrice;
    }

    public void setAdjustedClosePrice(float adjustedClosePrice) {
        this.adjustedClosePrice = adjustedClosePrice;
    }

    public Map<Integer, Float> getEmaList() {
        if(emaList == null){
            emaList = new HashMap<>();
            emaList.put(9, 0f);
            emaList.put(12, 0f);
            emaList.put(26, 0f);
        }
            
        return emaList;
    }

    public void setEmaList(Map<Integer, Float> emaList) {
        this.emaList = emaList;
    }

    public int getDivergence() {
        return divergence;
    }

    public void setDivergence(int divergence) {
        this.divergence = divergence;
    }

    public int getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(int issuePrice) {
        this.issuePrice = issuePrice;
    }

    public int getAdjustedVolume() {
        return adjustedVolume;
    }

    public void setAdjustedVolume(int adjustedVolume) {
        this.adjustedVolume = adjustedVolume;
    }

    public float getAdjustedYesterdayClosePrice() {
        return adjustedYesterdayClosePrice;
    }

    public void setAdjustedYesterdayClosePrice(float adjustedYesterdayClosePrice) {
        this.adjustedYesterdayClosePrice = adjustedYesterdayClosePrice;
    }
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
