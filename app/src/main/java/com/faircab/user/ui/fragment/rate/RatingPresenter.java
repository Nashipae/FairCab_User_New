package com.faircab.user.ui.fragment.rate;

import com.faircab.user.base.BasePresenter;
import com.faircab.user.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class RatingPresenter<V extends RatingIView> extends BasePresenter<V> implements RatingIPresenter<V> {

    @Override
    public void rate(HashMap<String, Object> obj) {

        getCompositeDisposable().add(APIClient.getAPIClient().rate(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(rateResponse -> getMvpView().onSuccess(rateResponse),
                        throwable -> getMvpView().onError(throwable)));
    }
}
