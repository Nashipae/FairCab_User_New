package com.faircab.user.ui.activity.card;

import com.faircab.user.base.MvpPresenter;


public interface CarsIPresenter<V extends CardsIView> extends MvpPresenter<V> {
    void card();
}
