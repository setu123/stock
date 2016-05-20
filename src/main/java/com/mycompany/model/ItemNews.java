/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.model;

import java.util.Date;

/**
 * @date Oct 2, 2015
 * @author setu
 */
public class ItemNews implements Comparable<ItemNews>{

    private String code;
    private String news;
    private Date date;
    private EPSList epsList;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    @Override
    public String toString() {
        return "ItemNews{" + "code=" + code + ", news=" + news + ", date=" + date + '}';
    }

    @Override
    public int compareTo(ItemNews o) {
        if(o==null)
            return -1;
        
        if(!this.getCode().equals(o.getCode()))
            return this.getCode().compareTo(o.getCode());
        
        return this.getDate().compareTo(o.getDate());
    }

    /**
     * @return the epsList
     */
    public EPSList getEpsList() {
        return epsList;
    }

    /**
     * @param epsList the epsList to set
     */
    public void setEpsList(EPSList epsList) {
        this.epsList = epsList;
    }
    
    
}
