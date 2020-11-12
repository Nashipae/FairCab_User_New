package com.faircab.user.ui.activity.register;

import android.content.Intent;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.faircab.user.BuildConfig;
import com.faircab.user.R;
import com.faircab.user.base.BaseActivity;
import com.faircab.user.data.SharedHelper;
import com.faircab.user.data.network.model.PhoneNumReponse;
import com.faircab.user.data.network.model.Token;
import com.faircab.user.data.network.model.VerificationReponse;
import com.faircab.user.ui.activity.main.MainActivity;
import com.faircab.user.ui.activity.social.SocialIView;
import com.faircab.user.ui.activity.social.SocialLoginActivity;
import com.faircab.user.ui.activity.social.SocialPresenter;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OtpActivity extends BaseActivity implements OtpIView, SocialIView {

    @BindView(R.id.phone_number_tv)
    TextView phoneNum;
    @BindView(R.id.countdown_tv)
    TextView countdown;
    @BindView(R.id.otpPinView)
    PinView pinView;
    @BindView(R.id.resend_btn)
    Button resendBtn;
    OtpPresenter otpPresenter = new OtpPresenter();
    private SocialPresenter<OtpActivity> presenter = new SocialPresenter<>();
    CountDownTimer countDownTimer;
    String code="";
    String loginBy="";
    String accessToken="";
    private HashMap<String, Object> map = new HashMap<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_otp;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        otpPresenter.attachView(this);
        presenter.attachView(this);
        setTitle(R.string.otp_verification);
        if(getIntent().getStringExtra("login_by")!=null){
        if(getIntent().getStringExtra("login_by").equals("facebook")
                || getIntent().getStringExtra("login_by").equals("google")){
            loginBy = getIntent().getStringExtra("login_by");
            map.put("login_by", loginBy);
            accessToken= getIntent().getStringExtra("accessToken");
            map.put("accessToken", accessToken);
        }}
        phoneNum.setText(SharedHelper.getKey(this, "dial_code") + SharedHelper.getKey(this, "mobile"));

        startTimer();
        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==4){
                    code=s.toString();
                    verifyOtp(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void startTimer(){
        new CountDownTimer(120*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long second = (millisUntilFinished / 1000) % 60;
                long minutes = (millisUntilFinished/(1000*60)) % 60;
                countdown.setText(minutes + ":" + second);
            }

            @Override
            public void onFinish() {
                countdown.setText("Finish");
                resendBtn.setEnabled(true);

            }
        }.start();
    }

    private void verifyOtp(String code){
        HashMap<String, Object> map = new HashMap<>();
        map.put("mobile_no", SharedHelper.getKey(this, "dial_code") + SharedHelper.getKey(this, "mobile"));
        map.put("code", code);
        showLoading();

        otpPresenter.verifyCode(map);
    }

    private void resendCode(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("mobile_no", SharedHelper.getKey(this, "dial_code") + SharedHelper.getKey(this, "mobile"));
        showLoading();

        otpPresenter.resendCode(map);
    }

    @OnClick({R.id.next,R.id.resend_btn} )
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next:
                verifyOtp(code);

                break;
            case R.id.resend_btn:
                resendCode();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        otpPresenter.onDetach();
        presenter.onDetach();
        super.onDestroy();
    }

    @Override
    public void onSuccess(PhoneNumReponse object) {
       // Toast.makeText(this, "sadsa", Toast.LENGTH_SHORT).show();

        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if(object.getStatus()){
            if(loginBy.equals("facebook") || loginBy.equals("google")){
                register();
            }else {
                startActivity(new Intent(OtpActivity.this,RegisterActivity.class));
            }
        }
    }

    private void register() {
        map.put("mobile", SharedHelper.getKey(this, "dial_code")+SharedHelper.getKey(this, "mobile"));
        map.put("isMobileVerified",1);

        String deviceToken=SharedHelper.getKey(this, "device_token");
        String deviceId=SharedHelper.getKey(this, "device_id");

        if(deviceToken!=null && !deviceToken.isEmpty() && deviceId!=null && !deviceId.isEmpty()){
            map.put("device_token", SharedHelper.getKey(this, "device_token"));
            map.put("device_id", SharedHelper.getKey(this, "device_id"));
            map.put("device_type", BuildConfig.DEVICE_TYPE);
            if (map.get("login_by").equals("google")) presenter.loginGoogle(map);
            else if (map.get("login_by").equals("facebook")) presenter.loginFacebook(map);

        }
        else{
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String newToken = instanceIdResult.getToken();
                    String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    map.put("device_token", newToken);
                    map.put("device_id", deviceId);
                    map.put("device_type", BuildConfig.DEVICE_TYPE);
                    if (map.get("login_by").equals("google")) presenter.loginGoogle(map);
                    else if (map.get("login_by").equals("facebook")) presenter.loginFacebook(map);

                }
            });
        }



        showLoading();
    }


    @Override
    public void onSuccess(Token token) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        String accessToken = token.getTokenType() + " " + token.getAccessToken();
        SharedHelper.putKey(this, "access_token", accessToken);
        SharedHelper.putKey(this, "logged_in", true);
        finishAffinity();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onSuccess(VerificationReponse verificationReponse) {

    }

    @Override
    public void onError(Throwable throwable) {
        handleError(throwable);
    }

    @Override
    public void onSuccessResend(PhoneNumReponse object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if(object.getStatus()){
            startTimer();
            resendBtn.setEnabled(false);
        }
    }

    @Override
    public void onErrorResend(Throwable e) {
        handleError(e);
    }


}
