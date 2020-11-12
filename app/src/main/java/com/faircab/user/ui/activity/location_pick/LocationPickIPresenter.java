package com.faircab.user.ui.activity.location_pick;

import com.faircab.user.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface LocationPickIPresenter<V extends LocationPickIView> extends MvpPresenter<V>{
    void address();
}
