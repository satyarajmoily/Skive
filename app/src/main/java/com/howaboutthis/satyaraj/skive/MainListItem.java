package com.howaboutthis.satyaraj.skive;



class MainListItem {

    private String subjectName;
    private float subjectPercentage;
    private int state;
    private String button;
    private String dayOfTheWeek;

    MainListItem(String subjectName, float subjectPercentage, int state, String button, String dayOfTheWeek) {
        this.subjectName = subjectName;
        this.subjectPercentage = subjectPercentage;
        this.state = state;
        this.button = button;
        this.dayOfTheWeek = dayOfTheWeek;
    }


    String getSubjectName() {
        return subjectName;
    }

    float getSubjectPercentage() {
        return subjectPercentage;
    }

    int getState() {
        return state;
    }

    String getButton() { return button; }

    String getDayOfTheWeek() { return dayOfTheWeek; }
}
