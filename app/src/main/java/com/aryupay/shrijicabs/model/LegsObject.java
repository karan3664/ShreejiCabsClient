package com.aryupay.shrijicabs.model;

import java.util.List;

public class LegsObject {

    private List<StepsObject> steps;

    private Distance distance;

    private Duration duration;


    public LegsObject(List<StepsObject> steps, Distance distance, Duration duration) {
        this.steps = steps;
        this.distance = distance;
        this.duration = duration;
    }

    public List<StepsObject> getSteps() {
        return steps;
    }

    public void setSteps(List<StepsObject> steps) {
        this.steps = steps;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}