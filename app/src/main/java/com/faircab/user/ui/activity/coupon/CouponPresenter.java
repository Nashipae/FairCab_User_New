package com.faircab.user.ui.activity.coupon;

import com.faircab.user.base.BasePresenter;
import com.faircab.user.data.network.APIClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CouponPresenter<V extends CouponIView> extends BasePresenter<V> implements CouponIPresenter<V> {

    @Override
    public void coupon() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .promocodesList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }
}
