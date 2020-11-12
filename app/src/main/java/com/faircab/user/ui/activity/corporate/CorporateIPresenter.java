package com.faircab.user.ui.activity.corporate;

import com.faircab.user.base.MvpPresenter;

import java.util.HashMap;

public interface CorporateIPresenter<V extends CorporateIView> extends MvpPresenter<V> {
    void postCorperateUser(HashMap<String, Object> obj);
    void getCompanyList();
    void profile();
}
