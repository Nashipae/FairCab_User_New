package com.faircab.user.ui.fragment.service_flow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.faircab.user.R;
import com.faircab.user.base.BaseFragment;
import com.faircab.user.chat.ChatActivity;
import com.faircab.user.common.CancelRequestInterface;
import com.faircab.user.common.fcm.MyFireBaseMessagingService;
import com.faircab.user.data.SharedHelper;
import com.faircab.user.data.network.APIClient;
import com.faircab.user.data.network.model.AddedStop;
import com.faircab.user.data.network.model.DataResponse;
import com.faircab.user.data.network.model.Datum;
import com.faircab.user.data.network.model.EstimateFare;
import com.faircab.user.data.network.model.Provider;
import com.faircab.user.data.network.model.ProviderService;
import com.faircab.user.data.network.model.Service;
import com.faircab.user.data.network.model.ServiceType;
import com.faircab.user.data.network.model.UpdateStops;
import com.faircab.user.ui.activity.add_remove_stops.AddRemoveStopsActivity;
import com.faircab.user.ui.activity.edit_location.EditLocationActivity;
import com.faircab.user.ui.activity.location_pick.LocationPickActivity;
import com.faircab.user.ui.activity.main.MainActivity;
import com.faircab.user.ui.fragment.RateCardFragment;
import com.faircab.user.ui.fragment.book_ride.BookRideFragment;
import com.faircab.user.ui.fragment.cancel_ride.CancelRideDialogFragment;
import com.faircab.user.ui.fragment.cancel_ride.CancelRideIView;
import com.faircab.user.ui.fragment.cancel_ride.CancelRidePresenter;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.faircab.user.MvpApplication.EDIT_LOCATION_REQUEST_CODE;
import static com.faircab.user.MvpApplication.PERMISSIONS_REQUEST_PHONE;
import static com.faircab.user.MvpApplication.PICK_LOCATION_REQUEST_CODE;
import static com.faircab.user.base.BaseActivity.DATUM;
import static com.faircab.user.base.BaseActivity.RIDE_REQUEST;
import static com.faircab.user.data.SharedHelper.getKey;

public class ServiceFlowFragment extends BaseFragment
        implements ServiceFlowIView, CancelRequestInterface, DirectionCallback , CancelRideIView {

    Unbinder unbinder;

    @BindView(R.id.sos)
    TextView sos;
    @BindView(R.id.otp)
    TextView otp;
    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.first_name)
    TextView firstName;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.share_ride)
    Button sharedRide;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.service_type_name)
    TextView serviceTypeName;
    @BindView(R.id.service_number)
    TextView serviceNumber;
    @BindView(R.id.service_model)
    TextView serviceModel;
    @BindView(R.id.call)
    Button call;
    @BindView(R.id.chat)
    FloatingActionButton chat;
    @BindView(R.id.edit_destination)
    FloatingActionButton editLocation;
    @BindView(R.id.provider_eta)
    TextView providerEta;
    private Runnable runnable;
    private Handler handler;
    private int delay = 5000;

    private Context thisContext;
    EditText cancel_reason;
    AlertDialog cancelDialog;


    private String providerPhoneNumber = null;
    private String shareRideText = "";
    private LatLng providerLatLng;
    private ServiceFlowPresenter<ServiceFlowFragment> presenter = new ServiceFlowPresenter<>();
    private CancelRidePresenter<ServiceFlowFragment> cancelPresenter = new CancelRidePresenter<>();

    HashMap<String, Object> updateRideMap;
    HashMap<String, Object> estimateFareMap;
    private CancelRequestInterface callback;
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d("RRR latitude", "" + intent.getDoubleExtra("latitude", 0));
                Log.d("RRR longitude", "" + intent.getDoubleExtra("longitude", 0));
                providerLatLng = new LatLng(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0));
                ((MainActivity) context).addCar(providerLatLng);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public ServiceFlowFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_service_flow;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        activity().registerReceiver(myReceiver, new IntentFilter(MyFireBaseMessagingService.INTENT_PROVIDER));
        callback = this;
        this.thisContext = getContext();
        presenter.attachView(this);
        cancelPresenter.attachView(this);

        if (DATUM != null) initView(DATUM);
        return view;
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        if (myReceiver != null) try {
            activity().unregisterReceiver(myReceiver);
            myReceiver = null;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }

    @OnClick({R.id.sos, R.id.cancel, R.id.share_ride, R.id.call,R.id.edit_destination, R.id.chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sos:
                sos();
                break;
            case R.id.cancel:
//                CancelRideDialogFragment cancelRideDialogFragment = new CancelRideDialogFragment(callback);
//                cancelRideDialogFragment.show(activity().getSupportFragmentManager(), cancelRideDialogFragment.getTag());
                cancelRideDialog();
                break;
            case R.id.share_ride:
                sharedRide();
                break;
            case R.id.call:
                callPhoneNumber(providerPhoneNumber);
                break;
            case R.id.chat:
                if (DATUM != null) {
                    Intent i = new Intent(activity(), ChatActivity.class);
                    i.putExtra("request_id", String.valueOf(DATUM.getId()));
                    startActivity(i);
                }
                break;
            case R.id.edit_destination:
                if(DATUM != null) {

                    Intent intent = new Intent(activity(), AddRemoveStopsActivity.class);
                    intent.putExtra("destClick", "isDest");
                    intent.putExtra("isSetting", "destination");
                    Gson gson=new Gson();
                    String positions=gson.toJson(DATUM.getStops());
                    intent.putExtra("positions", positions);
//                    intent.putExtra("destination", DATUM.getDAddress());
//                    intent.putExtra("destination_latitude", DATUM.getDLatitude());
//                    intent.putExtra("destination_longitude", DATUM.getDLongitude());
                    intent.putExtra("fieldClicked", "dropAddress");
                    startActivityForResult(intent, EDIT_LOCATION_REQUEST_CODE);
//                    Intent intent = new Intent(activity(), EditLocationActivity.class);
//                    intent.putExtra("destClick", "isDest");
//                    intent.putExtra("isSetting", "destination");
//                    intent.putExtra("destination", DATUM.getDAddress());
//                    intent.putExtra("destination_latitude", DATUM.getDLatitude());
//                    intent.putExtra("destination_longitude", DATUM.getDLongitude());
//                    intent.putExtra("fieldClicked", "dropAddress");
//                    startActivityForResult(intent, EDIT_LOCATION_REQUEST_CODE);
                }
                break;
        }
    }

    @SuppressLint({"StringFormatInvalid", "RestrictedApi"})
    private void initView(Datum datum) {
        Provider provider = datum.getProvider();
        if (provider != null) {
            firstName.setText(String.format("%s %s", provider.getFirstName(), provider.getLastName()));
            rating.setRating(Float.parseFloat(provider.getRating()));
            Glide.with(activity())
                    .load(provider.getAvatar())
                    .apply(RequestOptions
                            .placeholderOf(R.drawable.ic_user_placeholder)
                            .dontAnimate()
                            .error(R.drawable.ic_user_placeholder))
                    .into(avatar);
            providerPhoneNumber = provider.getMobile();
        }

        ServiceType serviceType = datum.getServiceType();
        if (serviceType != null) {
            serviceTypeName.setText(serviceType.getName());
            Glide.with(activity())
                    .load(serviceType.getImage())
                    .apply(RequestOptions.placeholderOf(R.drawable.car1)
                            .dontAnimate()
                            .error(R.drawable.car1))
                    .into(image);
        }

        if ("PICKEDUP".equalsIgnoreCase(datum.getStatus())) {
            chat.setVisibility(View.GONE);
        } else {
            chat.setVisibility(View.VISIBLE);
        }

        if ("STARTED".equalsIgnoreCase(datum.getStatus())|| "PICKEDUP".equalsIgnoreCase(datum.getStatus()) ||
                "DROPPED".equalsIgnoreCase(datum.getStatus())) {

            handler = new Handler();
            runnable = () -> {
                try {
                    if ( "PICKEDUP".equalsIgnoreCase(datum.getStatus()) ||
                            "DROPPED".equalsIgnoreCase(datum.getStatus())){
                        Double lat = (Double) RIDE_REQUEST.get("d_latitude");
                        Double lng = (Double) RIDE_REQUEST.get("d_longitude");
                        calculateETA(lat,lng);
                    }else {
                        Double lat = (Double) RIDE_REQUEST.get("s_latitude");
                        Double lng = (Double) RIDE_REQUEST.get("s_longitude");
                        calculateETA(lat,lng);
                    }
                    handler.postDelayed(runnable, delay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            handler.postDelayed(runnable, delay);
        }


        ProviderService providerService = datum.getProviderService();
        if (providerService != null) {
            serviceNumber.setText(providerService.getServiceNumber());
            serviceModel.setText(providerService.getServiceModel());
        }

        otp.setText(getString(R.string.otp_, datum.getOtp()));
        shareRideText = getString(R.string.app_name) + ": "
                + datum.getUser().getFirstName() + " " + datum.getUser().getLastName() + " is riding in "
                + datum.getServiceType().getName() + " would like to share his ride "
                + "http://maps.google.com/maps?q=loc:" + datum.getStops().get(datum.getStops().size()-1).getDLatitude() + "," + datum.getStops().get(datum.getStops().size()-1).getDLongitude();
//                + "http://maps.google.com/maps?q=loc:" + datum.getDLatitude() + "," + datum.getDLongitude();

        switch (datum.getStatus()) {
            case "STARTED":
                if(provider!=null && provider.getLatitude()!=null && provider.getLongitude()!=null) {
                    providerLatLng = new LatLng(provider.getLatitude(), provider.getLongitude());
                    LatLng origin = new LatLng(datum.getSLatitude(), datum.getSLongitude());
                    getDistance(providerLatLng, origin);
                }
                status.setText(R.string.driver_accepted_your_request);
                break;
            case "ARRIVED":
                status.setText(R.string.driver_has_arrived_your_location);
                break;
            case "PICKEDUP":
                status.setText(R.string.you_are_on_ride);
                cancel.setVisibility(View.GONE);
                sharedRide.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

//        if ("STARTED".equalsIgnoreCase(datum.getStatus())) {
//            LatLng source = new LatLng(datum.getProvider().getLatitude(), datum.getProvider().getLongitude());
//            LatLng destination = new LatLng(datum.getSLatitude(), datum.getSLongitude());
//            ((MainActivity) Objects.requireNonNull(getActivity())).drawRoute(source, destination);
//        } else {
//            LatLng origin = new LatLng(datum.getSLatitude(), datum.getSLongitude());
//            LatLng destination = new LatLng(datum.getDLatitude(), datum.getDLongitude());
//            ((MainActivity) Objects.requireNonNull(getActivity())).drawRoute(origin, destination);
//        }

    }

    private void calculateETA(Double lat, Double lng) {
        GoogleDirection
                .withServerKey(SharedHelper.getKey(activity(), "map_key"))
                .from(new LatLng(lat, lng))
                .to(new LatLng(DATUM.getProvider().getLatitude(), DATUM.getProvider().getLongitude()))
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            Route route = direction.getRouteList().get(0);
                            if (!route.getLegList().isEmpty()) {
                                Leg leg = route.getLegList().get(0);
                                providerEta.setVisibility(View.VISIBLE);
                                String arrivalTime = String.valueOf(leg.getDuration().getText());
                                if (arrivalTime.contains("hours"))
                                    arrivalTime = arrivalTime.replace("hours", "h\n");
                                else if (arrivalTime.contains("hour"))
                                    arrivalTime = arrivalTime.replace("hour", "h\n");
                                if (arrivalTime.contains("mins"))
                                    arrivalTime = arrivalTime.replace("mins", "min");
                                providerEta.setText("ETA :" + " " + arrivalTime);
                            }
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Un used
                    }
                });
    }

    private void sos() {
        new AlertDialog.Builder(getContext())
                .setTitle(getContext().getResources().getString(R.string.sos_alert))
                .setMessage(R.string.are_sure_you_want_to_emergency_alert)
                .setCancelable(true)
                .setPositiveButton(getContext().getResources().getString(R.string.yes), (dialog, which) -> callPhoneNumber(SharedHelper.getKey(getContext(), "sosNumber")))
                .setNegativeButton(getContext().getResources().getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show();
    }

    private void callPhoneNumber(String mobileNumber) {
        if (mobileNumber != null && !mobileNumber.isEmpty()) {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobileNumber)));
        }
    }

    private void sharedRide() {
        try {
            String appName = getString(R.string.app_name) + " " + getString(R.string.share_ride);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareRideText);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share Ride"));
//            startActivity(sendIntent);
        } catch (Exception e) {
            Toast.makeText(activity(), "applications not found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(DataResponse dataResponse) {
        System.out.println("RRR ServiceFlowFragment checkStatusResponse = " + printJSON(dataResponse));
        if (!dataResponse.getData().isEmpty()) initView(dataResponse.getData().get(0));
    }

    @Override
    public void onUpdateRideSuccess(Object o) {
        Toast.makeText(getContext(),"Destination Changed Successfully",Toast.LENGTH_SHORT).show();
        Log.d("UpdateRide",o.toString());
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }


    @Override
    public void onSuccessCancel(Object object) {
        try {
            Log.e("tag" , "cancel on success :");

            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (DATUM != null)
            FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(DATUM.getId()));
        Intent intent = new Intent("INTENT_FILTER");
        getActivity().sendBroadcast(intent);
        callback.cancelRequestMethod();
    }

    @Override
    public void onErrorCancel(Throwable e) {
        handleError(e);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(activity(), "Permission Granted. Try Again!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void cancelRequestMethod() {
    }

    public void getDistance(LatLng source, LatLng destination) {
        GoogleDirection.withServerKey(SharedHelper.getKey(activity(), "map_key"))
                .from(source)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (isAdded()) {
            if (direction.isOK()) {
                Route route = direction.getRouteList().get(0);
                if (!route.getLegList().isEmpty()) {
                    Leg leg = route.getLegList().get(0);
                    //      TODO: Commented by Rajaganapathi... cos some time screens blinks
                    //      status.setText(getString(R.string.driver_accepted_your_request_, leg.getDuration().getText()));
                }
            } else
                Toast.makeText(activity(), direction.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    private void cancelRideDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_cancel_ride_dialog, null);

        cancel_reason = view.findViewById(R.id.cancel_reason);
        Button submit = view.findViewById(R.id.submit);
        Button dismiss = view.findViewById(R.id.dismiss);

        builder.setView(view);
        cancelDialog = builder.create();
        cancelDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        submit.setOnClickListener(view1 -> {

            if (thisContext != null)
            {
                Datum datum = DATUM;
                HashMap<String, Object> map = new HashMap<>();
                map.put("request_id", datum.getId());
                map.put("cancel_reason", cancel_reason.getText().toString());
                showLoading();
                cancelPresenter.cancelRequest(map);
                try {
                    hideLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cancelDialog.dismiss();

            }

        });
        dismiss.setOnClickListener(view1 -> {


            try {
                if (thisContext != null)
                    cancelDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }


        });

        cancelDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_LOCATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String positions = data.getStringExtra("positions");
//                String address= data.getStringExtra("d_address");
//                Double dlatitude= data.getDoubleExtra("d_latitude",0);
//                Double dlongitude= data.getDoubleExtra("d_longitude",0);

                updateRideMap = new HashMap<>();
                estimateFareMap = new HashMap<>();
                if(DATUM!=null){
                    updateRideMap.put("request_id", DATUM.getId());
                    updateRideMap.put("positions", positions);
//                    updateRideMap.put("address", address);
//                    updateRideMap.put("latitude", dlatitude);
//                    updateRideMap.put("longitude", dlongitude);

                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<UpdateStops>>() {}.getType();
                    ArrayList<UpdateStops> stopsArrayList = gson.fromJson(positions, type);
                    ArrayList<UpdateStops> newPositions = new ArrayList<>();
                    for (UpdateStops updatestop:stopsArrayList) {
                        if(updatestop.action.equals("create") || updatestop.action.equals("update")){
                            newPositions.add(updatestop);
                        }
                    }

                    String newpositions = gson.toJson(newPositions);
                    estimateFareMap.put("s_latitude",DATUM.getSLatitude());
                    estimateFareMap.put("s_longitude",DATUM.getSLongitude());
                    estimateFareMap.put("positions",newpositions);
//                    estimateFareMap.put("d_latitude",dlatitude);
//                    estimateFareMap.put("d_longitude",dlongitude);
                    estimateFareMap.put("service_type",DATUM.getServiceTypeId());

                    estimatedApiCall();
                }

                }
        }
    }

    private void estimatedApiCall() {


        Call<EstimateFare> call = APIClient.getAPIClient().estimateFare(estimateFareMap);
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
                    showEstimatedFare(String.valueOf(estimateFare.getEstimatedFare()));

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
                onErrorBase(t);
                System.out.println("RRR call = [" + call + "], t = [" + t + "]");
                Log.e("tag" , "RRR call = [" + call + "], t = [" + t + "]");
            }
        });
    }



    private void showEstimatedFare(String fare) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_confirm_fare);
        TextView tvEstimatedFare = dialog.findViewById(R.id.tvEstimatedFare);
        tvEstimatedFare.setText("C$ "+fare);
        TextView tvConfirm = dialog.findViewById(R.id.tvConfirm);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);

        tvConfirm.setOnClickListener(v -> {
            presenter.updateRide(updateRideMap);
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
