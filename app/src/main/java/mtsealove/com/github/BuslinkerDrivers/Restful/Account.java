package mtsealove.com.github.BuslinkerDrivers.Restful;

import java.io.Serializable;

public class Account implements Serializable {
    String ID,
            Password,
            Name,
            Birth,
            Contact,
            CompanyID,
            PR,
            CarType,
            CarNum,
            SignUpDate;

    int CarYear, Cat;
    char gender;
    boolean Confirmed;

    public Account(String ID, String password, String name, String birth, String contact, String companyID, String PR, String carType, String carNum, String signUpDate, int carYear, int cat, char gender, boolean confirmed) {
        this.ID = ID;
        Password = password;
        Name = name;
        Birth = birth;
        Contact = contact;
        CompanyID = companyID;
        this.PR = PR;
        CarType = carType;
        CarNum = carNum;
        SignUpDate = signUpDate;
        CarYear = carYear;
        Cat = cat;
        this.gender = gender;
        Confirmed = confirmed;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBirth() {
        return Birth;
    }

    public void setBirth(String birth) {
        Birth = birth;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(String companyID) {
        CompanyID = companyID;
    }

    public String getPR() {
        return PR;
    }

    public void setPR(String PR) {
        this.PR = PR;
    }

    public String getCarType() {
        return CarType;
    }

    public void setCarType(String carType) {
        CarType = carType;
    }

    public String getCarNum() {
        return CarNum;
    }

    public void setCarNum(String carNum) {
        CarNum = carNum;
    }

    public String getSignUpDate() {
        return SignUpDate;
    }

    public void setSignUpDate(String signUpDate) {
        SignUpDate = signUpDate;
    }

    public int getCarYear() {
        return CarYear;
    }

    public void setCarYear(int carYear) {
        CarYear = carYear;
    }

    public int getCat() {
        return Cat;
    }

    public void setCat(int cat) {
        Cat = cat;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public boolean isConfirmed() {
        return Confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        Confirmed = confirmed;
    }

    @Override
    public String toString() {
        return "Account{" +
                "ID='" + ID + '\'' +
                ", Password='" + Password + '\'' +
                ", Name='" + Name + '\'' +
                ", Birth='" + Birth + '\'' +
                ", Contact='" + Contact + '\'' +
                ", CompanyID='" + CompanyID + '\'' +
                ", PR='" + PR + '\'' +
                ", CarType='" + CarType + '\'' +
                ", CarNum='" + CarNum + '\'' +
                ", SignUpDate='" + SignUpDate + '\'' +
                ", CarYear=" + CarYear +
                ", Cat=" + Cat +
                ", gender=" + gender +
                ", Confirmed=" + Confirmed +
                '}';
    }
}
