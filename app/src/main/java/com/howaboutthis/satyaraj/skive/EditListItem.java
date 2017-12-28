package com.howaboutthis.satyaraj.skive;


class EditListItem {
    private String subjectName;
    private float subjectPercentage;
    private int classesAttended;
    private int totalClasses;

    EditListItem(String subjectName, float subjectPercentage, int classesAttended, int totalClasses) {
        this.subjectName = subjectName;
        this.subjectPercentage = subjectPercentage;
        this.classesAttended = classesAttended;
        this.totalClasses = totalClasses;
    }

    String getSubjectName() {
        return subjectName;
    }

    float getSubjectPercentage() {
        return subjectPercentage;
    }

    int getClassesAttended() {
        return classesAttended;
    }

    int getTotalClasses() {
        return totalClasses;
    }
}
