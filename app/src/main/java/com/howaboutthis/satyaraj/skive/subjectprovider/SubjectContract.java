package com.howaboutthis.satyaraj.skive.subjectprovider;

public final class SubjectContract {

    private SubjectContract() {}

    public static final class SubjectEntry{

        /** Name of database table for subjects*/

        public final static String TABLE_NAME = "subjects";

        public final static String _ID = "_ID";

        public final static String COLUMN_SUBJECT_NAME="subject_name";

        public final static String COLUMN_PERCENTAGE = "percentage" ;

        public final static String COLUMN_CLASSES_ATTENDED = "classes_attended";

        public final static String COLUMN_TOTAL_CLASSES = "total_classes";

    }

}

