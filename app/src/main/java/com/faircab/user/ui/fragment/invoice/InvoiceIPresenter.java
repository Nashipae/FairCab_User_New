package com.faircab.user.ui.fragment.invoice;

import com.faircab.user.base.MvpPresenter;
import java.util.HashMap;

public interface InvoiceIPresenter<V extends InvoiceIView> extends MvpPresenter<V> {
    void payment(Integer requestId, Double tips);
    void updateRide(HashMap<String, Object> obj);
}
