package com.faircab.user.ui.activity.social;

import com.faircab.user.base.MvpView;
import com.faircab.user.data.network.model.Token;
import com.faircab.user.data.network.model.VerificationReponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SocialIView extends MvpView{
    void onSuccess(Token token);
    void onSuccess(VerificationReponse verificationReponse);
    void onError(Throwable e);
}
