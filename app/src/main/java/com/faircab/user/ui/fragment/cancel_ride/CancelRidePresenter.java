package com.faircab.user.ui.fragment.cancel_ride;

import com.faircab.user.base.BasePresenter;
import com.faircab.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.FieldMap;

public class CancelRidePresenter<V extends CancelRideIView> extends BasePresenter<V> implements CancelRideIPresenter<V> {

    @Override
    public void cancelRequest(@FieldMap HashMap<String, Object> params) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .cancelRequest(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessCancel, getMvpView()::onErrorCancel));
    }
}
