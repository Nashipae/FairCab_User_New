package com.faircab.user.ui.activity.profile;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.User;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ProfileIView extends MvpView{
    void onSuccess(User user);
    void onError(Throwable e);
}
