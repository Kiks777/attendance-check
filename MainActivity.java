//package com.termproject.ac;
//
//import java.time.LocalDate;
//
//public class AttendanceData {
//    private String id_number;
//    private boolean isIn;
//    private boolean isOut;
//    private String timeIn;
//    private String timeOut;
//
//    private LocalDate selectedDate;
//
//    public AttendanceData(String id_number, boolean isIn, boolean isOut, String timeIn, String timeOut) {
//        this.id_number = id_number;
//        this.isIn = isIn;
//        this.isOut = isOut;
//        this.timeIn = timeIn;
//        this.timeOut = timeOut;
//    }
//
//    public String getId_number() {
//        return id_number;
//    }
//
//    public void setId_number(String id_number) {
//        this.id_number = id_number;
//    }
//
//    public boolean isIn() {
//        return isIn;
//    }
//
//    public void setIn(boolean in) {
//        isIn = in;
//    }
//
//    public boolean isOut() {
//        return isOut;
//    }
//
//    public void setOut(boolean out) {
//        isOut = out;
//    }
//
//    public String getTimeIn() {
//        return timeIn;
//    }
//
//    public void setTimeIn(String timeIn) {
//        this.timeIn = timeIn;
//    }
//
//    public String getTimeOut() {
//        return timeOut;
//    }
//
//    public void setTimeOut(String timeOut) {
//        this.timeOut = timeOut;
//    }
//
//    public LocalDate getSelectedDate() {
//        return selectedDate;
//    }
//
//    public void setSelectedDate(LocalDate selectedDate) {
//        this.selectedDate = selectedDate;
//    }
//}


package com.termproject.ac;

import java.time.LocalDate;

public class AttendanceData {
    private String id_number;
    private boolean isIn;
    private boolean isOut;
    private String timeIn;
    private String timeOut;



    public AttendanceData(String id_number, boolean isIn, boolean isOut, String timeIn, String timeOut) {
        this.id_number = id_number;
        this.isIn = isIn;
        this.isOut = isOut;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public boolean isIn() {
        return isIn;
    }

    public void setIn(boolean in) {
        isIn = in;
    }

    public boolean isOut() {
        return isOut;
    }

    public void setOut(boolean out) {
        isOut = out;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }


}

