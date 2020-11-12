package com.faircab.user.ui.activity.edit_location;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.AddressResponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface EditLocationIView extends MvpView {

    void onSuccess(AddressResponse address);
    void onError(Throwable e);
}
