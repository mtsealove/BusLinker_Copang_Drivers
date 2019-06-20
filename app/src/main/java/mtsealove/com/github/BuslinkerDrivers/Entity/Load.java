package mtsealove.com.github.BuslinkerDrivers.Entity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

public class Load {
    String Name;
    String Cat;
    String Time;
    String address;
    private Context context;
    private double latitude, longitude;

    public Load(Context context, String Name, String Cat, String address, String Time) {
        this.Name = Name;
        this.context = context;
        this.Cat = Cat;
        this.address = address;
        this.Time = Time;
        GetLatLang();
    }

    public String getCat() {
        return Cat;
    }

    public void setCat(String cat) {
        Cat = cat;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private void GetLatLang() {  //주소를 기반으로 위경도 설정
        Geocoder geocoder = new Geocoder(context);
        try {
            Address addr = geocoder.getFromLocationName(address, 1).get(0);
            latitude = addr.getLatitude();
            longitude = addr.getLongitude();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
