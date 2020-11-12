package com.faircab.user.base;

import android.app.Activity;

import com.faircab.user.MvpApplication;

import java.util.HashMap;

public interface MvpPresenter<V extends MvpView> {

    Activity activity();

    MvpApplication appContext();

    void attachView(V mvpView);

    void onDetach();

    void refreshToken(HashMap<String, Object> obj);

    void logout(String id);

}
