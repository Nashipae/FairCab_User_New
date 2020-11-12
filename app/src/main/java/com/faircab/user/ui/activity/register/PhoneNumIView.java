package com.faircab.user.ui.activity.register;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.PhoneNumReponse;
import com.faircab.user.data.network.model.RegisterResponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface PhoneNumIView extends MvpView{
    void onSuccess(PhoneNumReponse object);
    void onError(Throwable e);
}
