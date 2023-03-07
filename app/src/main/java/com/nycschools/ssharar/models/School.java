package com.nycschools.ssharar.models;

/* Author: Steven Sharar
   Date: 3/3/2023
 */
public class School {
    public String dbn;
    public String school_name;
    public String phone_number;
    public String school_email;

    public String getDbn() {
        return dbn;
    }

    public void setDbn(String dbn) {
        this.dbn = dbn;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getSchool_email() {
        return school_email;
    }

    public void setSchool_email(String school_email) {
        this.school_email = school_email;
    }


}
