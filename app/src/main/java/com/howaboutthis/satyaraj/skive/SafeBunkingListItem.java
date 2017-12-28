package com.howaboutthis.satyaraj.skive;


 class SafeBunkingListItem {
    private String subjectName;
    private Float subjectPercentage;
    private String Comment;
    private int attended;
    private int total;

    SafeBunkingListItem(String subjectName, Float subjectPercentage, String comment, int attended, int total) {
        this.subjectName = subjectName;
        this.subjectPercentage = subjectPercentage;
        this.Comment = comment;
        this.attended = attended;
        this.total = total;
    }

    String getSubjectName() {
        return subjectName;
    }

    Float getSubjectPercentage() {
        return subjectPercentage;
    }

    String getComment() {
        return Comment;
    }

    public int getAttended() {
        return attended;
    }

    int getTotal() {
        return total;
    }
}
