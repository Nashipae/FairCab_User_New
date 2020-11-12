package com.faircab.user.ui.activity.main;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.AddressResponse;
import com.faircab.user.data.network.model.DataResponse;
import com.faircab.user.data.network.model.InitSettingsResponse;
import com.faircab.user.data.network.model.Provider;
import com.faircab.user.data.network.model.User;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface MainIView extends MvpView{
    void onSuccess(User user);
    void onSuccess(DataResponse dataResponse);
    void onSuccessLogout(Object object);
    void onSuccess(AddressResponse response);
    void onSuccess(List<Provider> objects);
    void onSuccess(InitSettingsResponse initSettingsResponse);
    void onError(Throwable e);
    void onCheckStatusError(Throwable e);

}
