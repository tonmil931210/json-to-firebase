package miltoncasanova.jsonfb;


import java.io.Serializable;import java.lang.String;import java.lang.SuppressWarnings;

@SuppressWarnings("serial")
public class DataEntry implements Serializable {

    private String gender;
    private String firstName;
    private String lastName;
    private String picture;

    public DataEntry() {

    }

    public DataEntry(String gender, String firstName, String lastName, String picture) {
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
        this.picture = picture;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
