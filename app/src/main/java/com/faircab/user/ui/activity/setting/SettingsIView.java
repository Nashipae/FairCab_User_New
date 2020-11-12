package com.faircab.user.ui.activity.setting;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.AddressResponse;

public interface SettingsIView extends MvpView {

    void onSuccessAddress(Object object);

    void onLanguageChanged(Object object);

    void onSuccess(AddressResponse address);

    void onError(Throwable e);
}
