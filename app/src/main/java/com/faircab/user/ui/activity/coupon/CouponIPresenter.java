package com.faircab.user.ui.activity.coupon;

import com.faircab.user.base.MvpPresenter;

public interface CouponIPresenter<V extends CouponIView> extends MvpPresenter<V> {
    void coupon();
}
