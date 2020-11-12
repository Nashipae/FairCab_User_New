package com.faircab.user.ui.activity.wallet;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.AddWallet;

public interface WalletIView extends MvpView {
    void onSuccess(AddWallet object);
    void onError(Throwable e);
}
