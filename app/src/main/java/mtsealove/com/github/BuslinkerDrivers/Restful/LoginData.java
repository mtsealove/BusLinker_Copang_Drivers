package mtsealove.com.github.BuslinkerDrivers.Restful;

public class LoginData {
    String ID, password, token;

    public LoginData(String ID, String password, String token) {
        this.ID = ID;
        this.password = password;
        this.token = token;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
