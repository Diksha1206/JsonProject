package com.example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Partner {

    String firstName;
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLasttName() {
        return lasttName;
    }
    public void setLasttName(String lasttName) {
        this.lasttName = lasttName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    
    String lasttName;
    String email;
    String country;
    List<Date> availableDates;
    public List<Date> getAvailableDates() {
        return availableDates;
    }
    public void setAvailableDates(List<Date> availableDates) {
        this.availableDates = availableDates;
    }
    


    
}
