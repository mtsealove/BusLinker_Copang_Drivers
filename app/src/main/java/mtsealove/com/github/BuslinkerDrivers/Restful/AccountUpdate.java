package mtsealove.com.github.BuslinkerDrivers.Restful;

public class AccountUpdate {
    String NewPassword, Contact, PR, CarType, CarNumber, ID;

    public AccountUpdate(String ID, String newPassword, String contact, String PR, String carType, String carNumber) {
        this.ID=ID;
        NewPassword = newPassword;
        Contact = contact;
        this.PR = PR;
        CarType = carType;
        CarNumber = carNumber;
    }

    public String getNewPassword() {
        return NewPassword;
    }

    public void setNewPassword(String newPassword) {
        NewPassword = newPassword;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
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

    public String getCarNumber() {
        return CarNumber;
    }

    public void setCarNumber(String carNumber) {
        CarNumber = carNumber;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
