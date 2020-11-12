package com.faircab.user.ui.activity.upcoming_trip_detail;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.Datum;

import java.util.List;

public interface UpcomingTripDetailsIView extends MvpView {

    void onSuccess(List<Datum> upcomingTripDetails);
    void onError(Throwable e);
}
