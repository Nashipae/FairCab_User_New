package com.faircab.user.ui.fragment.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.faircab.user.R;
import com.faircab.user.base.BaseFragment;
import com.faircab.user.common.EqualSpacingItemDecoration;
import com.faircab.user.data.SharedHelper;
import com.faircab.user.data.network.APIClient;
import com.faircab.user.data.network.model.EstimateFare;
import com.faircab.user.data.network.model.Message;
import com.faircab.user.data.network.model.Provider;
import com.faircab.user.data.network.model.Service;
import com.faircab.user.ui.activity.main.MainActivity;
import com.faircab.user.ui.activity.payment.PaymentActivity;
import com.faircab.user.ui.adapter.ServiceAdapter;
import com.faircab.user.ui.fragment.RateCardFragment;
import com.faircab.user.ui.fragment.book_ride.BookRideFragment;
import com.faircab.user.ui.fragment.schedule.ScheduleFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.faircab.user.base.BaseActivity.RIDE_REQUEST;
import static com.faircab.user.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

public class ServiceFragment extends BaseFragment implements ServiceIView {

    private ServicePresenter<ServiceFragment> presenter = new ServicePresenter<>();

    @BindView(R.id.service_rv)
    RecyclerView serviceRv;
    @BindView(R.id.capacity)
    TextView capacity;
    @BindView(R.id.payment_type)
    TextView paymentType;
    @BindView(R.id.error_layout)
    TextView errorLayout;
    Unbinder unbinder;
    ServiceAdapter adapter;
    List<Service> mServices = new ArrayList<>();
    @BindView(R.id.use_wallet)
    CheckBox useWallet;
    @BindView(R.id.wallet_balance)
    TextView walletBalance;
    @BindView(R.id.surge_value)
    TextView surgeValue;
    @BindView(R.id.tv_demand)
    TextView tvDemand;

    private boolean isFromAdapter = true;
    private int servicePos = 0;
    private EstimateFare mEstimateFare;
    private double walletAmount;
    private int surge;
    int isWithCable=0;
    public ServiceFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_service;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        String lat = String.valueOf(RIDE_REQUEST.get("s_latitude"));
        String lon = String.valueOf(RIDE_REQUEST.get("s_longitude"));

        Log.e("tag" , "source lat is : "+lat);
        Log.e("tag" , "source lang is : "+lon);

        presenter.services(lat,lon);
        return view;
    }

    @OnClick({R.id.payment_type, R.id.get_pricing, R.id.schedule_ride, R.id.ride_now})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.payment_type:
                ((MainActivity) Objects.requireNonNull(getActivity())).updatePaymentEntities();
                startActivityForResult(new Intent(getActivity(), PaymentActivity.class), PICK_PAYMENT_METHOD);
                break;
            case R.id.get_pricing:
                if (adapter != null) {
                    isFromAdapter = false;
                    Service service = adapter.getSelectedService();
                    if (service != null) {
                        RIDE_REQUEST.put("service_type", service.getId());
                        if (RIDE_REQUEST.containsKey("service_type") && RIDE_REQUEST.get("service_type") != null) {
                            showLoading();
                            estimatedApiCall();

                            Log.e("tag" , "proceed btn click");
                        }
                    }
                }
                break;
            case R.id.schedule_ride:
                ((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(new ScheduleFragment());
                break;
            case R.id.ride_now:
                sendRequest();
                break;
            default:
                break;
        }
    }

    private void estimatedApiCall() {
        Call<EstimateFare> call = APIClient.getAPIClient().estimateFare(RIDE_REQUEST);
        call.enqueue(new Callback<EstimateFare>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<EstimateFare> call,
                                   @NonNull Response<EstimateFare> response) {
                try {
                    hideLoading();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (response.body() != null) {
                    EstimateFare estimateFare = response.body();

                    Log.e("tag" , "response.body() != null");

                    RateCardFragment.SERVICE = estimateFare.getService();
                    mEstimateFare = estimateFare;
                    surge = estimateFare.getSurge();
                    walletAmount = estimateFare.getWalletBalance();
                    SharedHelper.putKey(getContext(), "wallet", String.valueOf(estimateFare.getWalletBalance()));
                    if (walletAmount == 0) walletBalance.setVisibility(View.GONE);
                    else {
                        walletBalance.setVisibility(View.VISIBLE);
                        walletBalance.setText(
                                SharedHelper.getKey(getContext(), "currency") + " "
                                        + getNewNumberFormat(Double.parseDouble(String.valueOf(walletAmount))));
                    }
                    if (surge == 0) {
                        surgeValue.setVisibility(View.GONE);
                        tvDemand.setVisibility(View.GONE);
                    } else {
                        surgeValue.setVisibility(View.VISIBLE);
                        surgeValue.setText(estimateFare.getSurgeValue());
                        tvDemand.setVisibility(View.VISIBLE);
                    }
                    if (isFromAdapter) {
                        mServices.get(servicePos).setEstimatedTime(estimateFare.getTime());
                        RIDE_REQUEST.put("distance", estimateFare.getDistance());
                        adapter.setEstimateFare(mEstimateFare);
                        adapter.notifyDataSetChanged();
                        if (mServices.isEmpty()) errorLayout.setVisibility(View.VISIBLE);
                        else errorLayout.setVisibility(View.GONE);
                    } else {
                        if (adapter != null) {
                            isFromAdapter = false;
                            Service service = adapter.getSelectedService();
                            if (service != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("service_name", service.getName());
                                bundle.putSerializable("mService", service);
                                bundle.putSerializable("estimate_fare", estimateFare);
                                bundle.putDouble("use_wallet", walletAmount);
                                BookRideFragment bookRideFragment = new BookRideFragment();
                                bookRideFragment.setArguments(bundle);
                                ((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(bookRideFragment);
                            }
                        }
                    }
                } else if (response.raw().code() == 500) {
                    Log.e("tag" , "error code is 500 : ");

                    try {
                        JSONObject object = new JSONObject(response.errorBody().string());
                        if (object.has("error"))
                            Toast.makeText(activity(), object.optString("error"), Toast.LENGTH_SHORT).show();

                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<EstimateFare> call, @NonNull Throwable t) {
//                try {
//                    hideLoading();
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
                onErrorBase(t);
                System.out.println("RRR call = [" + call + "], t = [" + t + "]");
                Log.e("tag" , "RRR call = [" + call + "], t = [" + t + "]");
            }
        });
    }

    @Override
    public void onSuccess(List<Service> services) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (services != null && !services.isEmpty()) {
            RIDE_REQUEST.put("service_type", 1);
            // estimatedApiCall();
            mServices.clear();
            mServices.addAll(services);
            adapter = new ServiceAdapter(getActivity(), mServices, mListener, capacity, mEstimateFare);
            serviceRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            serviceRv.setItemAnimator(new DefaultItemAnimator());
            serviceRv.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.HORIZONTAL));
            serviceRv.setAdapter(adapter);

            if (adapter != null) {
                Service mService = adapter.getSelectedService();
                if (mService != null) RIDE_REQUEST.put("service_type", mService.getId());
            }
            mListener.whenClicked(0);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccess(EstimateFare estimateFare) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (estimateFare != null) {
            mEstimateFare = estimateFare;
            double walletAmount = estimateFare.getWalletBalance();
            SharedHelper.putKey(getContext(), "wallet",
                    String.valueOf(estimateFare.getWalletBalance()));
            if (walletAmount == 0) walletBalance.setVisibility(View.GONE);
            else {
                walletBalance.setVisibility(View.VISIBLE);
                walletBalance.setText(
                        SharedHelper.getKey(getContext(), "currency") + " "
                                + getNewNumberFormat(Double.parseDouble(String.valueOf(walletAmount))));
            }
            if (estimateFare.getSurge() == 0) {
                surgeValue.setVisibility(View.GONE);
                tvDemand.setVisibility(View.GONE);
            } else {
                surgeValue.setVisibility(View.VISIBLE);
                surgeValue.setText(estimateFare.getSurgeValue());
                tvDemand.setVisibility(View.VISIBLE);
            }
            if (isFromAdapter) {
                mServices.get(servicePos).setEstimatedTime(estimateFare.getTime());
                RIDE_REQUEST.put("distance", estimateFare.getDistance());
                adapter.notifyDataSetChanged();
                if (mServices.isEmpty()) errorLayout.setVisibility(View.VISIBLE);
                else errorLayout.setVisibility(View.GONE);
            } else {
                if (adapter != null) {
                    isFromAdapter = false;
                    Service service = adapter.getSelectedService();
                    if (service != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("service_name", service.getName());
                        bundle.putSerializable("mService", service);
                        bundle.putSerializable("estimate_fare", estimateFare);
                        bundle.putDouble("use_wallet", walletAmount);
                        BookRideFragment bookRideFragment = new BookRideFragment();
                        bookRideFragment.setArguments(bundle);
                        ((MainActivity) getActivity()).changeFragment(bookRideFragment);
                    }
                }
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            RIDE_REQUEST.put("payment_mode", data.getStringExtra("payment_mode"));
            if (data.getStringExtra("payment_mode").equals("CARD")) {
                RIDE_REQUEST.put("card_id", data.getStringExtra("card_id"));
                RIDE_REQUEST.put("card_last_four", data.getStringExtra("card_last_four"));
            }
            initPayment(paymentType);
        }
    }

    private ServiceListener mListener = pos -> {
        isFromAdapter = true;
        servicePos = pos;
        if(mServices.get(pos).getId()==6){
            showInstructionDialog(pos);
        } else if (mServices.get(pos).getId()==7){
            showBootDialog(pos);
        } else {
            RIDE_REQUEST.put("service_type", mServices.get(pos).getId());
            showLoading();
            estimatedApiCall();
            List<Provider> providers = new ArrayList<>();
            for (Provider provider : SharedHelper.getProviders(Objects.requireNonNull(getActivity())))
                if (provider.getProviderService().getServiceTypeId().equals(mServices.get(pos).getId()))
                    providers.add(provider);

            ((MainActivity) getActivity()).setSpecificProviders(providers);

        }

    };

    public interface ServiceListener {
        void whenClicked(int pos);
    }

    private void sendRequest() {
        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        map.put("use_wallet", useWallet.isChecked() ? 1 : 0);
        showLoading();
        presenter.rideNow(map);
    }

    @Override
    public void onSuccess(@NonNull Message object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        activity().sendBroadcast(new Intent("INTENT_FILTER"));
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initPayment(paymentType);
    }


    private void showInstructionDialog(int pos) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_tow_truck);
        EditText instructionsEt = dialog.findViewById(R.id.etInstructions);

        RadioGroup radioGroup = dialog.findViewById(R.id.rg_c);
        TextView tvProceed = dialog.findViewById(R.id.tvProceed);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_wc) {
                    isWithCable=1;
                } else if(checkedId == R.id.rb_woc) {
                    isWithCable=0;
                }
            }

        });


        tvProceed.setOnClickListener(v -> {
            String instructions = instructionsEt.getText().toString();
            RIDE_REQUEST.put("service_type", mServices.get(pos).getId());
            RIDE_REQUEST.put("instructions", instructions);
            RIDE_REQUEST.put("is_booster_cable", isWithCable);
            showLoading();
            estimatedApiCall();
            List<Provider> providers = new ArrayList<>();
            for (Provider provider : SharedHelper.getProviders(Objects.requireNonNull(getActivity())))
                if (provider.getProviderService().getServiceTypeId().equals(mServices.get(pos).getId()))
                    providers.add(provider);

            ((MainActivity) getActivity()).setSpecificProviders(providers);

            dialog.dismiss();
        });

        tvCancel.setOnClickListener(v -> {

            RIDE_REQUEST.put("service_type", mServices.get(pos).getId());
            RIDE_REQUEST.put("instructions", "");
            RIDE_REQUEST.put("is_booster_cable", 0);
            showLoading();
            estimatedApiCall();
            List<Provider> providers = new ArrayList<>();
            for (Provider provider : SharedHelper.getProviders(Objects.requireNonNull(getActivity())))
                if (provider.getProviderService().getServiceTypeId().equals(mServices.get(pos).getId()))
                    providers.add(provider);

            ((MainActivity) getActivity()).setSpecificProviders(providers);
            dialog.dismiss();
        });

        dialog.show();

        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int) WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(width, height);
    }

    private void showBootDialog(int pos) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_boot);
        RadioGroup radioGroup = dialog.findViewById(R.id.rg_c);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_wc) {
                isWithCable=1;
                } else if(checkedId == R.id.rb_woc) {
                    isWithCable=0;
                }
            }

        });
        TextView tvProceed = dialog.findViewById(R.id.tvProceed);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);
        tvProceed.setOnClickListener(v -> {
            RIDE_REQUEST.put("service_type", mServices.get(pos).getId());
            RIDE_REQUEST.put("instructions", "");
            RIDE_REQUEST.put("is_booster_cable", isWithCable);
            showLoading();
            estimatedApiCall();
            List<Provider> providers = new ArrayList<>();
            for (Provider provider : SharedHelper.getProviders(Objects.requireNonNull(getActivity())))
                if (provider.getProviderService().getServiceTypeId().equals(mServices.get(pos).getId()))
                    providers.add(provider);

            ((MainActivity) getActivity()).setSpecificProviders(providers);

            dialog.dismiss();
        });

        tvCancel.setOnClickListener(v -> {

            dialog.dismiss();
        });

        dialog.show();

        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int) WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(width, height);
    }

}
