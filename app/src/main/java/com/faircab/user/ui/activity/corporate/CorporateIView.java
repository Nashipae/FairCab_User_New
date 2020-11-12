package com.faircab.user.ui.activity.corporate;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.Company;
import com.faircab.user.data.network.model.Message;
import com.faircab.user.data.network.model.User;

import java.util.List;

public interface CorporateIView extends MvpView {
    void onSuccess(Message object);
    void onSuccessCompanyList(List<Company> companies);
    void onSuccessUser(User user);
    void onError(Throwable e);
    void onErrorCorporate(Throwable e);
}
