
package com.mycompany.model;

import java.util.Date;

/**
 * @date Dec 22, 2016
 * @author setu
 */
public class HoldingChange {
    private Date date;
    private Date previousDate;
    private Date lastUpdated;
    private float director;
    private float government;
    private float institute;
    private float foreign;
    private float publics;

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the previousDate
     */
    public Date getPreviousDate() {
        return previousDate;
    }

    /**
     * @param previousDate the previousDate to set
     */
    public void setPreviousDate(Date previousDate) {
        this.previousDate = previousDate;
    }

    /**
     * @return the lastUpdate
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * @param lastUpdate the lastUpdate to set
     */
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * @return the director
     */
    public float getDirector() {
        return director;
    }

    /**
     * @param director the director to set
     */
    public void setDirector(float director) {
        this.director = director;
    }

    /**
     * @return the government
     */
    public float getGovernment() {
        return government;
    }

    /**
     * @param government the government to set
     */
    public void setGovernment(float government) {
        this.government = government;
    }

    /**
     * @return the institute
     */
    public float getInstitute() {
        return institute;
    }

    /**
     * @param institute the institute to set
     */
    public void setInstitute(float institute) {
        this.institute = institute;
    }

    /**
     * @return the foreign
     */
    public float getForeign() {
        return foreign;
    }

    /**
     * @param foreign the foreign to set
     */
    public void setForeign(float foreign) {
        this.foreign = foreign;
    }

    /**
     * @return the publics
     */
    public float getPublics() {
        return publics;
    }

    /**
     * @param publics the publics to set
     */
    public void setPublics(float publics) {
        this.publics = publics;
    }
}
