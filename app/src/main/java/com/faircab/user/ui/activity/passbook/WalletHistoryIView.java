package com.faircab.user.ui.activity.passbook;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.WalletResponse;

public interface WalletHistoryIView extends MvpView {
    void onSuccess(WalletResponse response);
    void onError(Throwable e);
}
