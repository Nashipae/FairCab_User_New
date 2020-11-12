package com.faircab.user.ui.activity.coupon;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.PromoResponse;

public interface CouponIView extends MvpView {
    void onSuccess(PromoResponse object);
    void onError(Throwable e);
}
