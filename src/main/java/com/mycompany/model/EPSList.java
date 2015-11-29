/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.model;

/**
 * @date Oct 2, 2015
 * @author setu
 */
public class EPSList {

    private float first;
    private float second;
    private float third;
    private float fourth;

    public float getFirst() {
        return first;
    }

    public void setFirst(float first) {
        this.first = first;
    }

    public float getSecond() {
        return second;
    }

    public void setSecond(float second) {
        this.second = second;
    }

    public float getThird() {
        return third;
    }

    public void setThird(float third) {
        this.third = third;
    }

    public float getFourth() {
        return fourth;
    }

    public void setFourth(float fourth) {
        this.fourth = fourth;
    }

    @Override
    public String toString() {
        return "EPSList{" + "first=" + first + ", second=" + second + ", third=" + third + ", fourth=" + fourth + '}';
    }
    
    
}
