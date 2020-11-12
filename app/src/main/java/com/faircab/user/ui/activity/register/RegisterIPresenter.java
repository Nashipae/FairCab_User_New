package com.faircab.user.ui.activity.register;

import com.faircab.user.base.MvpPresenter;

import java.util.HashMap;

public interface RegisterIPresenter<V extends RegisterIView> extends MvpPresenter<V>{
    void register(HashMap<String, Object> obj);
    void verifyEmail(String email);
}
