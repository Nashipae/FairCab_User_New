package com.faircab.user.ui.activity.add_remove_stops;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.AddressResponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface AddRemoveStopsIView extends MvpView {

    void onSuccess(AddressResponse address);
    void onError(Throwable e);
}
