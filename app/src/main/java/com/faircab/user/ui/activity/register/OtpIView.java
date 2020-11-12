package com.faircab.user.ui.activity.register;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.PhoneNumReponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface OtpIView extends MvpView{
    void onSuccess(PhoneNumReponse object);
    void onError(Throwable e);
    void onSuccessResend(PhoneNumReponse object);
    void onErrorResend(Throwable e);
}
