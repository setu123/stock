package com.mycompany.model;

import java.util.Objects;

/**
 * @date Apr 22, 2015
 * @author Setu
 */

public class BasicInfo {
    protected String code;
    private float yearLow;
    private float yearHigh;
    private String sector;
    private int faceValue;
    private int totalSecurity;
    private float authorizedCapital;
    private float paidUpCapital;
    private String yearEnd;
    private float reserve;
    private float PE;
    private String category;
    private SharePercentage sharePercentage;

    public BasicInfo() {
    }

    public BasicInfo(String code) {
        this.code = code;
    }

    public BasicInfo(String code, float yearLow, float yearHigh) {
        this.code = code;
        this.yearLow = yearLow;
        this.yearHigh = yearHigh;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        return "BasicInfo{" + "code=" + code + ", low=" + yearLow + ", high=" + yearHigh + ", sector=" + sector + ", faceValue=" + faceValue + ", totalSecurity=" + totalSecurity + ", authorizedCapital=" + authorizedCapital + ", paidUpCapital=" + paidUpCapital + ", yearEnd=" + yearEnd + ", reserve=" + reserve + ", PE=" + PE + ", category=" + category + ", sharePercentage=" + sharePercentage + '}';
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public int getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(int faceValue) {
        this.faceValue = faceValue;
    }

    public int getTotalSecurity() {
        return totalSecurity;
    }

    public void setTotalSecurity(int totalSecurity) {
        this.totalSecurity = totalSecurity;
    }

    public float getAuthorizedCapital() {
        return authorizedCapital;
    }

    public void setAuthorizedCapital(float authorizedCapital) {
        this.authorizedCapital = authorizedCapital;
    }

    public float getPaidUpCapital() {
        return paidUpCapital;
    }

    public void setPaidUpCapital(float paidUpCapital) {
        this.paidUpCapital = paidUpCapital;
    }

    public String getYearEnd() {
        return yearEnd;
    }

    public void setYearEnd(String yearEnd) {
        this.yearEnd = yearEnd;
    }

    public float getReserve() {
        return reserve;
    }

    public void setReserve(float reserve) {
        this.reserve = reserve;
    }

    public float getPE() {
        return PE;
    }

    public void setPE(float PE) {
        this.PE = PE;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public SharePercentage getSharePercentage() {
        if(sharePercentage == null)
            sharePercentage = new SharePercentage(0, 0, 0, 0, 0);
        return sharePercentage;
    }

    public void setSharePercentage(SharePercentage sharePercentage) {
        this.sharePercentage = sharePercentage;
    }

    /**
     * @return the yearHigh
     */
    public float getYearHigh() {
        return yearHigh;
}

    /**
     * @param yearHigh the yearHigh to set
     */
    public void setYearHigh(float yearHigh) {
        this.yearHigh = yearHigh;
    }

    /**
     * @return the yearLow
     */
    public float getYearLow() {
        return yearLow;
    }

    /**
     * @param yearLow the yearLow to set
     */
    public void setYearLow(float yearLow) {
        this.yearLow = yearLow;
    }

    }
