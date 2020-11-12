package com.faircab.user.ui.activity.register;

import com.faircab.user.base.MvpPresenter;

import java.util.HashMap;

public interface OtpIPresenter<V extends OtpIView> extends MvpPresenter<V>{
    void verifyCode(HashMap<String, Object> obj);
    void resendCode(HashMap<String, Object> obj);
}
