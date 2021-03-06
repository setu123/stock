package com.mycompany.model;

/**
 * @date Aug 21, 2015
 * @author Setu
 */
public class SharePercentage {

    private float director;
    private float government;
    private float institute;
    private float foreign;
    private float publics;

    public SharePercentage(float director, float government, float institute, float foreign, float publics) {
        this.director = director;
        this.government = government;
        this.institute = institute;
        this.foreign = foreign;
        this.publics = publics;
    }

    public float getDirector() {
        return director;
    }

    public void setDirector(float director) {
        this.director = director;
    }

    public float getGovernment() {
        return government;
    }

    public void setGovernment(float government) {
        this.government = government;
    }

    public float getInstitute() {
        return institute;
    }

    public void setInstitute(float institute) {
        this.institute = institute;
    }

    public float getForeign() {
        return foreign;
    }

    public void setForeign(float foreign) {
        this.foreign = foreign;
    }

    public float getPublics() {
        return publics;
    }

    public void setPublics(float publics) {
        this.publics = publics;
    }

}
