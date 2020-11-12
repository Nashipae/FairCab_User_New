package com.faircab.user.ui.fragment.invoice;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.Message;

public interface InvoiceIView extends MvpView{
    void onSuccess(Message message);
    void onSuccess(Object o);
    void onError(Throwable e);
}
