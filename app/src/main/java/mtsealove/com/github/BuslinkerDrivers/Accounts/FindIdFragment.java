package mtsealove.com.github.BuslinkerDrivers.Accounts;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import mtsealove.com.github.BuslinkerDrivers.R;
import mtsealove.com.github.BuslinkerDrivers.Restful.API;
import mtsealove.com.github.BuslinkerDrivers.Restful.PhoneNumber;
import mtsealove.com.github.BuslinkerDrivers.Restful.PostResult;
import mtsealove.com.github.BuslinkerDrivers.Restful.Verify;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindIdFragment extends Fragment {
    private EditText phoneET, confirmET;
    private Button confirmBtn;

    public FindIdFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FindIdFragment newInstance() {
        FindIdFragment fragment = new FindIdFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_find_id, container, false);
        phoneET=view.findViewById(R.id.phoneET);
        phoneET.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        confirmET=view.findViewById(R.id.confirmET);
        confirmBtn=view.findViewById(R.id.verifyBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostPhone();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    //인증번호
    private String VerifyCode;

    private void PostPhone() {
        if(phoneET.getText().toString().split("-").length!=3)   //전화번호 입력 확인
            Toast.makeText(getContext(), "올바른 전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else {
            API api=new API();
            Call<Verify> call=api.getRetrofitService().PostPhone(new PhoneNumber(phoneET.getText().toString()));
            call.enqueue(new Callback<Verify>() {
                @Override
                public void onResponse(Call<Verify> call, Response<Verify> response) {
                    if(response.isSuccessful()){

                        VerifyCode=response.body().getVerifyCode();
                        Log.e("인증번호", VerifyCode);
                        Toast.makeText(getContext(), "인증번호가 발송되었습니다", Toast.LENGTH_SHORT).show();
                        confirmET.setVisibility(View.VISIBLE);
                        phoneET.setEnabled(false);
                    }
                }

                @Override
                public void onFailure(Call<Verify> call, Throwable t) {

                }
            });
        }
    }
}
