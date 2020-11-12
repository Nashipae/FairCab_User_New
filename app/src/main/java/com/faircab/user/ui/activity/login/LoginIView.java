package com.faircab.user.ui.activity.login;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.ForgotResponse;
import com.faircab.user.data.network.model.Token;

public interface LoginIView extends MvpView{
    void onSuccess(Token token);
    void onSuccess(ForgotResponse object);
    void onError(Throwable e);
}
