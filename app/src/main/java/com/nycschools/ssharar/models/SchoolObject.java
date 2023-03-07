package com.nycschools.ssharar.models;

public class SchoolObject {
    public School school;
    public SchoolSAT sat;
    public boolean showInfo;

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public SchoolSAT getSat() {
        return sat;
    }

    public void setSat(SchoolSAT sat) {
        this.sat = sat;
    }

    public boolean isShowInfo() {
        return showInfo;
    }

    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }
}
