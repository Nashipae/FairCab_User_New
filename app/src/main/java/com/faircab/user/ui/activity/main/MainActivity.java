package com.faircab.user.ui.activity.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.faircab.user.BuildConfig;
import com.faircab.user.R;
import com.faircab.user.base.BaseActivity;
import com.faircab.user.common.Constants;
import com.faircab.user.common.InfoWindowData;
import com.faircab.user.common.LocaleHelper;
import com.faircab.user.common.Utilities;
import com.faircab.user.common.fcm.MyFireBaseMessagingService;
import com.faircab.user.data.SharedHelper;
import com.faircab.user.data.network.model.AddressResponse;
import com.faircab.user.data.network.model.DataResponse;
import com.faircab.user.data.network.model.InitSettingsResponse;
import com.faircab.user.data.network.model.Provider;
import com.faircab.user.data.network.model.AddedStop;
import com.faircab.user.data.network.model.Stop;
import com.faircab.user.data.network.model.User;
import com.faircab.user.ui.activity.corporate.CorporateActivity;
import com.faircab.user.ui.activity.coupon.CouponActivity;
import com.faircab.user.ui.activity.help.HelpActivity;
import com.faircab.user.ui.activity.location_pick.LocationPickActivity;
import com.faircab.user.ui.activity.passbook.WalletHistoryActivity;
import com.faircab.user.ui.activity.payment.PaymentActivity;
import com.faircab.user.ui.activity.profile.ProfileActivity;
import com.faircab.user.ui.activity.setting.SettingsActivity;
import com.faircab.user.ui.activity.wallet.WalletActivity;
import com.faircab.user.ui.activity.your_trips.YourTripActivity;
import com.faircab.user.ui.fragment.RateCardFragment;
import com.faircab.user.ui.fragment.book_ride.BookRideFragment;
import com.faircab.user.ui.fragment.invoice.InvoiceFragment;
import com.faircab.user.ui.fragment.rate.RatingDialogFragment;
import com.faircab.user.ui.fragment.schedule.ScheduleFragment;
import com.faircab.user.ui.fragment.searching.SearchingFragment;
import com.faircab.user.ui.fragment.service.ServiceFragment;
import com.faircab.user.ui.fragment.service_flow.ServiceFlowFragment;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.faircab.user.MvpApplication.DEFAULT_ZOOM;
import static com.faircab.user.MvpApplication.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.faircab.user.MvpApplication.PICK_LOCATION_REQUEST_CODE;
import static com.faircab.user.MvpApplication.mLastKnownLocation;

//      TODO: Payment Gateway
//import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
//import com.braintreepayments.api.models.PayPalAccountNonce;
//import com.braintreepayments.api.models.PaymentMethodNonce;

public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener, DirectionCallback,
        MainIView,
//        PaymentMethodNonceCreatedListener,
        LocationListener {
    private boolean adjustBounds = true;

    private static final String TAG = "MainActivity";

    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.ivBack)
    FloatingActionButton ivBack;
    @BindView(R.id.gps)
    ImageView gps;
    @BindView(R.id.source)
    TextView sourceTxt;
    @BindView(R.id.destination)
    TextView destinationTxt;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.top_layout)
    LinearLayout topLayout;
    @BindView(R.id.pick_location_layout)
    LinearLayout pickLocationLayout;
    @BindView(R.id.stops_layout)
    LinearLayout stopsLayout;
    @BindView(R.id.top_location)
    RelativeLayout top_location;
    @BindView(R.id.bottom_location)
    LinearLayout bottom_location;
    @BindView(R.id.stops_destinations)
    LinearLayout stopsDestinations;
    @BindView(R.id.stop1_address_layout)
    CardView stop1Layout;
    @BindView(R.id.stop2_address_layout)
    CardView stop2Layout;
    @BindView(R.id.stop3_address_layout)
    CardView stop3Layout;

    @BindView(R.id.stop1_daddress_layout)
    LinearLayout stop1DLayout;
    @BindView(R.id.stop2_daddress_layout)
    LinearLayout stop2DLayout;
    @BindView(R.id.stop3_daddress_layout)
    LinearLayout stop3DLayout;

    @BindView(R.id.stop1_address)
    TextView stop1Address;
    @BindView(R.id.stop2_address)
    TextView stop2Address;
    @BindView(R.id.stop3_address)
    TextView stop3Address;
    @BindView(R.id.stop1_daddress)
    TextView stop1DAddress;
    @BindView(R.id.stop2_daddress)
    TextView stop2DAddress;
    @BindView(R.id.stop3_daddress)
    TextView stop3DAddress;


    @BindView(R.id.llPickHomeAdd)
    LinearLayout llPickHomeAdd;
    @BindView(R.id.llPickWorkAdd)
    LinearLayout llPickWorkAdd;
    private InfoWindowData destinationLeg;

    public static String currentStatus = "EMPTY";
    private boolean doubleBackToExitPressedOnce = false;

    private LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private boolean mLocationPermissionGranted;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocation;

    private BottomSheetBehavior bottomSheetBehavior;
    private MainPresenter<MainActivity> mainPresenter = new MainPresenter<>();

    private CircleImageView picture;
    private TextView name;
    private String STATUS = "";
    private TextView sub_name;
    private boolean initialProcess = true;
    private LatLng newPosition = null;
    private Marker marker;
    private HashMap<Integer, Marker> providersMarker = new HashMap<>();
    private PlaceDetectionClient mPlaceDetectionClient;
    private DataResponse checkStatusResponse = new DataResponse();

    private Runnable r;
    private Handler h;
    private int delay = 5000;
    private com.faircab.user.data.network.model.Address home = null, work = null;

    private DatabaseReference mProviderLocation;
    private Location mLocation;

    boolean check_current_location = false;
    public boolean updateRoute = true;
    public boolean isCPRouteTrigged = false;
    public boolean isPDRouteTrigged = false;
    public long shortDistance = 0;
    public Location currLocation;
    public static String currentStopHeader = "Stop 1";
    public static String currentStopAddress = "";
    public static double currentStopDestLatitude = 0;
    public static double currentStopDestLongitude = 0;
    public static int currentStopId;
    public boolean inRide = false;

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null) {
                String key = intent.getExtras().getString("schedule");
                if (key != null && key.equalsIgnoreCase("EMPTY"))
                    changeFlow("EMPTY");
            } else
                mainPresenter.checkStatus();
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ButterKnife.bind(this);

        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);

        registerReceiver(myReceiver, new IntentFilter(MyFireBaseMessagingService.INTENT_FILTER));

        mainPresenter.attachView(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        picture = headerView.findViewById(R.id.picture);
        name = headerView.findViewById(R.id.name);
        sub_name = headerView.findViewById(R.id.sub_name);
        headerView.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, picture, ViewCompat.getTransitionName(picture));
            startActivity(new Intent(MainActivity.this, ProfileActivity.class), options.toBundle());
        });

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bottomSheetBehavior = BottomSheetBehavior.from(container);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, @BottomSheetBehavior.State int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_EXPANDED:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        BottomSheetBehavior.from(container).setHideable(true);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        h = new Handler();
        r = () -> {
            mainPresenter.checkStatus();
            h.postDelayed(r, delay);
        };
        h.postDelayed(r, delay);

    }

    @Override
    public void onResume() {
        super.onResume();
        mainPresenter.profile();
        mainPresenter.address();
        mainPresenter.checkStatus();
        mainPresenter.settings();
        if (currentStatus.equalsIgnoreCase("COMPLETED") && DATUM.getPaid() == 1) {
            changeFlow("RATING");
        }
    }

    @Override
    protected void onDestroy() {
        mainPresenter.onDetach();
        unregisterReceiver(myReceiver);
        h.removeCallbacks(r);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else if (inRide) {
            if (doubleBackToExitPressedOnce) {
                finish();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    mainPresenter.checkStatus();
                    changeFlow("EMPTY");
                    check_current_location = false;
                    showCurrentPlace(true);
                }
            } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show();
            }
        }

        stopsDestinations.setVisibility(View.GONE);
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_payment:
                startActivity(new Intent(this, PaymentActivity.class));
                break;
            case R.id.nav_your_trips:
                startActivity(new Intent(this, YourTripActivity.class));
                break;
            case R.id.nav_coupon:
                startActivity(new Intent(this, CouponActivity.class));
                break;
            case R.id.nav_wallet:
                startActivity(new Intent(this, WalletActivity.class));
                break;
            case R.id.nav_passbook:
                startActivity(new Intent(this, WalletHistoryActivity.class));
                break;
            case R.id.nav_corperate:
                startActivity(new Intent(this, CorporateActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.nav_share:
                shareApp();
                break;
            case R.id.nav_become_driver:
                alertBecomeDriver();
                break;
            case R.id.nav_logout:
                // alertLogout();
                ShowLogoutPopUp();
//                changeFragment(InvoiceFragment.newInstance());
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    public void ShowLogoutPopUp() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder
                .setMessage(getString(R.string.are_sure_you_want_to_logout))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                    mainPresenter.logout(SharedHelper.getKey(this, "user_id"));
                }).setNegativeButton(getString(R.string.no), (dialog, id) -> {
            dialog.cancel();
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void alertBecomeDriver() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.PROVIDER_PACKAGE_NAME));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onCameraIdle() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        if (STATUS.equals("SERVICE") || STATUS.equals("EMPTY")) try {
            CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
            HashMap<String, Object> map = new HashMap<>();
            map.put("latitude", cameraPosition.target.latitude);
            map.put("longitude", cameraPosition.target.longitude);
            mainPresenter.providers(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.menu, R.id.gps, R.id.source, R.id.destination, R.id.ivBack, R.id.llPickHomeAdd, R.id.llPickWorkAdd, R.id.stops_destinations})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu:
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else {
                    User user = new Gson().fromJson(SharedHelper.getKey(this, "userInfo"), User.class);
                    if (user != null) {
                        name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
                        sub_name.setText(user.getEmail());
                        SharedHelper.putKey(activity(), "picture", user.getPicture());
                        Glide.with(activity())
                                .load(BuildConfig.BASE_IMAGE_URL + user.getPicture())
                                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder)
                                        .dontAnimate()
                                        .error(R.drawable.ic_user_placeholder))
                                .into(picture);
                    }
                    drawerLayout.openDrawer(Gravity.START);
                }

                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.gps:
                if (mLastKnownLocation != null) {
                    LatLng currentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));
                    check_current_location = false;
                    showCurrentPlace(true);
                }
                break;
            case R.id.source:
                Intent sourceIntent = new Intent(this, LocationPickActivity.class);
                sourceIntent.putExtra("srcClick", "isSource");
                sourceIntent.putExtra("isSetting", "source");
                sourceIntent.putExtra("destination", destinationTxt.getText().toString());
                sourceIntent.putExtra("fieldClicked", "pickupAddress");
                startActivityForResult(sourceIntent, PICK_LOCATION_REQUEST_CODE);
                break;
            case R.id.destination:
                Intent intent = new Intent(this, LocationPickActivity.class);
                intent.putExtra("destClick", "isDest");
                intent.putExtra("isSetting", "destination");
                intent.putExtra("destination", destinationTxt.getText().toString());
                intent.putExtra("fieldClicked", "dropAddress");
                startActivityForResult(intent, PICK_LOCATION_REQUEST_CODE);
                break;
            case R.id.pick_location_layout:
                Intent pick_location_layout = new Intent(this, LocationPickActivity.class);
                pick_location_layout.putExtra("destClick", "isDest");
                pick_location_layout.putExtra("isSetting", "destination");
                pick_location_layout.putExtra("destination", destinationTxt.getText().toString());
                pick_location_layout.putExtra("fieldClicked", "dropAddress");
                startActivityForResult(pick_location_layout, PICK_LOCATION_REQUEST_CODE);
                break;
            case R.id.stops_destinations:
//            case R.id.stop1_address_layout:
//            case R.id.stop2_address_layout:
//            case R.id.stop3_address_layout:
                Intent stopsIntent = new Intent(this, LocationPickActivity.class);
                stopsIntent.putExtra("destClick", "isDest");
                stopsIntent.putExtra("isSetting", "destination");
                stopsIntent.putExtra("destination", destinationTxt.getText().toString());
                stopsIntent.putExtra("fieldClicked", "dropAddress");
                startActivityForResult(stopsIntent, PICK_LOCATION_REQUEST_CODE);
                break;

            case R.id.llPickHomeAdd:
                updateSavedAddress(home);
                break;
            case R.id.llPickWorkAdd:
                updateSavedAddress(work);
                break;
        }
    }

    private void updateSavedAddress(com.faircab.user.data.network.model.Address address) {

        ArrayList<AddedStop> addedStops = new ArrayList<>();

        addedStops.add(new AddedStop(address.getAddress(), address.getLatitude(), address.getLongitude()));
//        RIDE_REQUEST.put("d_address", address.getAddress());
//        RIDE_REQUEST.put("d_latitude", address.getLatitude());
//        RIDE_REQUEST.put("d_longitude", address.getLongitude());
//        destinationTxt.setText(String.valueOf(RIDE_REQUEST.get("d_address")));
        destinationTxt.setText(address.getAddress());
        Gson gson = new Gson();
        String destinationStops = gson.toJson(addedStops);
        RIDE_REQUEST.put("positions", destinationStops);


        if (RIDE_REQUEST.containsKey("s_address") && RIDE_REQUEST.containsKey("positions")) {
//        if (RIDE_REQUEST.containsKey("s_address") && RIDE_REQUEST.containsKey("d_address")) {
//            LatLng origin = new LatLng((Double) RIDE_REQUEST.get("s_latitude"), (Double) RIDE_REQUEST.get("s_longitude"));
//            LatLng destination = new LatLng((Double) RIDE_REQUEST.get("d_latitude"), (Double) RIDE_REQUEST.get("d_longitude"));
//            resetCheck();
//            drawRoute(origin, destination , "updateSavedAddress");
//            currentStatus = "SERVICE";
//            changeFlow(currentStatus);

            LatLng origin = new LatLng((Double) RIDE_REQUEST.get("s_latitude"), (Double) RIDE_REQUEST.get("s_longitude"));
            Type type = new TypeToken<ArrayList<AddedStop>>() {
            }.getType();
            ArrayList<AddedStop> stopsArrayList = gson.fromJson(destinationStops, type);

            ArrayList<LatLng> destinationStopsLatLng = new ArrayList<>();
            for (AddedStop stop : stopsArrayList) {
                destinationStopsLatLng.add(new LatLng(stop.getD_latitude(), stop.getD_longitude()));
            }
            resetCheck();
            drawRoute(origin, destinationStopsLatLng, "updateSavedAddress");
//            drawRoute(origin, destinationStopsLatLng , "onActivityResult");
            currentStatus = "SERVICE";
            changeFlow(currentStatus);

        }
    }


    @Override
    public void onCameraMove() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        } catch (Resources.NotFoundException e) {
            Log.d("Map:Style", "Can't find style. Error: ");
        }
        this.mGoogleMap = googleMap;

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        // showLoading();
        showCurrentPlace(false);

        changeFlow(currentStatus);
    }

    @Override
    public void onSuccess(DataResponse dataResponse) {
        this.checkStatusResponse = dataResponse;
        updatePaymentEntities();
        SharedHelper.putKey(this, "sosNumber", dataResponse.getSos());

        try {

            if (dataResponse.getData() != null && !dataResponse.getData().isEmpty() &&
                    dataResponse.getData().get(0).getProvider() != null) {
                DATUM = dataResponse.getData().get(0);
                provider = DATUM.getProvider();
                provider.setLatitude(provider.getLatitude());
                provider.setLongitude(provider.getLongitude());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (!dataResponse.getData().isEmpty()) {
            if (!currentStatus.equals(dataResponse.getData().get(0).getStatus())) {
                DATUM = dataResponse.getData().get(0);
                currentStatus = DATUM.getStatus();
                Log.e("tag", "status in onSuccess : " + currentStatus);
                changeFlow(currentStatus);
                if (dataResponse.getData().get(0).getStops() != null && dataResponse.getData().get(0).getStops().size() > 0) {
                    stopsLayout.setVisibility(View.VISIBLE);
                    stopsInfoDisplay(dataResponse.getData().get(0).getStops());
                    checkPendingStops(dataResponse.getData().get(0).getStops());
                }
                pickLocationLayout.setVisibility(View.GONE);
                inRide = true;

                if (currentStatus.equalsIgnoreCase("COMPLETED")) {
                    check_current_location = false;
                    showCurrentPlace(true);
                }
            } else {
                if (dataResponse.getData().get(0).getStops() != null && dataResponse.getData().get(0).getStops().size() > 0) {
                    pickLocationLayout.setVisibility(View.GONE);
                    stopsLayout.setVisibility(View.VISIBLE);
                    stopsInfoDisplay(dataResponse.getData().get(0).getStops());
                    checkPendingStops(dataResponse.getData().get(0).getStops());
                }
            }
        } else if (currentStatus.equals("SERVICE")) {
            //      Do nothing
        } else {
            inRide = false;
            currentStatus = "EMPTY";
            changeFlow(currentStatus);
            pickLocationLayout.setVisibility(View.VISIBLE);

            destinationTxt.setVisibility(View.VISIBLE);
            stopsDestinations.setVisibility(View.GONE);
            stopsLayout.setVisibility(View.GONE);
        }


        Log.e("tag", "status in onSuccess out of condition : " + currentStatus);


        getDeviceLocation();

        // added by 92 it solutions
        if ("STARTED".equalsIgnoreCase(currentStatus)) {
            if (!isCPRouteTrigged) {
                resetCheck();
                isCPRouteTrigged = true;
            }
            adjustBounds = false;

            LatLng source = new LatLng(DATUM.getProvider().getLatitude(), DATUM.getProvider().getLongitude());
            LatLng destination = new LatLng(DATUM.getSLatitude(), DATUM.getSLongitude());
            drawRoute(source, destination, "STARTED");
        } else if ("ARRIVED".equalsIgnoreCase(currentStatus)) {

            if (!isPDRouteTrigged) {
                resetCheck();
                isPDRouteTrigged = true;
            }
            adjustBounds = false;

            LatLng origin = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            LatLng destination = new LatLng(DATUM.getDLatitude(), DATUM.getDLongitude());

            drawRoute(origin, destination, "ARRIVED , PICKEDUP");
        } else if ("PICKEDUP".equalsIgnoreCase(currentStatus)) {

            if (!isPDRouteTrigged) {
                resetCheck();
                isPDRouteTrigged = true;
            }
            adjustBounds = false;

            LatLng origin = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            LatLng destination = new LatLng(DATUM.getDLatitude(), DATUM.getDLongitude());

            if (pendingStopsCount() > 0) {
                LatLng stopDestLatLng = new LatLng(currentStopDestLatitude, currentStopDestLongitude);
                drawRoute(origin, stopDestLatLng, "STOP DESTINATION");
            } else {
                drawRoute(origin, destination, "ARRIVED , PICKEDUP");
            }
        } else {
            adjustBounds = true;
        }

        //////


//        if (currentStatus.equals("ARRIVED")
//                || currentStatus.equals("PICKEDUP")
//                || currentStatus.equals("DROPPED"))
//            removeAllMarkerAddDriverMarker(DATUM.getProvider());

//        if (currentStatus.equals("STARTED")) updateDriverNavigation(DATUM.getProvider());

//        if (currentStatus.equals("STARTED")
//                || currentStatus.equals("ARRIVED")
//                || currentStatus.equals("PICKEDUP")
//            /*|| currentStatus.equals("DROPPED")*/)
        //  updateDriverNavigation(DATUM.getProvider().getId());
    }

    private void checkPendingStops(List<Stop> stopArrayList) {
        for (int i = 0; i < stopArrayList.size(); i++) {
            if (i == 0) {
                if (stopArrayList.get(i).getStatus().equals("PENDING")) {
                    currentStopHeader = "Stop 1";
                    currentStopAddress = stopArrayList.get(i).getDAddress();
                    currentStopId = stopArrayList.get(i).getId();
                    currentStopDestLatitude = stopArrayList.get(i).getDLatitude();
                    currentStopDestLongitude = stopArrayList.get(i).getDLongitude();
                    break;
                }
            } else if (i == 1) {
                if (stopArrayList.get(i).getStatus().equals("PENDING")) {
                    currentStopHeader = "Stop 2";
                    currentStopAddress = stopArrayList.get(i).getDAddress();
                    currentStopId = stopArrayList.get(i).getId();
                    currentStopDestLatitude = stopArrayList.get(i).getDLatitude();
                    currentStopDestLongitude = stopArrayList.get(i).getDLongitude();
                    break;
                }
            } else if (i == 2) {
                if (stopArrayList.get(i).getStatus().equals("PENDING")) {
                    currentStopHeader = "Stop 3";
                    currentStopAddress = stopArrayList.get(i).getDAddress();
                    currentStopId = stopArrayList.get(i).getId();
                    currentStopDestLatitude = stopArrayList.get(i).getDLatitude();
                    currentStopDestLongitude = stopArrayList.get(i).getDLongitude();
                    break;
                }
            }
        }

    }

    private int pendingStopsCount() {
        int c = 0;
        for (Stop stop : DATUM.getStops()) {
            if (stop.getStatus().equals("PENDING")) {
                c = c + 1;
            }
        }
        return c;
    }

    private void stopsInfoDisplay(List<Stop> stopArrayList) {
        stop1Layout.setVisibility(View.GONE);
        stop2Layout.setVisibility(View.GONE);
        stop3Layout.setVisibility(View.GONE);

        for (int i = 0; i < stopArrayList.size(); i++) {
            if (i == 0) {
                stop1Layout.setVisibility(View.VISIBLE);
                stop1Address.setText(stopArrayList.get(i).getDAddress());
                if (stopArrayList.get(i).getStatus().equals("PENDING")) {
                    stop1Address.setTextColor(getResources().getColor(R.color.colorPrimaryText));

                } else if (stopArrayList.get(i).getStatus().equals("DROPPED")) {
                    stop1Address.setTextColor(getResources().getColor(R.color.colorSecondaryText));
                }
            } else if (i == 1) {

                stop2Layout.setVisibility(View.VISIBLE);
                stop2Address.setText(stopArrayList.get(i).getDAddress());
                if (stopArrayList.get(i).getStatus().equals("PENDING")) {
                    stop2Address.setTextColor(getResources().getColor(R.color.colorPrimaryText));

                } else if (stopArrayList.get(i).getStatus().equals("DROPPED")) {
                    stop2Address.setTextColor(getResources().getColor(R.color.colorSecondaryText));
                }
            } else if (i == 2) {
                stop3Layout.setVisibility(View.VISIBLE);
                stop3Address.setText(stopArrayList.get(i).getDAddress());
                if (stopArrayList.get(i).getStatus().equals("PENDING")) {
                    stop3Address.setTextColor(getResources().getColor(R.color.colorPrimaryText));

                } else if (stopArrayList.get(i).getStatus().equals("DROPPED")) {
                    stop3Address.setTextColor(getResources().getColor(R.color.colorSecondaryText));
                }
            }
        }

    }


    private void updateDriverNavigation(int provider) {
        if (mProviderLocation == null)
            mProviderLocation = FirebaseDatabase.getInstance().getReference().child("loc_p_" + provider);
        mProviderLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    // LatLngFireBaseDB fireBaseDB = dataSnapshot.getValue(LatLngFireBaseDB.class);
                    HashMap driverLocation = (HashMap) dataSnapshot.getValue();
                    double lat = Double.parseDouble(driverLocation.get("lat").toString());
                    double lng = Double.parseDouble(driverLocation.get("lng").toString());

                    if (lng > 0 && lat > 0) {
                        addCar(new LatLng(lat, lng));
                        // removeAllMarkerAddDriverMarker(new LatLng(lat, lng), DATUM.getProvider());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("RRR ", "Failed to read value.", error.toException());
            }
        });
    }

    public void changeFlow(String status) {

        Log.e("tag", "status is : " + status);

        STATUS = status;
        llPickHomeAdd.setVisibility(View.INVISIBLE);
        llPickWorkAdd.setVisibility(View.INVISIBLE);
        dismissDialog("SEARCHING");
        dismissDialog("INVOICE");
        dismissDialog("RATING");
        System.out.println("From status: " + status);
        RatingDialogFragment ratingDialogFragment = new RatingDialogFragment();
        switch (status) {
            case "EMPTY":

                Log.e("tag", "in empty bloack: ");
                ivBack.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);

                mGoogleMap.clear();
                providersMarker.clear();
//                RIDE_REQUEST.remove("s_address");
//                RIDE_REQUEST.remove("s_latitude");
//                RIDE_REQUEST.remove("s_longitude");
                RIDE_REQUEST.remove("d_address");
                RIDE_REQUEST.remove("d_latitude");
                RIDE_REQUEST.remove("d_longitude");
                RIDE_REQUEST.remove("positions");
                showCurrentPlace(false);
                addDriverMarkers(SharedHelper.getProviders(this));
                destinationTxt.setText(getString(R.string.where_to));
                changeFragment(null);
                if (home != null) llPickHomeAdd.setVisibility(View.VISIBLE);
                else llPickHomeAdd.setVisibility(View.INVISIBLE);
                if (work != null) llPickWorkAdd.setVisibility(View.VISIBLE);
                else llPickWorkAdd.setVisibility(View.INVISIBLE);
                break;
            case "SERVICE":
                // canCallCurrentLocation = false;
                ivBack.setVisibility(View.VISIBLE);
                menu.setVisibility(View.GONE);
                updatePaymentEntities();
                changeFragment(new ServiceFragment());
                break;
            case "SEARCHING":
                updatePaymentEntities();
                SearchingFragment searchingFragment = new SearchingFragment();
                searchingFragment.show(getSupportFragmentManager(), "SEARCHING");
                break;
            case "STARTED":
                ivBack.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
                if (DATUM != null) {
                    initialProcess = true;
                    FirebaseMessaging.getInstance().subscribeToTopic(String.valueOf(DATUM.getId()));
                }
                changeFragment(new ServiceFlowFragment());
                break;
            case "ARRIVED":
                MarkerOptions markerOptions = new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .position(new LatLng(provider.getLatitude(), provider.getLongitude()))
                        .rotation(0.0f)
                        .snippet("" + provider.getId())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2));
                changeFragment(new ServiceFlowFragment());
                break;
            case "PICKEDUP":
                changeFragment(new ServiceFlowFragment());
                break;
            case "DROPPED":
            case "COMPLETED":
                RIDE_REQUEST.remove("positions");
                try {
                    if (DATUM.getPaid() == 1) {
                        currentStatus = "COMPLETED";
                        changeFlow("RATING");
                    } else
                        changeFragment(InvoiceFragment.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "RATING":
                changeFragment(null);
                if (DATUM != null)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(DATUM.getId()));
                ratingDialogFragment.show(getSupportFragmentManager(), "RATING");
                RIDE_REQUEST.clear();
                mGoogleMap.clear();
                check_current_location = false;
                pickLocationLayout.setVisibility(View.VISIBLE);
                destinationTxt.setVisibility(View.VISIBLE);
                stopsDestinations.setVisibility(View.GONE);
                stopsLayout.setVisibility(View.GONE);
                sourceTxt.setText("");
                sourceTxt.setHint(getString(R.string.fetching_current_location));
                destinationTxt.setText(R.string.where_to);
                break;
            default:
                break;
        }
    }

    public void changeFragment(Fragment fragment) {
        if (isFinishing()) return;

        if (fragment != null) {
            if (fragment instanceof BookRideFragment || fragment instanceof ServiceFragment ||
                    fragment instanceof ServiceFlowFragment || fragment instanceof RateCardFragment)
                container.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            else container.setBackgroundColor(getResources().getColor(R.color.white));

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            if (fragment instanceof RateCardFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof ScheduleFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof ServiceFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof BookRideFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());

            try {
                fragmentTransaction.replace(R.id.container, fragment, fragment.getTag());
                fragmentTransaction.commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            bottom_location.setVisibility(View.GONE);
           // stopsLayout.setVisibility(View.VISIBLE);

        } else {
            for (Fragment fragmentd : getSupportFragmentManager().getFragments()) {
                if (fragmentd instanceof ServiceFlowFragment)
                    getSupportFragmentManager().beginTransaction().remove(fragmentd).commitAllowingStateLoss();
                if (fragmentd instanceof InvoiceFragment)
                    getSupportFragmentManager().beginTransaction().remove(fragmentd).commitAllowingStateLoss();
            }
            container.removeAllViews();
            bottom_location.setVisibility(View.VISIBLE);
           // stopsLayout.setVisibility(View.GONE);

            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }

    void dismissDialog(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment instanceof SearchingFragment) {
            SearchingFragment df = (SearchingFragment) fragment;
            df.dismissAllowingStateLoss();
        }
        if (fragment instanceof RatingDialogFragment) {
            RatingDialogFragment df = (RatingDialogFragment) fragment;
            df.dismissAllowingStateLoss();
        }
    }

    void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocation.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        mGoogleMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(new LatLng(
                                        mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()
                                ), DEFAULT_ZOOM));

                        SharedHelper.putKey(activity(), "latitude", String.valueOf(mLastKnownLocation.getLatitude()));
                        SharedHelper.putKey(activity(), "longitude", String.valueOf(mLastKnownLocation.getLongitude()));
                        Location newlocation = new Location("");
                        newlocation.setLatitude(mLastKnownLocation.getLatitude());
                        newlocation.setLongitude(mLastKnownLocation.getLongitude());

                        if (currentStatus.equals("SERVICE")) {
                            perKmUpdateRouteCheck(newlocation);
                        }

                        if (inRide) {
                            check_current_location = false;
                            showCurrentPlace(true);
                        }

                    } else {
                        Log.d("Map", "Current location is null. Using defaults.");
                        mDefaultLocation = new LatLng(
                                Double.valueOf(SharedHelper.getKey(activity(), "latitude", "-33.8523341")),
                                Double.valueOf(SharedHelper.getKey(activity(), "longitude", "151.2106085"))
                        );
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        if (inRide) {
                            check_current_location = false;
                            showCurrentPlace(true);
                        }


                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getLocalizedMessage());
        }
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) mLocationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) return;
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.getUiSettings().setCompassEnabled(false);
                mGoogleMap.setOnCameraMoveListener(this);
                mGoogleMap.setOnCameraIdleListener(this);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    updateLocationUI();
                    getDeviceLocation();
                    showCurrentPlace(false);
                }
        }
    }

    public void drawRoute(LatLng source, LatLng destination, String from) {

        Log.e("tag", "drawRoute : " + SharedHelper.getKey(activity(), "map_key") + "from is :" + from);

        if (updateRoute) {
            GoogleDirection
                    .withServerKey(SharedHelper.getKey(activity(), "map_key"))
                    .from(source)
                    .to(destination)
                    .transportMode(TransportMode.DRIVING)
                    .execute(this);
        }
    }

    public void drawRoute(LatLng source, ArrayList<LatLng> destinations, String from) {

        LatLng stop1 = null, stop2 = null, destination = null;

        List<LatLng> waypoints = null;

        if (destinations.size() == 1) {
            destination = new LatLng(destinations.get(0).latitude, destinations.get(0).longitude);
        }

        if (destinations.size() == 2) {
            stop1 = new LatLng(destinations.get(0).latitude, destinations.get(0).longitude);
            destination = new LatLng(destinations.get(1).latitude, destinations.get(1).longitude);
            waypoints = Arrays.asList(stop1);
        }

        if (destinations.size() == 3) {
            stop1 = new LatLng(destinations.get(0).latitude, destinations.get(0).longitude);
            stop2 = new LatLng(destinations.get(1).latitude, destinations.get(1).longitude);
            destination = new LatLng(destinations.get(2).latitude, destinations.get(2).longitude);
            waypoints = Arrays.asList(stop1, stop2);

        }

        Log.e("tag", "drawRoute : " + SharedHelper.getKey(activity(), "map_key") + "from is :" + from);

        if (destinations.size() == 1) {
            if (updateRoute) {
                GoogleDirection
                        .withServerKey(SharedHelper.getKey(activity(), "map_key"))
                        .from(source)
                        .to(destination)
                        .transportMode(TransportMode.DRIVING)
                        .execute(this);
            }

        } else {
            if (updateRoute) {
                GoogleDirection
                        .withServerKey(SharedHelper.getKey(activity(), "map_key"))
                        .from(source)
                        .and(waypoints)
                        .to(destination)
                        .transportMode(TransportMode.DRIVING)
                        .execute(this);
            }

        }


    }

    private Bitmap getMarkerBitmapFromView() {

        //HERE YOU CAN ADD YOUR CUSTOM VIEW
        View mView = ((LayoutInflater) this.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                inflate(R.layout.map_custom_infowindow, null);

        //IN THIS EXAMPLE WE ARE TAKING TEXTVIEW BUT YOU CAN ALSO TAKE ANY KIND OF VIEW LIKE IMAGEVIEW, BUTTON ETC.
        TextView tvEtaVal = mView.findViewById(R.id.tvEstimatedFare);
        String arrivalTime = destinationLeg.getArrival_time();
        if (arrivalTime.contains("hours")) arrivalTime = arrivalTime.replace("hours", "h\n");
        else if (arrivalTime.contains("hour")) arrivalTime = arrivalTime.replace("hour", "h\n");
        if (arrivalTime.contains("mins")) arrivalTime = arrivalTime.replace("mins", "min");
        tvEtaVal.setText(arrivalTime);
        mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());
        mView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(mView.getMeasuredWidth(),
                mView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = mView.getBackground();
        if (drawable != null) drawable.draw(canvas);
        mView.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            updateRoute = false;
            initialProcess = true;
            mGoogleMap.clear();
            Route route = direction.getRouteList().get(0);
            if (!route.getLegList().isEmpty()) {
                int legCount = route.getLegList().size();
                for (int index = 0; index < legCount; index++) {
                    Leg leg = route.getLegList().get(index);
                    InfoWindowData originLeg = new InfoWindowData();
                    originLeg.setAddress(leg.getStartAddress());
                    originLeg.setArrival_time(null);
                    originLeg.setDistance(leg.getDistance().getText());

                    destinationLeg = new InfoWindowData();
                    destinationLeg.setAddress(leg.getEndAddress());
                    destinationLeg.setArrival_time(leg.getDuration().getText());
                    destinationLeg.setDistance(leg.getDistance().getText());

                    LatLng origin = new LatLng(leg.getStartLocation().getLatitude(), leg.getStartLocation().getLongitude());
                    LatLng destination = new LatLng(leg.getEndLocation().getLatitude(), leg.getEndLocation().getLongitude());
                    if (currentStatus.equals("SERVICE")) {
                        mGoogleMap.addMarker(new MarkerOptions()
                                .position(origin)
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView())));
                        if (specificProviders != null)
                            for (Provider provider : specificProviders) {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .anchor(0.5f, 0.5f)
                                        .position(new LatLng(provider.getLatitude(), provider.getLongitude()))
                                        .rotation(0.0f)
                                        .snippet("" + provider.getId())
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2));
                                providersMarker.put(provider.getId(), mGoogleMap.addMarker(markerOptions));
                            }
                    } else {
                        Marker mark = mGoogleMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.src_icon))
                                .position(origin));
                        mark.setTag(originLeg);
                    }
                    mGoogleMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.des_icon))
                            .position(destination))
                            .setTag(destinationLeg);

                    List<Step> stepList = leg.getStepList();
                    ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(this, stepList, 3, getResources().getColor(R.color.colorAccent), 3, getResources().getColor(R.color.colorAccent));
                    for (PolylineOptions polylineOption : polylineOptionList) {
                        mGoogleMap.addPolyline(polylineOption);
                    }
                    if (adjustBounds) setCameraWithCoordinationBounds(route);
                }


            }


//            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
//            mGoogleMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 3, getResources().getColor(R.color.colorAccent)));
//            if (adjustBounds) setCameraWithCoordinationBounds(route);


        } else {
            System.out.println("RRR onDirectionFailure = [" + rawBody);
            changeFlow("EMPTY");
            Toast.makeText(this, getString(R.string.root_not_available), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        System.out.println("RRR onDirectionFailure = [" + t.getMessage() + "]");
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        try {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));
        } catch (Exception e) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 90));
        }
    }

    public void addCar(LatLng latLng) {
        final String[] eta = {""};

        if (isFinishing()) return;

        if (latLng != null && latLng.latitude > 0 && latLng.longitude > 0) {
            LatLng oldPosition = null;
            if (newPosition != null)
                oldPosition = newPosition;
            newPosition = latLng;
            if (marker == null) {
                marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng)
                        .anchor(0.5f, 0.75f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2)));
            } else {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2));
            }
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2));
            marker.setPosition(newPosition);
            animateMarker(oldPosition, newPosition, marker);
            marker.setRotation(bearingBetweenLocations(oldPosition, newPosition));

            if (marker != null && !TextUtils.isEmpty(eta[0])) {
                marker.setTitle("ETA");
                marker.setSnippet(eta[0]);
                marker.showInfoWindow();
            } else marker.hideInfoWindow();
        }
    }

    @Override
    public void onSuccess(@NonNull User user) {
        String dd = LocaleHelper.getLanguage(this);
        String userLanguage = (user.getLanguage() == null) ? Constants.Language.ENGLISH : user.getLanguage();
        if (!userLanguage.equalsIgnoreCase(dd)) {
            LocaleHelper.setLocale(getApplicationContext(), user.getLanguage());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        if (user.getCorpDeleted() == 1)
            SharedHelper.putKey(this, "corporate_user", "1");
        else
            SharedHelper.putKey(this, "corporate_user", "0");
        SharedHelper.putKey(this, "first_name", user.getFirstName());
        SharedHelper.putKey(this, "last_name", user.getLastName());
        SharedHelper.putKey(this, "email", user.getEmail());
        SharedHelper.putKey(this, "lang", user.getLanguage());
        SharedHelper.putKey(this, "corporate_otp", user.getCorporatePin() + "");
        SharedHelper.putKey(this, "stripe_publishable_key", user.getStripePublishableKey());
        SharedHelper.putKey(this, "currency", user.getCurrency());
        SharedHelper.putKey(this, "measurementType", user.getMeasurement());
        SharedHelper.putKey(this, "walletBalance", String.valueOf(user.getWalletBalance()));
        SharedHelper.putKey(this, "userInfo", printJSON(user));
        name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        sub_name.setText(user.getEmail());
        SharedHelper.putKey(activity(), "picture", user.getPicture());
        Glide.with(activity())
                .load(BuildConfig.BASE_IMAGE_URL + user.getPicture())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder)
                        .dontAnimate()
                        .error(R.drawable.ic_user_placeholder))
                .into(picture);
    }

//    private void removeAllMarkerAddDriverMarker(LatLng latLng, Provider provider) {
//        if (providersMarker.size() == 1) {
//            Marker marker = providersMarker.get(provider.getId());
//            LatLng startPosition = marker.getPosition();
//            marker.setPosition(latLng);
//        } else {
//            providersMarker.clear();
//            MarkerOptions markerOptions = new MarkerOptions()
//                    .anchor(0.5f, 0.5f)
//                    .position(latLng)
//                    .rotation(0.0f)
//                    .snippet("" + provider.getId())
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2));
//            providersMarker.put(provider.getId(), mGoogleMap.addMarker(markerOptions));
//        }
//    }

    @Override
    public void onSuccessLogout(Object object) {
        Utilities.LogoutApp(activity());
    }

    @Override
    public void onSuccess(AddressResponse response) {
        home = (response.getHome().isEmpty()) ? null : response.getHome().get(response.getHome().size() - 1);
        work = (response.getWork().isEmpty()) ? null : response.getWork().get(response.getWork().size() - 1);
        if (currentStatus.equalsIgnoreCase("EMPTY")) {
            if (home != null) llPickHomeAdd.setVisibility(View.VISIBLE);
            else llPickHomeAdd.setVisibility(View.INVISIBLE);
            if (work != null) llPickWorkAdd.setVisibility(View.VISIBLE);
            else llPickWorkAdd.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSuccess(List<Provider> providerList) {
//        System.out.println("RRR providerList = " + printJSON(providerList));
        SharedHelper.putProviders(this, printJSON(providerList));
        if (providerList != null)
            addDriverMarkers(providerList);
    }

    @Override
    public void onSuccess(InitSettingsResponse initSettingsResponse) {

        SharedHelper.putKey(activity(), "map_key", initSettingsResponse.getMapKey());
        // Log.e("tag", "drawRoute: " + SharedHelper.getKey(activity(), "map_key"));


    }

    private void addDriverMarkers(List<Provider> providers) {
        if (providers != null) {
            for (Provider provider : providers)
                if (providersMarker.containsKey(provider.getId())) {
                    Marker marker = providersMarker.get(provider.getId());
                    LatLng startPosition = marker.getPosition();
                    LatLng newPos = new LatLng(provider.getLatitude(), provider.getLongitude());
                    marker.setPosition(newPos);
                    animateMarker(startPosition, newPos, marker);
//                    marker.setRotation(bearingBetweenLocations(startPosition, newPos));
                } else {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .anchor(0.5f, 0.5f)
                            .position(new LatLng(provider.getLatitude(), provider.getLongitude()))
                            .rotation(0.0f)
                            .snippet("" + provider.getId())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2));
                    providersMarker.put(provider.getId(), mGoogleMap.addMarker(markerOptions));
                }
        }
    }

    private List<Provider> specificProviders;

    public void setSpecificProviders(List<Provider> specificProviders) {
        this.specificProviders = specificProviders;
//        LatLng origin = new LatLng((Double) RIDE_REQUEST.get("s_latitude"), (Double) RIDE_REQUEST.get("s_longitude"));
//        LatLng destination = new LatLng((Double) RIDE_REQUEST.get("d_latitude"), (Double) RIDE_REQUEST.get("d_longitude"));
//        resetCheck();
//        drawRoute(origin, destination  , "setSpecificProviders");
    }

    @Override
    public void onError(Throwable e) {
        //      By Rajaganapathi
       // handleError(e);
    }

    @Override
    public void onCheckStatusError(Throwable e) {
        Log.d("Error", "My Error" + e.getLocalizedMessage());

        if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();
            Log.e("onError", response.code() + "");
        }
    }

    //  private boolean canCallCurrentLocation = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_LOCATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    // canCallCurrentLocation = data.getBooleanExtra("canCallCurrentLocation", false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (RIDE_REQUEST.containsKey("s_address"))
                    sourceTxt.setText(String.valueOf(RIDE_REQUEST.get("s_address")));
                else sourceTxt.setText("");
                if (RIDE_REQUEST.containsKey("positions")) {
                    Gson gson = new Gson();
                    String jsonText = data.getStringExtra("positions");
                    Type type = new TypeToken<ArrayList<AddedStop>>() {
                    }.getType();
                    ArrayList<AddedStop> stopsArrayList = gson.fromJson(jsonText, type);
                    destinationTxt.setText(String.valueOf(stopsArrayList.get(0).getD_address()));
                    stop1DAddress.setText(stopsArrayList.get(0).getD_address());
                }
                if (RIDE_REQUEST.containsKey("d_address"))
                    destinationTxt.setText(String.valueOf(RIDE_REQUEST.get("d_address")));
                else destinationTxt.setText(R.string.where_to);


                if ( RIDE_REQUEST.containsKey("positions")) {
                    LatLng origin = new LatLng((Double) RIDE_REQUEST.get("s_latitude"), (Double) RIDE_REQUEST.get("s_longitude"));
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<AddedStop>>() {
                    }.getType();

                    stopsDestinations.setVisibility(View.VISIBLE);
                    destinationTxt.setVisibility(View.GONE);
                    String jsonText = data.getStringExtra("positions");
                    ArrayList<AddedStop> stopArrayList = gson.fromJson(jsonText, type);

                    stop1DLayout.setVisibility(View.GONE);
                    stop2DLayout.setVisibility(View.GONE);
                    stop3DLayout.setVisibility(View.GONE);

                    for (int i = 0; i < stopArrayList.size(); i++) {
                        if (i == 0) {
                            stop1DLayout.setVisibility(View.VISIBLE);
                            stop1DAddress.setText(stopArrayList.get(i).getD_address());
                        } else if (i == 1) {

                            stop2DLayout.setVisibility(View.VISIBLE);
                            stop2DAddress.setText(stopArrayList.get(i).getD_address());

                        } else if (i == 2) {
                            stop3DLayout.setVisibility(View.VISIBLE);
                            stop3DAddress.setText(stopArrayList.get(i).getD_address());

                        }
                    }

                    ArrayList<LatLng> destinationStops = new ArrayList<>();
                    for (AddedStop stop : stopArrayList) {
                        destinationStops.add(new LatLng(stop.getD_latitude(), stop.getD_longitude()));
                    }
                    resetCheck();
                    drawRoute(origin, destinationStops, "onActivityResult");
                    currentStatus = "SERVICE";
                    changeFlow(currentStatus);

                }


                if (RIDE_REQUEST.containsKey("s_address") && RIDE_REQUEST.containsKey("d_address")) {
                    LatLng origin = new LatLng((Double) RIDE_REQUEST.get("s_latitude"), (Double) RIDE_REQUEST.get("s_longitude"));
                    LatLng destination = new LatLng((Double) RIDE_REQUEST.get("d_latitude"), (Double) RIDE_REQUEST.get("d_longitude"));
                    resetCheck();
                    drawRoute(origin, destination, "onActivityResult");
                    currentStatus = "SERVICE";
                    changeFlow(currentStatus);
                }
                //      TODO: By Rajaganapathi..
                /*else changeFlow("EMPTY");*/
            }
        }
    }

    //      TODO: Payment Gateway

//    @Override
//    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
//        String nonce = paymentMethodNonce.getNonce();
//        Log.d("PayPal", "onPaymentMethodNonceCreated " + nonce);
//        if (paymentMethodNonce instanceof PayPalAccountNonce) {
//            PayPalAccountNonce payPalAccountNonce = (PayPalAccountNonce) paymentMethodNonce;
//            String email = payPalAccountNonce.getEmail();
//        }
//    };

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (inRide) {
                check_current_location = false;
                showCurrentPlace(true);
            }


            String s = location.getLatitude() + "\n" + location.getLongitude()
                    + "\n\nMy Current City is: "
                    + cityName;

//            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            Log.d("Location", s);
        }
    }

    private void resetCheck() {
        updateRoute = true;
        shortDistance = 0;
    }

    private void perKmUpdateRouteCheck(Location newlocation) {
        if (currLocation != null) {
            shortDistance += currLocation.distanceTo(newlocation);
            if ((float) (shortDistance / 1000f) >= 1) {
                resetCheck();
            }
            Log.d("Distance_Covered", shortDistance + "");
        }
        currLocation = newlocation;
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace(boolean gps) {
        if (mGoogleMap == null) return;

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;

            if (mLastKnownLocation != null) {
                if (TextUtils.isEmpty(sourceTxt.getText()) || sourceTxt.getText().toString().equals(getResources().getString(R.string.pickup_location)) || gps)
                    mLocation = getLastKnownLocation();
                if (mLocation != null) {
                    Address address = getAddress(mLocation.getLatitude(), mLocation.getLongitude());

                    if (address != null) {
                        if (check_current_location == false) {
                            String streetAddress = getStreetAddress(address);
                            sourceTxt.setText(streetAddress);
                            RIDE_REQUEST.put("s_address", streetAddress);
                            RIDE_REQUEST.put("s_latitude", mLastKnownLocation.getLatitude());
                            RIDE_REQUEST.put("s_longitude", mLastKnownLocation.getLongitude());
                            check_current_location = true;
                        }
                    } else {
                        Toasty.info(this, "Unable to find address.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            try {
                hideLoading();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        } else getLocationPermission();
    }

    public void updatePaymentEntities() {
        if (checkStatusResponse != null) {
            isCash = checkStatusResponse.getCash() == 1;
            isCard = checkStatusResponse.getCard() == 1;
            SharedHelper.putKey(this, "currency", checkStatusResponse.getCurrency());
            if (isCash) RIDE_REQUEST.put("payment_mode", Utilities.PaymentMode.cash);
            else if (isCard) RIDE_REQUEST.put("payment_mode", Utilities.PaymentMode.card);
        }
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) continue;
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy())
                    bestLocation = l;
            }
        if (bestLocation == null) return null;
        return bestLocation;
    }

    protected String getStreetAddress(Address address) {
        ArrayList<String> addressFragments = new ArrayList<>();
        // Fetch the address lines using getAddressLine,
        // join them, and send them to the thread.
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressFragments.add(address.getAddressLine(i));
        }
        String streetAddress = TextUtils.join(", ", addressFragments);
        Log.d("Base", streetAddress);
        return streetAddress;
    }


}
