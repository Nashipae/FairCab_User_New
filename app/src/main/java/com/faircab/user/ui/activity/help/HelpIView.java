package com.faircab.user.ui.activity.help;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.Help;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface HelpIView extends MvpView {
    void onSuccess(Help help);
    void onError(Throwable e);
}
