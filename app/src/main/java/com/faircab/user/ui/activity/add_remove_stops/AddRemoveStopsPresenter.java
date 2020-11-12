package com.faircab.user.ui.activity.add_remove_stops;

import com.faircab.user.base.BasePresenter;
import com.faircab.user.data.network.APIClient;
import com.faircab.user.ui.activity.location_pick.LocationPickIPresenter;
import com.faircab.user.ui.activity.location_pick.LocationPickIView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddRemoveStopsPresenter<V extends AddRemoveStopsIView> extends BasePresenter<V> implements AddRemoveStopsIPresenter<V> {

    @Override
    public void address() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .address()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }
}
