package com.faircab.user.ui.activity.splash;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.User;


public interface SplashIView extends MvpView{
    void onSuccess(User user);
    void onError(Throwable e);
}
