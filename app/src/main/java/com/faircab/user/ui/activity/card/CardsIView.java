package com.faircab.user.ui.activity.card;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.Card;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface CardsIView extends MvpView{
    void onSuccess(List<Card> cardList);
    void onError(Throwable e);
}
