package com.faircab.user.ui.activity.edit_location;

import com.faircab.user.base.MvpPresenter;
import com.faircab.user.ui.activity.location_pick.LocationPickIView;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface EditLocationIPresenter<V extends EditLocationIView> extends MvpPresenter<V>{
    void address();
}
