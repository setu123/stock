package com.mycompany.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    public enum SignalType{
        BUY, SELL, HOLD
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
        if(o == null || o.getDate()==null)
            return -1;
        
        int returnValue;
        if(this.getDate()!=null && o.getDate()!=null)
            returnValue = (int)(this.getDate().getTime() - o.getDate().getTime()); //Sort by date
        else
            returnValue = this.getCode().compareTo(o.getCode());
        
        return returnValue; //Sort by date
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
        if(emaList == null)
            emaList = new HashMap<>();
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
