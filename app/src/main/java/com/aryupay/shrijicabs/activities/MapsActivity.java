package com.aryupay.shrijicabs.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.aryupay.shrijicabs.R;
import com.aryupay.shrijicabs.adapters.ShrijicabsUserSessionManager;
import com.aryupay.shrijicabs.interfaces.ApiClient;
import com.aryupay.shrijicabs.interfaces.ApiInterface;
import com.aryupay.shrijicabs.login.LoginActivity;
import com.aryupay.shrijicabs.network.Apis;
import com.aryupay.shrijicabs.network.ShrijiCabsSingleton;
import com.aryupay.shrijicabs.utils.DataParser;
import com.aryupay.shrijicabs.utils.Keys;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {

    int intValue = 0;
    RadioGroup radioGroupAcNonac;
    Handler handler = new Handler();
    private GoogleMap mMap;
    String pickupLocationLatLng, dropLocationLatLng;
    DrawerLayout drawer;
    EditText vehicleCategories;
    Marker mCurrLocationMarker;
    ValueAnimator vAnimator;
    ShimmerFrameLayout shimmerFrameLayout;
    RelativeLayout relativeLayout;
    SupportMapFragment mapFragment;
    ImageView navigationDrawerBtn, userProfileImage, pinLocation;
    TextView dailySafarTv, rental, tourTrip;
    GoogleApiClient mGoogleApiClient;
    ShrijicabsUserSessionManager sessionManager;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    FloatingActionButton floatingActionButton;
    boolean showingFirst = true;
    AppBarLayout appBarLayout;
    RelativeLayout confirmAdressOrBookNow_Btn, BookNow_BtnRental, BookNow_BtnTour;
    int position;
    List<String> categoryVehicleList, categoryVehicleListId;
    List<String> categoryVehicleDetailList, categoryVehicleDetailListId;
    EditText pickupFrom, dropTo, editTextPickupLocation, editText_dropLocation;
    FrameLayout frameLayout;
    ApiInterface apiInterface;
    HashMap<String, String> user;
    RadioButton radioOneWay, radioRoundWay, radioAc, radioNonAc;
    TextView selectCategory;
    LinearLayout searchingId;
    ProgressBar progressBarHorizontal;
    SharedPreferences preferences;
    Double pickupLocationLatitude, pickupLocationLongitude, dropLocationLatitude, dropoLocationLongitude;
    String booking_status_id;
    TextView confirmAdressOrBookNowTv, referAndEarn, settings, insurance, notifications, freeRides, ridesHistory, bookings, help;
    LinearLayout dailySafarLinearLayout, rentalLinearLayout, tourTripLinearLayout;

    // Rental

    Switch AcNonSwitch;
    TextView tvSelectCat, tvSelectCars, tvSeater, tvACMode;
    EditText etCarsCat, etPaymentMethod;

    // Tour

    Switch tourAcNonSwitch;
    TextView tourCategory, tourCar, tourSeater, tourACMode;
    EditText tourCarCategory, tourPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        categoryVehicleList = new ArrayList<>();
        categoryVehicleDetailList = new ArrayList<>();
        categoryVehicleListId = new ArrayList<>();
        categoryVehicleDetailListId = new ArrayList<>();
        pinLocation = findViewById(R.id.pin_location);
        radioGroupAcNonac = findViewById(R.id.radio_group_Ac_nonac);
        pickupFrom = findViewById(R.id.pickupFrom);
        dropTo = findViewById(R.id.dropLocation);
        appBarLayout = findViewById(R.id.app_bar);
        radioAc = findViewById(R.id.radio_ac);
        radioNonAc = findViewById(R.id.radio_non_ac);
        drawer = findViewById(R.id.drawer_layout);
        vehicleCategories = findViewById(R.id.vehicleCategories);
        editTextPickupLocation = findViewById(R.id.editText_pickupLocation);
        editText_dropLocation = findViewById(R.id.editText_dropLocation);
        NavigationView navigationView = findViewById(R.id.nav_view);
        help = navigationView.getHeaderView(0).findViewById(R.id.help);
        bookings = navigationView.getHeaderView(0).findViewById(R.id.bookings);
        notifications = navigationView.getHeaderView(0).findViewById(R.id.notifications);
        freeRides = navigationView.getHeaderView(0).findViewById(R.id.freeRides);
        insurance = navigationView.getHeaderView(0).findViewById(R.id.insurance);
        settings = navigationView.getHeaderView(0).findViewById(R.id.settings);
        ridesHistory = navigationView.getHeaderView(0).findViewById(R.id.ridesHistory);
        selectCategory = findViewById(R.id.categoryTextView);
        userProfileImage = navigationView.getHeaderView(0).findViewById(R.id.userProfileImage);
        searchingId = findViewById(R.id.searchingId);
        radioOneWay = findViewById(R.id.radio_one_way);
        radioRoundWay = findViewById(R.id.radio_round_way);
        shimmerFrameLayout = findViewById(R.id.placeHolderShimmer);

        // rental
        AcNonSwitch = findViewById(R.id.AcNonSwitch);
        tvSelectCat = findViewById(R.id.tvSelectCat);
        tvSelectCars = findViewById(R.id.tvSelectCars);
        tvSeater = findViewById(R.id.tvSeater);
        etCarsCat = findViewById(R.id.etCarsCat);
        etPaymentMethod = findViewById(R.id.etPaymentMethod);
        tvACMode = findViewById(R.id.tvACMode);
        preferences = this.getSharedPreferences("user_info", MODE_PRIVATE);

// Tour
        tourAcNonSwitch = findViewById(R.id.tourAcNonSwitch);
        tourCar = findViewById(R.id.tourCar);
        tourCategory = findViewById(R.id.tourCategory);
        tourSeater = findViewById(R.id.tourSeater);
        tourCarCategory = findViewById(R.id.tourCarCategory);
        tourPayment = findViewById(R.id.tourPayment);
        tourACMode = findViewById(R.id.tourACMode);

        booking_status_id = preferences.getString("booking_status_id", "");

        if (booking_status_id != null) {
            if (booking_status_id.matches("4")) {
//                Intent intent = new Intent(MapsActivity.this, SourceDestinationOnMapActivity.class);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
////                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//
            } else if (booking_status_id.matches("5")) {
//                Intent intent = new Intent(MapsActivity.this, PaymentActivity.class);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
////                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//
            }
        }
        //

        getVehicleInfoFromTheServer();
        getVehicalDetails();
        vehicleCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAllVehicleInfo();
            }
        });
        bookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MapsActivity.this, YourBookingsActivity.class));
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MapsActivity.this, HelpActivity.class));
            }
        });
        ridesHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MapsActivity.this, RidesHistory.class));
            }
        });
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                Intent intent = new Intent(MapsActivity.this, NotificationsActivity.class);
                startActivity(intent);
            }
        });
        freeRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                Intent intent = new Intent(MapsActivity.this, FreeRidesActivity.class);
                startActivity(intent);
            }
        });
        insurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                Intent intent = new Intent(MapsActivity.this, InsuranceActivity.class);
                startActivity(intent);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                Intent intent = new Intent(MapsActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        sessionManager = new ShrijicabsUserSessionManager(MapsActivity.this);
        if (sessionManager.checKLogin()) {
            finish();
            return;
        }

        user = sessionManager.getUserDetails();


        tourTripLinearLayout = findViewById(R.id.tour_trip_linear_layout);
        dailySafarLinearLayout = findViewById(R.id.dailySafarLinearLayout);
        rentalLinearLayout = findViewById(R.id.rental_linear_layout);
        confirmAdressOrBookNowTv = findViewById(R.id.confirmAdressOrBookNow_tv);
        relativeLayout = findViewById(R.id.relative_layout_content);
        progressBarHorizontal = findViewById(R.id.progressBar_horizontal);
        confirmAdressOrBookNow_Btn = findViewById(R.id.confirmAdressOrBookNow_Btn);
        BookNow_BtnRental = findViewById(R.id.BookNow_BtnRental);
        BookNow_BtnTour = findViewById(R.id.BookNow_BtnTour);

        frameLayout = findViewById(R.id.frame_layout);
        dailySafarTv = findViewById(R.id.daily_safar_tv);
        rental = findViewById(R.id.rental);
        tourTrip = findViewById(R.id.tour_trip);

        referAndEarn = navigationView.getHeaderView(0).findViewById(R.id.referAndEarn);
        referAndEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                Intent intent = new Intent(MapsActivity.this, ReferAndEarnActivity.class);
                startActivity(intent);
            }
        });
        confirmAdressOrBookNow_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmAdressOrBookNowTv.getText().toString().trim().equals("Ok")) {
                    appBarLayout.setVisibility(View.GONE);
                    floatingActionButton.setVisibility(View.GONE);
                    editTextPickupLocation.setVisibility(View.GONE);
                    editText_dropLocation.setVisibility(View.VISIBLE);
                    confirmAdressOrBookNowTv.setText(R.string.confirmAdress);
                    pickupFrom.setText(editTextPickupLocation.getText().toString().trim());

                    drawRouteBetweenTwoLatlng();
                } else if (confirmAdressOrBookNowTv.getText().toString().trim().equals("Confirm Address")) {
                    appBarLayout.setVisibility(View.VISIBLE);
                    floatingActionButton.setVisibility(View.VISIBLE);
                    editTextPickupLocation.setVisibility(View.GONE);
                    editText_dropLocation.setVisibility(View.GONE);
                    dropTo.setText(editText_dropLocation.getText().toString().trim());
                    pickupFrom.setText(editTextPickupLocation.getText().toString().trim());
                    pinLocation.setVisibility(View.GONE);
                    confirmAdressOrBookNowTv.setText(R.string.bookNow);
//                    if(!editText_dropLocation.getText().toString().trim().equals("") && !editTextPickupLocation.getText().toString().trim().equals("")){
//                        totalAmount.setVisibility(View.VISIBLE);
//                    }
                    drawRouteBetweenTwoLatlng();
                } else {
                    if (pickupFrom.getText().toString().equals("")) {
                        Snackbar.make(findViewById(android.R.id.content), "Please Enter Pickup Location", Snackbar.LENGTH_LONG).show();
                    } else if (dropTo.getText().toString().trim().equals("")) {
                        Snackbar.make(findViewById(android.R.id.content), "Please Enter Drop Location", Snackbar.LENGTH_LONG).show();
                    } else if (vehicleCategories.getText().toString().trim().equals("")) {
                        Snackbar.make(findViewById(android.R.id.content), "Please Enter Vehicle Category", Snackbar.LENGTH_LONG).show();
                    } else {
                        Date d = new Date();
                        CharSequence s = DateFormat.format("yyyy-MM-dd HH:mm:ss", d.getTime());
                        sendClientDetailsToVendor((String) s);
                        //payment  gateway
//                    startActivity(new Intent(MapsActivity.this,PaymentActivity.class));
                    }
                }
            }
        });
        pickupFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dropTo.getText().toString().trim().equals("")) {
                    appBarLayout.setVisibility(View.GONE);
                    floatingActionButton.setVisibility(View.GONE);
                    editTextPickupLocation.setVisibility(View.VISIBLE);
                    editText_dropLocation.setVisibility(View.GONE);
                    pinLocation.setVisibility(View.VISIBLE);
                    confirmAdressOrBookNowTv.setText(R.string.confirmAdress);
                } else {
                    appBarLayout.setVisibility(View.GONE);
                    floatingActionButton.setVisibility(View.GONE);
                    editTextPickupLocation.setVisibility(View.VISIBLE);
                    editText_dropLocation.setVisibility(View.GONE);
                    pinLocation.setVisibility(View.VISIBLE);
                    confirmAdressOrBookNowTv.setText(R.string.ok);
                }
            }
        });
        dropTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appBarLayout.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.GONE);
                editTextPickupLocation.setVisibility(View.GONE);
                editText_dropLocation.setVisibility(View.VISIBLE);
                pinLocation.setVisibility(View.VISIBLE);
                confirmAdressOrBookNowTv.setText(R.string.confirmAdress);

            }
        });

        TextView profileView = navigationView.getHeaderView(0).findViewById(R.id.profileView);
        profileView.setText(user.get(Keys.KEY_NAME));
        if (user.get(Keys.KEY_PROFILE_IMAGE).equals("")) {
            getProfileImageFromServer();
        } else {
            Picasso.get().load(user.get(Keys.KEY_PROFILE_IMAGE)).into(userProfileImage);
        }

        TextView logout = navigationView.getHeaderView(0).findViewById(R.id.logout);
        TextView aboutUs = navigationView.getHeaderView(0).findViewById(R.id.about);
        TextView privacyPolicy = navigationView.getHeaderView(0).findViewById(R.id.privacyPolicy);
        floatingActionButton = findViewById(R.id.floating_action);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showingFirst == true) {
                    showingFirst = false;
                    floatingActionButton.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    frameLayout.setVisibility(View.GONE);
                } else {
                    floatingActionButton.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    showingFirst = true;
                    frameLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MapsActivity.this, AboutUsActivity.class));
                overridePendingTransition(R.anim.enter_layout, R.anim.exit_layout);
            }
        });
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MapsActivity.this, PrivacyPolicyActivity.class));
                overridePendingTransition(R.anim.enter_layout, R.anim.exit_layout);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.logOutUser();
                Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.exit, R.anim.enter);
            }
        });
        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.enter_layout, R.anim.exit_layout);
            }
        });
        Thread thread = new Thread() {
            @Override
            public void run() {
                dailySafarTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dailySafarLinearLayout.setVisibility(View.VISIBLE);
                        rentalLinearLayout.setVisibility(View.GONE);
                        tourTripLinearLayout.setVisibility(View.GONE);

                        confirmAdressOrBookNow_Btn.setVisibility(View.VISIBLE);
                        BookNow_BtnRental.setVisibility(View.GONE);
                        BookNow_BtnTour.setVisibility(View.GONE);
                    }
                });
                rental.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dailySafarLinearLayout.setVisibility(View.GONE);


                        rentalLinearLayout.setVisibility(View.VISIBLE);
                        tourTripLinearLayout.setVisibility(View.GONE);

                        BookNow_BtnRental.setVisibility(View.VISIBLE);
                        BookNow_BtnTour.setVisibility(View.GONE);
                        confirmAdressOrBookNow_Btn.setVisibility(View.GONE);
                    }
                });

                tourTrip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dailySafarLinearLayout.setVisibility(View.GONE);
                        rentalLinearLayout.setVisibility(View.GONE);
                        tourTripLinearLayout.setVisibility(View.VISIBLE);

                        confirmAdressOrBookNow_Btn.setVisibility(View.GONE);
                        BookNow_BtnRental.setVisibility(View.GONE);
                        BookNow_BtnTour.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
        thread.start();


        BookNow_BtnRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookRentalCar();

            }
        });
        BookNow_BtnTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookTourCar();
            }
        });
        navigationDrawerBtn = findViewById(R.id.nav_drawer_btn);
        navigationDrawerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        drawer.setScrimColor(Color.TRANSPARENT);
        //to see both navigation bar and profile activity both in one screen
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                relativeLayout.setTranslationX(slideX);
            }
        };

        drawer.addDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(MapsActivity.this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        tvSelectCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog builder = new AlertDialog.Builder(MapsActivity.this).create();
                builder.setTitle("Select Category");
                ListView modeList = new ListView(MapsActivity.this);
                final String[] stringArray = new String[]{"1 hr 20 km", "2 hr 30 km", "4 hr 60 km", "6 hr 80 km", "8 hr 90 km", "10 hr 100 km", "12 hr 120 km"};
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
                modeList.setAdapter(modeAdapter);

                modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String val = (String) parent.getItemAtPosition(position);
                        System.out.println("Valueis " + val);
                        tvSelectCat.setText(val);
                        builder.dismiss();
                    }

                });

                builder.setView(modeList);

                builder.show();
            }
        });

        tvSelectCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllVehicleDetails();

            }
        });

        tvSeater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog builder = new AlertDialog.Builder(MapsActivity.this).create();
                builder.setTitle("Select Seat");

                ListView modeList = new ListView(MapsActivity.this);
                final String[] stringArray = new String[]{"4", "5", "6", "7", "8", "9", "10", "11"};
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
                modeList.setAdapter(modeAdapter);

                modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String val = (String) parent.getItemAtPosition(position);
                        System.out.println("Valueis " + val);
                        tvSeater.setText(val);
                        builder.dismiss();
                    }
                });

                builder.setView(modeList);
                builder.show();

            }
        });

        etCarsCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog builder = new AlertDialog.Builder(MapsActivity.this).create();
                builder.setTitle("Select Car Category");

                ListView modeList = new ListView(MapsActivity.this);
                final String[] stringArray = new String[]{"Bolero", "Tufaan", "Scarpio", "Tavera"};
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
                modeList.setAdapter(modeAdapter);

                modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String val = (String) parent.getItemAtPosition(position);
                        System.out.println("Valueis " + val);
                        etCarsCat.setText(val);
                        builder.dismiss();
                    }
                });

                builder.setView(modeList);

                builder.show();
            }
        });

        etPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog builder = new AlertDialog.Builder(MapsActivity.this).create();
                builder.setTitle("Select Payment Mode");

                ListView modeList = new ListView(MapsActivity.this);
                final String[] stringArray = new String[]{"Cash", "Wallet", "Debit/Credit"};
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
                modeList.setAdapter(modeAdapter);

                modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String val = (String) parent.getItemAtPosition(position);
                        System.out.println("Valueis " + val);
                        etPaymentMethod.setText(val);
                        builder.dismiss();
                    }
                });

                builder.setView(modeList);
                builder.show();
            }
        });

        AcNonSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tvACMode.setText("AC");
                } else {
                    tvACMode.setText("non-ac");
                }
            }
        });

        //Tour

        tourCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog builder = new AlertDialog.Builder(MapsActivity.this).create();
                builder.setTitle("Select Category");
                ListView modeList = new ListView(MapsActivity.this);
                final String[] stringArray = new String[]{"1 hr 20 km", "2 hr 30 km", "4 hr 60 km", "6 hr 80 km", "8 hr 90 km", "10 hr 100 km", "12 hr 120 km"};
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
                modeList.setAdapter(modeAdapter);

                modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String val = (String) parent.getItemAtPosition(position);
                        System.out.println("Valueis " + val);
                        tourCategory.setText(val);
                        builder.dismiss();
                    }

                });

                builder.setView(modeList);

                builder.show();
            }
        });

        tourCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllVehicleDetailsTour();

            }
        });

        tourSeater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog builder = new AlertDialog.Builder(MapsActivity.this).create();
                builder.setTitle("Select Seat");

                ListView modeList = new ListView(MapsActivity.this);
                final String[] stringArray = new String[]{"4", "5", "6", "7", "8", "9", "10", "11"};
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
                modeList.setAdapter(modeAdapter);

                modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String val = (String) parent.getItemAtPosition(position);
                        System.out.println("Valueis " + val);
                        tourSeater.setText(val);
                        builder.dismiss();
                    }
                });

                builder.setView(modeList);
                builder.show();

            }
        });

        tourCarCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog builder = new AlertDialog.Builder(MapsActivity.this).create();
                builder.setTitle("Select Car Category");

                ListView modeList = new ListView(MapsActivity.this);
                final String[] stringArray = new String[]{"Bolero", "Tufaan", "Scarpio", "Tavera"};
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
                modeList.setAdapter(modeAdapter);

                modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String val = (String) parent.getItemAtPosition(position);
                        System.out.println("Valueis " + val);
                        tourCarCategory.setText(val);
                        builder.dismiss();
                    }
                });

                builder.setView(modeList);

                builder.show();
            }
        });

        tourPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog builder = new AlertDialog.Builder(MapsActivity.this).create();
                builder.setTitle("Select Payment Mode");

                ListView modeList = new ListView(MapsActivity.this);
                final String[] stringArray = new String[]{"Cash", "Wallet", "Debit/Credit"};
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
                modeList.setAdapter(modeAdapter);

                modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String val = (String) parent.getItemAtPosition(position);
                        System.out.println("Valueis " + val);
                        tourPayment.setText(val);
                        builder.dismiss();
                    }
                });

                builder.setView(modeList);
                builder.show();
            }
        });

        tourAcNonSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tourACMode.setText("AC");
                } else {
                    tourACMode.setText("non-ac");
                }
            }
        });
    }


    private void BookRentalCar() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Please Wait .....");
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> map = new HashMap<>();

        map.put("riderange", tvSelectCat.getText().toString());
        map.put("vehicle", etCarsCat.getText().toString());
        map.put("seater", tvSeater.getText().toString());
        map.put("type", tvACMode.getText().toString());
        map.put("vehicle_category", categoryVehicleDetailListId.get(position));

        Log.d("hmdfcuksdf", String.valueOf(new JSONObject(map)));

        Call<JsonObject> call = apiInterface.rental(map, "Bearer " + user.get(Keys.KEY_TOKEN));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Shriji Cabs")
                            .setContentText("Booking Success")
                            .show();
                } else {
                    Toast.makeText(MapsActivity.this, response.code() + " ", Toast.LENGTH_SHORT).show();
                }
//

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
                new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Shriji Cabs")
                        .setContentText(t.toString())
                        .show();
                Toast.makeText(MapsActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void BookTourCar() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Please Wait .....");
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> map = new HashMap<>();

        map.put("riderange", tourCategory.getText().toString());
        map.put("vehicle", tourCarCategory.getText().toString());
        map.put("seater", tourSeater.getText().toString());
        map.put("type", tourACMode.getText().toString());
        map.put("vehicle_category", categoryVehicleDetailListId.get(position));

        Log.d("hmdfcuksdf", String.valueOf(new JSONObject(map)));

        Call<JsonObject> call = apiInterface.rental(map, "Bearer " + user.get(Keys.KEY_TOKEN));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Shriji Cabs")
                            .setContentText("Booking Success")
                            .show();
                } else {
                    Toast.makeText(MapsActivity.this, response.code() + " ", Toast.LENGTH_SHORT).show();
                }
//

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
                new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Shriji Cabs")
                        .setContentText(t.toString())
                        .show();
                Toast.makeText(MapsActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendClientDetailsToVendor(String dateAndTime) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Please Wait .....");
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> map = new HashMap<>();
        map.put("schedule_at", dateAndTime);
        map.put("latitude", String.valueOf(pickupLocationLatitude));
        map.put("longitude", String.valueOf(pickupLocationLongitude));
        map.put("drop_latitude", String.valueOf(dropLocationLatitude));
        map.put("drop_longitude", String.valueOf(dropoLocationLongitude));

        if (radioOneWay.isChecked()) {
            map.put("way", "1");
        } else if (radioRoundWay.isChecked()) {
            map.put("way", "2");
        }
        if (radioGroupAcNonac.getVisibility() == View.GONE) {
            map.put("ac", "");
            map.put("non_ac", "");
        } else {
            if (radioAc.isChecked()) {
                map.put("ac", "Ac");
            } else {
                map.put("non_ac", "Non Ac");
            }
        }
        map.put("vehicle_detail", categoryVehicleListId.get(position));
        Log.d("hmdfcuksdf", String.valueOf(new JSONObject(map)));

        Call<JsonObject> call = apiInterface.booking(map, "Bearer " + user.get(Keys.KEY_TOKEN));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(MapsActivity.this, response.code() + " ", Toast.LENGTH_SHORT).show();
                if (response.isSuccessful()) {
                    Log.i("onLogin", response.body().toString());
                    vAnimator.start();
                    appBarLayout.setVisibility(View.GONE);
                    floatingActionButton.setVisibility(View.GONE);
                    confirmAdressOrBookNow_Btn.setVisibility(View.GONE);
                    searchingId.setVisibility(View.VISIBLE);
                    selectCategory.setText("Searching for Vendor");
                    progressBarHorizontal.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                    shimmerFrameLayout.startShimmerAnimation();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (intValue < 100) {
                                intValue++;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBarHorizontal.setProgress(intValue);
                                    }
                                });
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(20000);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        vAnimator.removeAllListeners();
                                        vAnimator.end();
                                        vAnimator.cancel();
                                        shimmerFrameLayout.stopShimmerAnimation();
                                        appBarLayout.setVisibility(View.VISIBLE);
                                        floatingActionButton.setVisibility(View.VISIBLE);
                                        confirmAdressOrBookNow_Btn.setVisibility(View.VISIBLE);
                                        searchingId.setVisibility(View.GONE);
                                        Intent i = new Intent(MapsActivity.this, SourceDestinationOnMapActivity.class);
                                        i.putExtra("drop_let", dropLocationLatitude + "");
                                        i.putExtra("drop_long", dropoLocationLongitude + "");
                                        i.putExtra("pick_lat", pickupLocationLatitude + "");
                                        i.putExtra("pic_long", pickupLocationLongitude + "");
                                        startActivity(i);
//                                        startActivity(new Intent(MapsActivity.this, SourceDestinationOnMapActivity.class));     //Payment Activity
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(20));

                } else {
                    try {
                        JsonObject object = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                        String[] error = object.get("error").toString().split(",");
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < error.length; i++) {
                            stringBuilder.append(error[i] + "\n");
                        }
                        showDailogForError(stringBuilder.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(e.toString(), "fdgfsgfd");
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
                new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Shriji Cabs")
                        .setContentText(t.toString())
                        .show();
                Toast.makeText(MapsActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDailogForError(String error) {
        new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Shriji Cabs")
                .setContentText(error)
                .show();
    }

    private void getVehicleInfoFromTheServer() {
        StringRequest request = new StringRequest(Request.Method.GET, Apis.VEHICLE_CATEGORY_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String category = jsonObject.getString("name");
                        String categoryId = jsonObject.getString("id");
                        categoryVehicleList.add(category);
                        categoryVehicleListId.add(categoryId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, "Please Check Your Connectivity", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = ShrijiCabsSingleton.getInstance(this).getRequestQueue();
        requestQueue.add(request);
    }


    private void getVehicalDetails() {
        StringRequest request = new StringRequest(Request.Method.GET, Apis.VEHICLE_DETAIL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String category = jsonObject.getString("number");
                        String categoryId = jsonObject.getString("id");
                        categoryVehicleDetailList.add(category);
                        categoryVehicleDetailListId.add(categoryId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, "Please Check Your Connectivity", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = ShrijiCabsSingleton.getInstance(this).getRequestQueue();
        requestQueue.add(request);
    }

    private void showAllVehicleInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Your Car Category");
        builder.setItems(categoryVehicleList.toArray(new String[categoryVehicleList.size()]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                vehicleCategories.setText(categoryVehicleList.get(item));
                position = item;
                if (position != 0) {
                    radioGroupAcNonac.setVisibility(View.VISIBLE);
                } else {
                    radioGroupAcNonac.setVisibility(View.GONE);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAllVehicleDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Your Car");
        builder.setItems(categoryVehicleDetailList.toArray(new String[categoryVehicleDetailList.size()]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                tvSelectCars.setText(categoryVehicleDetailList.get(item));
//                position = item;
//                if (position != 0) {
//                    radioGroupAcNonac.setVisibility(View.VISIBLE);
//                } else {
//                    radioGroupAcNonac.setVisibility(View.GONE);
//                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAllVehicleDetailsTour() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Your Car");
        builder.setItems(categoryVehicleDetailList.toArray(new String[categoryVehicleDetailList.size()]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                tourCar.setText(categoryVehicleDetailList.get(item));
//                position = item;
//                if (position != 0) {
//                    radioGroupAcNonac.setVisibility(View.VISIBLE);
//                } else {
//                    radioGroupAcNonac.setVisibility(View.GONE);
//                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String getAdressFromLatLng(double latitude, double longitude) {
        String address = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(e.toString(), "dsgsyfwas");
        }
        return address;
    }

    private void getProfileImageFromServer() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Please Wait....");
        dialog.show();

        Call<JsonObject> call = apiInterface.getFiles("Bearer " + user.get(Keys.KEY_TOKEN));

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                dialog.dismiss();

//                Log.i("Tesponse ", response.body().toString());
                try {
                    JSONObject responseinjsonObject = new JSONObject(String.valueOf(response.body()));

                    if (responseinjsonObject.getString("status").equals("success")) {
                        String data = responseinjsonObject.getString("data");
                        JSONArray jsonArray = new JSONArray(data);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String file = jsonObject.getString("file");
                        JSONObject fileDesc = new JSONObject(file);
                        String imagePath = fileDesc.getString("path");
                        sessionManager.updateProfileToSp(imagePath, getApplicationContext());
                        Picasso.get().load(imagePath).into(userProfileImage);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(MapsActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(final CameraPosition cameraPosition) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        LatLng mCenterLatLong = cameraPosition.target;


                        try {
                            if (editTextPickupLocation.getVisibility() == View.VISIBLE) {
                                pickupLocationLatLng = mCenterLatLong.latitude + "," + mCenterLatLong.longitude;
                                editTextPickupLocation.setText(getAdressFromLatLng(mCenterLatLong.latitude, mCenterLatLong.longitude));

                            } else if (editText_dropLocation.getVisibility() == View.VISIBLE) {
                                dropLocationLatLng = mCenterLatLong.latitude + "," + mCenterLatLong.longitude;
                                editText_dropLocation.setText(getAdressFromLatLng(mCenterLatLong.latitude, mCenterLatLong.longitude));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        });
    }

    public void addMarker(Place p) {

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(p.getLatLng());
        markerOptions.title(p.getName() + "");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(p.getLatLng()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }

    private void drawRouteBetweenTwoLatlng() {
        if (pickupLocationLatLng != null && dropLocationLatLng != null) {
            String[] pickupLocationLatLngStringArr = pickupLocationLatLng.split(",");
            String[] dropLocationLatLngStringArr = dropLocationLatLng.split(",");
            Toast.makeText(this, pickupLocationLatLng + "  " + dropLocationLatLng, Toast.LENGTH_SHORT).show();
            pickupLocationLatitude = Double.parseDouble(pickupLocationLatLngStringArr[0]);
            pickupLocationLongitude = Double.parseDouble(pickupLocationLatLngStringArr[1]);
            dropLocationLatitude = Double.parseDouble(dropLocationLatLngStringArr[0]);
            dropoLocationLongitude = Double.parseDouble(dropLocationLatLngStringArr[1]);
            LatLng pickupLocationLatLng = new LatLng(pickupLocationLatitude, pickupLocationLongitude);
            LatLng dropLocationLatLng = new LatLng(dropLocationLatitude, dropoLocationLongitude);
            String url = getUrl(pickupLocationLatLng, dropLocationLatLng);
            Log.d("onMapClick", url);
            FetchUrl fetchUrl = new FetchUrl();// Start downloading json data from Google Directions API
            fetchUrl.execute(url);//move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pickupLocationLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);// Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();// Connecting to url
            urlConnection.connect();// Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data);
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("success", data);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("error", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask(); // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0]);
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());// Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());
            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;// Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();// Fetching i-th routeList
                List<HashMap<String, String>> path = result.get(i);// Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }// Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
                Log.d("onPostExecute", "onPostExecute lineoptions decoded");
            }// Drawing polyline in the Google Map for the i-th route

            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.userName:
                Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
//            case R.id.nav_gallery:
//                Toast.makeText(this, "Gallery is clicked", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.nav_slideshow:
//                Toast.makeText(this, "Slideshow is clicked", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.nav_manage:
//                Toast.makeText(this, "Tools is clicked", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.nav_share:
//                Toast.makeText(this,  "Share is clicked", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.nav_send:
//                Toast.makeText(this,  "Send is clicked", Toast.LENGTH_SHORT).show();
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (appBarLayout.getVisibility() == View.GONE) {
            pinLocation.setVisibility(View.GONE);
            confirmAdressOrBookNowTv.setText("Book Now");
            appBarLayout.setVisibility(View.VISIBLE);
            floatingActionButton.setVisibility(View.VISIBLE);
            showingFirst = true;
            floatingActionButton.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        } else {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
            dialog.setMessage("Do You Want to Close this application");
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MapsActivity.super.onBackPressed();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog builder = dialog.create();
            builder.setTitle("Shriji Cabs");
            builder.show();
        }

    }

//    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {
//
//        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
//        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
//        markerImageView.setImageResource(resId);
//        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
//        customMarkerView.buildDrawingCache();
//        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
//                Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(returnedBitmap);
//        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
//        Drawable drawable = customMarkerView.getBackground();
//        if (drawable != null)
//            drawable.draw(canvas);
//        customMarkerView.draw(canvas);
//        return returnedBitmap;
//    }

//    private void addCustomMarker() {
//        Log.d("tvrf", "addCustomMarker()");
//        if (mMap == null) {
//            return;
//        }
//
//        // adding a marker on map with image from  drawable
//        mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))
//                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.avatar))));
//    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(final Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        final Circle circle = mMap.addCircle(new CircleOptions().center(new LatLng(location.getLatitude(), location.getLongitude()))
                .strokeColor(Color.BLUE).radius(100));
        circle.setRadius(999999999);

        vAnimator = new ValueAnimator();
        vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        vAnimator.setRepeatMode(ValueAnimator.RESTART);  /* PULSE */
        vAnimator.setIntValues(0, 100);
        vAnimator.setDuration(1000);
        vAnimator.setEvaluator(new IntEvaluator());
        vAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                // Log.e("", "" + animatedFraction);
                circle.setRadius(animatedFraction * 100);
            }
        });

//        imageViewGps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                editTextPickupLocation.setText(getAdressFromLatLng(location.getLatitude(),location.getLongitude()));
//            }
//        });

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }
}
