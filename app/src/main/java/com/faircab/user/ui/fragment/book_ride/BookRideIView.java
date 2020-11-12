package com.faircab.user.ui.fragment.book_ride;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.Message;
import com.faircab.user.data.network.model.PromoResponse;


public interface BookRideIView extends MvpView{
    void onSuccess(Message object);
    void onError(Throwable e);
    void onSuccessCoupon(PromoResponse promoResponse);
}
