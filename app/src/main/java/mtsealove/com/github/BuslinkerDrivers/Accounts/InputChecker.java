package mtsealove.com.github.BuslinkerDrivers.Accounts;

//입력 확인 클래스
public class InputChecker {
    private static String Upper="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String Lower="abcdefghijklmnopqrstuvwxyz";
    private static String Number="0123456789";
    private static String Special="`~!@#$%^&*()_+=-[{]};:'\",<.>/?|\\";
    //비밀번호 입력 체크
    public static boolean CheckPassword(String password){
        boolean up=false, low=false, num=false, sp=false;
        if(password.length()<8) //길이 확인
            return false;
        else {
            for(int i=0; i<password.length(); i++){
                //비밀번호 문자열의 모든 문자 대조
                for (int j=0; j<Upper.length(); j++){   //대문자 확인
                    if(password.charAt(i)==Upper.charAt(j)){
                        up=true;
                        break;
                    }
                }
                for(int j=0; j<Lower.length(); j++){
                    if(password.charAt(i)==Lower.charAt(j)){
                        low=true;
                        break;
                    }
                }
                for(int j=0; j<Number.length();j++){
                    if(password.charAt(i)==Number.charAt(j)){
                        num=true;
                        break;
                    }
                }
                for(int j=0; j<Special.length(); j++){
                    if(password.charAt(i)==Special.charAt(j)){
                        sp=true;
                        break;
                    }
                }
            }
            if(up&&low&&num&&sp)
                return true;
            else return false;
        }
    }
}
