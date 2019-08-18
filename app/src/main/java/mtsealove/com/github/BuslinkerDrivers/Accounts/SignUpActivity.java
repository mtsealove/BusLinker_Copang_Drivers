package mtsealove.com.github.BuslinkerDrivers.Accounts;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import mtsealove.com.github.BuslinkerDrivers.R;
import mtsealove.com.github.BuslinkerDrivers.Service.ApiService;
import mtsealove.com.github.BuslinkerDrivers.SetIPActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.RequestBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.*;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private int CarYear;
    private EditText emailET, pwET, pwConfirmET, nameET, companyET, prET, carTypeET, carNumberET, contactET;
    private TextView birthTV, carYearTV;
    private Button registerBtn;
    private RadioGroup genderRG;
    //이미지 선택 버튼들
    private LinearLayout PhotoFrontBtn, PhotoSideBtn, PhotoWholeSeatBtn, PhotoSeatBtn, LisenceBtn, InsureBtn, RegistrationBtn;
    private ImageView PhotoFrontIV, PhotoSideIV, PhotoWholeSeaIV, PhotoSeatIV, LisenceIV, InsureIV, RegistrationIV;
    private String birth = null;
    //사진 선택을 위한 객체들
    private ApiService apiService;
    private Bitmap bitmapFront, bitmapSide, bitmapWholeSeat, bitmapSeat, bitmapLicense, bitmapInsure, bitmapReigstration;
    private Uri picUri;
    private final static int PHOTO_FRONT = 100, PHOTO_SIDE = 200, PHOTO_WHOLE_SEAT = 300, PHOTO_SEAT = 400, LICENSE = 500, INSURE = 600, REGISTRATION = 700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        CheckPermission();
        initRetrofitClient();

        emailET = findViewById(R.id.emailET);
        pwET = findViewById(R.id.pwET);
        pwConfirmET = findViewById(R.id.pwConfirmET);
        nameET = findViewById(R.id.nameET);
        companyET = findViewById(R.id.companyET);
        prET = findViewById(R.id.prET);
        carTypeET = findViewById(R.id.carTypeET);
        carNumberET = findViewById(R.id.carNumberET);
        contactET=findViewById(R.id.contactET);
        birthTV = findViewById(R.id.birthTV);
        carYearTV = findViewById(R.id.carYearTV);
        genderRG = findViewById(R.id.genderRG);
        registerBtn = findViewById(R.id.registerBtn);

        contactET.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        //사진 버튼
        PhotoFrontBtn = findViewById(R.id.photoFrontBtn);
        PhotoSeatBtn = findViewById(R.id.photoSeatBtn);
        PhotoSideBtn = findViewById(R.id.photoSideBtn);
        PhotoWholeSeatBtn = findViewById(R.id.photoWholeSeatBtn);
        LisenceBtn = findViewById(R.id.licenseBtn);
        InsureBtn = findViewById(R.id.insureBtn);
        RegistrationBtn = findViewById(R.id.registrationBtn);

        //버튼 클릭 리스너
        PhotoFrontBtn.setOnClickListener(this);
        PhotoSideBtn.setOnClickListener(this);
        PhotoWholeSeatBtn.setOnClickListener(this);
        PhotoSeatBtn.setOnClickListener(this);
        LisenceBtn.setOnClickListener(this);
        InsureBtn.setOnClickListener(this);
        RegistrationBtn.setOnClickListener(this);

        //화면에 표시될 사진
        PhotoFrontIV = findViewById(R.id.photoFrontIV);
        PhotoSideIV = findViewById(R.id.photoSideIV);
        PhotoWholeSeaIV = findViewById(R.id.photoWholeSeatIV);
        PhotoSeatIV = findViewById(R.id.photoSeatIV);
        LisenceIV = findViewById(R.id.LicenseIV);
        InsureIV = findViewById(R.id.insureIV);
        RegistrationIV = findViewById(R.id.registrationIV);


        CarYear = getCurrentYear();
        carYearTV.setText("연식: " + CarYear);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckInput();
            }
        });
        //이미지 코드 초기화
        ImageList=new ArrayList<>();
        ImageList.add(PHOTO_FRONT);
        ImageList.add(PHOTO_SIDE);
        ImageList.add(PHOTO_WHOLE_SEAT);
        ImageList.add(PHOTO_SEAT);
        ImageList.add(LICENSE);
        ImageList.add(INSURE);
        ImageList.add(REGISTRATION);
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

    private String email, pw, pwConfirm, name, company, pr, carType, carNumber, contact;
    private char gender;

    //입력 무결성 체크
    private void CheckInput() {
        email = emailET.getText().toString();
        pw = pwET.getText().toString();
        pwConfirm = pwConfirmET.getText().toString();
        name = nameET.getText().toString();
        contact=contactET.getText().toString();
        int genderID = genderRG.getCheckedRadioButtonId();
        gender = 'n';
        switch (genderID) {
            case R.id.maleRB:
                gender = 'm';
                break;
            case R.id.femaleRB:
                gender = 'f';
                break;
        }
        company = companyET.getText().toString();
        pr = prET.getText().toString();
        carType = carTypeET.getText().toString();
        carNumber = carNumberET.getText().toString();

        if (email.length() == 0) toast("이메일 주소를 입력하세요");
        else if (pw.length() == 0) toast("비밀번호를 입력하세요");
        else if (pwConfirm.length() == 0) toast("비밀번호를 확인하세요");
        else if(!pw.equals(pwConfirm)) toast("비밀번호가 일치하지 않습니다");
        else if (name.length() == 0) toast("이름을 입력하세요");
        else if (birth == null) toast("생년월일을 선택하세요");
        else if (gender == 'n') toast("성별을 선택하세요");
        else if (pr.length() == 0) toast("자기소개를 입력하세요");
        else if(contact.length()==0)  toast("연락처를 입력하세요");
        else if (company.length() == 0) toast("회사 ID를 입력하세요 ");
        else if (carType.length() == 0) toast("보유 차종을 입력하세요");
        else if (carNumber.length() == 0) toast("차량번호를 입력하세요");
        else if(bitmapFront==null) toast("차량 정면 사진을 첨부하세요");
        else if(bitmapSide==null) toast("차량 측면 사진을 첨부하세요");
        else if(bitmapWholeSeat==null) toast("좌석 전체 사진을 첨부하세요");
        else if(bitmapSeat==null) toast("좌석 일부 사진을 첨부하세요");
        else if(bitmapLicense==null) toast("버스운전면허등록증을 첨부하세요");
        else if(bitmapInsure==null) toast("보험증서를 첨부하세요");
        else if(bitmapReigstration==null) toast("차량등록증을 첨부하세요");
        else {  //입력이 완료되었다면
            ConnectSocket();
        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //권한 체크
    private void CheckPermission() {
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    //권한 리스너
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            //권한 거부 시
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
            builder.setTitle("권한 거부")
                    .setMessage("권한이 거부었습니다.\n권한을 허용하여야 회원가입을 진행할 수 있습니다")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    };

    //RetroFit 초기화
    private void initRetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        apiService = new Retrofit.Builder().baseUrl("http://"+SetIPActivity.IP+":3210").client(client).build().create(ApiService.class);
    }

    //이미지 선택 인텐트
    public Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "사진을 선택하세요");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;

        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());
    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //데이터 유지를 위해 사용
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        picUri = savedInstanceState.getParcelable("pic_uri");
    }

    ArrayList<Integer> ImageList;   //이미지 리스트
    int indexOfImage=0;
    ProgressDialog progressDialog;
    //파일 업로드
    private void multipartImageUpload() {
        String ColumnName=null;
        try {
            File filesDir = getApplicationContext().getFilesDir();
            File file = new File(filesDir, "image" + ".png");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            //요청 코드에 따라 다른 이미지 업로드
            int requestCode=ImageList.get(indexOfImage);
            switch (requestCode) {
                case PHOTO_FRONT:
                    bitmapFront.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    ColumnName="PhotoFront";
                    break;
                case PHOTO_SIDE:
                    bitmapSide.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    ColumnName="PhotoSide";
                    break;
                case PHOTO_WHOLE_SEAT:
                    bitmapWholeSeat.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    ColumnName="PhotoWholeSeat";
                    break;
                case PHOTO_SEAT:
                    bitmapSeat.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    ColumnName="PhotoSeat";
                    break;
                case LICENSE:
                    bitmapLicense.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    ColumnName="PhotoBusLicense";
                    break;
                case INSURE:
                    bitmapInsure.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    ColumnName="PhotoInsurance";
                    break;
                case REGISTRATION:
                    bitmapReigstration.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    ColumnName="PhotoCarRegistration";
                    break;
            }
            final byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("uploadFile", file.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), email);   //새로 가입한 ID를 넘겨주기
            RequestBody Column=RequestBody.create(MediaType.parse("text/plain"), ColumnName);   //칼럼명 설정

            Call<ResponseBody> req = apiService.postImage(body, name, Column);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.code() == 200) {
                        Log.e("이미지 삽입", "성공");
                        indexOfImage++;
                        progressDialog.setProgress(indexOfImage);
                        if(indexOfImage<ImageList.size()){
                            multipartImageUpload();
                        } else{
                            progressDialog.dismiss();
                            AlertDialog.Builder builder=new AlertDialog.Builder(SignUpActivity.this);
                            builder.setTitle("완료")
                                    .setMessage("회원가입이 완료되었습니다")
                                    .setCancelable(false)
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    });
                            AlertDialog dialog=builder.create();
                            dialog.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    toast("업로드 실패");
                    t.printStackTrace();
                }
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.photoFrontBtn:
                startActivityForResult(getPickImageChooserIntent(), PHOTO_FRONT);
                break;
            case R.id.photoSideBtn:
                startActivityForResult(getPickImageChooserIntent(), PHOTO_SIDE);
                break;
            case R.id.photoSeatBtn:
                startActivityForResult(getPickImageChooserIntent(), PHOTO_SEAT);
                break;
            case R.id.photoWholeSeatBtn:
                startActivityForResult(getPickImageChooserIntent(), PHOTO_WHOLE_SEAT);
                break;
            case R.id.licenseBtn:
                startActivityForResult(getPickImageChooserIntent(), LICENSE);
                break;
            case R.id.insureBtn:
                startActivityForResult(getPickImageChooserIntent(), INSURE);
                break;
            case R.id.registrationBtn:
                startActivityForResult(getPickImageChooserIntent(), REGISTRATION);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) { //요청 성공

            String filePath = getImageFilePath(data);
            if (filePath != null) { //파일이 불러와 졌다면
                switch (requestCode) {
                    case PHOTO_FRONT:   //차량 정면
                        bitmapFront = BitmapFactory.decodeFile(filePath);
                        PhotoFrontIV.setImageBitmap(bitmapFront);
                        break;
                    case PHOTO_SIDE:    //차량 측면
                        bitmapSide = BitmapFactory.decodeFile(filePath);
                        PhotoSideIV.setImageBitmap(bitmapSide);
                        break;
                    case PHOTO_WHOLE_SEAT:  //좌석 전체
                        bitmapWholeSeat = BitmapFactory.decodeFile(filePath);
                        PhotoWholeSeaIV.setImageBitmap(bitmapWholeSeat);
                        break;
                    case PHOTO_SEAT:    //좌석
                        bitmapSeat = BitmapFactory.decodeFile(filePath);
                        PhotoSeatIV.setImageBitmap(bitmapSeat);
                        break;
                    case LICENSE:   //운전면허
                        bitmapLicense = BitmapFactory.decodeFile(filePath);
                        LisenceIV.setImageBitmap(bitmapLicense);
                        break;
                    case INSURE:    //보험 증서
                        bitmapInsure = BitmapFactory.decodeFile(filePath);
                        InsureIV.setImageBitmap(bitmapInsure);
                        break;
                    case REGISTRATION:  //차량 등록증
                        bitmapReigstration = BitmapFactory.decodeFile(filePath);
                        RegistrationIV.setImageBitmap(bitmapReigstration);
                        break;
                }
            }
        }
    }


    ProgressDialog sqlDialog;
    //소켓
    private Socket mSocket;

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject data = new JSONObject();
            try {
                data.put("email", email);
                data.put("pw", pw);
                data.put("name", name);
                data.put("birth", birth);
                data.put("contact", contact);
                data.put("company", company);
                data.put("pr", pr);
                data.put("carType",carType);
                data.put("carYear", CarYear);
                data.put("carNumber", carNumber);
                data.put("gender", gender);

            } catch (Exception e) {
                e.printStackTrace();
            }

            mSocket.emit("PostDriver", data);
        }
    };

    String TAG = "결과";
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //데이터 파싱
            try {
                Log.e(TAG, args[0].toString());
                JSONObject object = (JSONObject) args[0];
                int added=object.getInt("affectedRows");
                if(added==1){   //데이터베이스에 넣기를 성공했을 때
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sqlDialog.dismiss();
                            progressDialog=new ProgressDialog(SignUpActivity.this);
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog.setMessage("이미지를 업로드하는 중입니다");
                            progressDialog.setCancelable(false);
                            progressDialog.setMax(ImageList.size());
                            progressDialog.show();
                        }
                    });
                    multipartImageUpload(); //이미지 삽입
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toast("에러");
                        }
                    });
                }


            } catch (Exception e) {
                Log.e("Err", e.toString());
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SignUpActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                mSocket.disconnect();
            }
        }
    };

    //소켓 생성
    private void ConnectSocket() {
        try {
            sqlDialog=new ProgressDialog(this);
            sqlDialog.setMessage("데이터를 생성하는 중입니다");

            sqlDialog.setCancelable(false);
            sqlDialog.show();
            mSocket = IO.socket(SetIPActivity.IP);   //서버 주소
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on("PostDriverResult", onMessageReceived);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
