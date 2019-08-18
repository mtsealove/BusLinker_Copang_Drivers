package mtsealove.com.github.BuslinkerDrivers.Restful;

public class Verify {
    String VerifyCode;

    public Verify(String verifyCode) {
        VerifyCode = verifyCode;
    }

    public String getVerifyCode() {
        return VerifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        VerifyCode = verifyCode;
    }
}
