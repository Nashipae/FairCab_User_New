package com.faircab.user.ui.fragment.service;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.EstimateFare;
import com.faircab.user.data.network.model.Message;
import com.faircab.user.data.network.model.Service;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ServiceIView extends MvpView{
    void onSuccess(List<Service> serviceList);
    void onSuccess(EstimateFare estimateFare);
    void onError(Throwable e);
    void onSuccess(Message object);
}
