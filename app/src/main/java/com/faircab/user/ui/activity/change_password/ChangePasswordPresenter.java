package com.faircab.user.ui.activity.change_password;


import com.faircab.user.base.BasePresenter;
import com.faircab.user.data.SharedHelper;
import com.faircab.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class ChangePasswordPresenter<V extends ChangePasswordIView> extends BasePresenter<V> implements ChangePasswordIPresenter<V> {


    @Override
    public void changePassword(HashMap<String, Object> parms) {
        SharedHelper.apiState="changePassword";

        getCompositeDisposable().add(APIClient.getAPIClient().changePassword(parms)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object -> getMvpView().onSuccess(object),
                        throwable -> getMvpView().onError(throwable)));
    }
}
