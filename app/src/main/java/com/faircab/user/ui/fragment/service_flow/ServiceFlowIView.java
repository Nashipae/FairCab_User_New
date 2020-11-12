package com.faircab.user.ui.fragment.service_flow;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.DataResponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ServiceFlowIView extends MvpView{
    void onSuccess(DataResponse dataResponse);
    void onUpdateRideSuccess(Object o);
    void onError(Throwable e);
}
