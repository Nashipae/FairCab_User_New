package com.faircab.user.ui.fragment.schedule;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.Message;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ScheduleIView extends MvpView{
    void onSuccess(Message object);
    void onError(Throwable e);
}
