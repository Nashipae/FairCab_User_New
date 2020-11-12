package com.faircab.user.ui.activity.register;

import com.faircab.user.base.MvpPresenter;

import java.util.HashMap;

public interface PhoneNumIPresenter<V extends PhoneNumIView> extends MvpPresenter<V>{
    void sendVerificationCode(HashMap<String, Object> obj);
}
