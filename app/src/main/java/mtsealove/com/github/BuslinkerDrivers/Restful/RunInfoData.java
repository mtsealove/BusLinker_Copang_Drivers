package mtsealove.com.github.BuslinkerDrivers.Restful;

public class RunInfoData {
    String DriverID, key;

    public RunInfoData(String driverID, String key) {
        DriverID = driverID;
        this.key = key;
        if(key==null)
            key="";
    }

    public String getDriverID() {
        return DriverID;
    }

    public void setDriverID(String driverID) {
        DriverID = driverID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
