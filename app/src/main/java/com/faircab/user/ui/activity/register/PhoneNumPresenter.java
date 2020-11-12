package com.faircab.user.ui.activity.register;

import com.faircab.user.base.BasePresenter;
import com.faircab.user.data.SharedHelper;
import com.faircab.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PhoneNumPresenter<V extends PhoneNumIView>
        extends BasePresenter<V>
        implements PhoneNumIPresenter<V> {

    @Override
    public void sendVerificationCode(HashMap<String, Object> obj) {
        SharedHelper.apiState="sendVerificationCode";

        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .sendVerificationCode(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }
}
