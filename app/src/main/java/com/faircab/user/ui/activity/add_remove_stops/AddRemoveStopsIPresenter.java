package com.faircab.user.ui.activity.add_remove_stops;

import com.faircab.user.base.MvpPresenter;
import com.faircab.user.ui.activity.location_pick.LocationPickIView;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface AddRemoveStopsIPresenter<V extends AddRemoveStopsIView> extends MvpPresenter<V>{
    void address();
}
