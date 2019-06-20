package mtsealove.com.github.BuslinkerDrivers.Entity;

//버스 기사 객체
public class Driver {
    String Name;
    String ID;
    String CarType;
    int CarYear;
    String Contact;

    public Driver(String Name, String ID, String CarType, int CarYear, String Contact){
        this.Name=Name;
        this.ID=ID;
        this.CarType=CarType;
        this.CarYear=CarYear;
        this.Contact=Contact;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCarType() {
        return CarType;
    }

    public void setCarType(String carType) {
        CarType = carType;
    }

    public int getCarYear() {
        return CarYear;
    }

    public void setCarYear(int carYear) {
        CarYear = carYear;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }
}
