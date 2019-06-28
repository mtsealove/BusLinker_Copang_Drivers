package mtsealove.com.github.BuslinkerDrivers.Accounts;


import android.app.DatePickerDialog;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import mtsealove.com.github.BuslinkerDrivers.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TooManyListenersException;

public class SignUpActivity extends AppCompatActivity {
    private int CarYear;
    private EditText emailET, pwET, pwConfirmET, nameET, companyET, prET, carTypeET, carNumberET;
    private TextView birthTV, carYearTV;
    private Button registerBtn;
    private RadioGroup genderRG;
    private String birth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailET = findViewById(R.id.emailET);
        pwET = findViewById(R.id.pwET);
        pwConfirmET = findViewById(R.id.pwConfirmET);
        nameET = findViewById(R.id.nameET);
        companyET = findViewById(R.id.companyET);
        prET = findViewById(R.id.prET);
        carTypeET = findViewById(R.id.carTypeET);
        carNumberET = findViewById(R.id.carNumberET);
        birthTV = findViewById(R.id.birthTV);
        carYearTV = findViewById(R.id.carYearTV);
        genderRG = findViewById(R.id.genderRG);
        registerBtn=findViewById(R.id.registerBtn);

        CarYear = getCurrentYear();
        carYearTV.setText("연식: " + CarYear);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckInput();
            }
        });
    }

    //현재 연도 구해오기
    private int getCurrentYear() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        int year = Integer.parseInt(dateFormat.format(date));
        return year;
    }

    //연식 내리기
    public void YearDown(View v) {
        if (CarYear >= 0) {
            CarYear--;
            carYearTV.setText("연식: " + CarYear);
        }
    }

    //연식 올리기
    public void YearUp(View v) {
        if (CarYear < getCurrentYear()) {
            CarYear++;
            carYearTV.setText("연식: " + CarYear);
        }
    }

    //생년월일 선택 다이얼로그 생성
    public void setBirth(View v) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat yearF = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthF = new SimpleDateFormat("MM");
        SimpleDateFormat dateF = new SimpleDateFormat("dd");
        int year = Integer.parseInt(yearF.format(date));
        int month = Integer.parseInt(monthF.format(date)) - 1;
        int dateOfMonth = Integer.parseInt(dateF.format(date));

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener, year, month, dateOfMonth);
        datePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            //날짜 변경 및 화면에 표시
            birth = i + "-" + (i1 + 1) + "-" + i2;
            birthTV.setText(birth);
        }
    };

    //입력 무결성 체크
    private void CheckInput() {
        String email = emailET.getText().toString();
        String pw = pwET.getText().toString();
        String pwConfirm = pwConfirmET.getText().toString();
        String name = nameET.getText().toString();
        int genderID = genderRG.getCheckedRadioButtonId();
        char gender = 'n';
        switch (genderID) {
            case R.id.maleRB:
                gender = 'm';
                break;
            case R.id.femaleRB:
                gender = 'f';
                break;
        }
        String company=companyET.getText().toString();
        String pr=prET.getText().toString();
        String carType=carTypeET.getText().toString();
        String carNumber=carNumberET.getText().toString();

        if(email.length()==0) toast("이메일 주소를 입력하세요");
        else if(pw.length()==0) toast("비밀번호를 입력하세요");
        else if(pwConfirm.length()==0) toast("비밀번호를 확인하세요");
        else if(name.length()==0) toast("이름을 입력하세요");
        else if(birth==null) toast("생년월일을 선택하세요");
        else if(gender=='n') toast("성별을 선택하세요");
        else if(pr.length()==0) toast("자기소개를 입력하세요");
        else if(carType.length()==0) toast("보유 차종을 입력하세요");
        else if(carNumber.length()==0) toast("차량번호를 입력하세요");
        else {

        }
    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
