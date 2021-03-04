package com.aryupay.shrijicabs.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.aryupay.shrijicabs.R;
import com.aryupay.shrijicabs.adapters.ShrijicabsUserSessionManager;
import com.aryupay.shrijicabs.interfaces.ApiClient;
import com.aryupay.shrijicabs.interfaces.ApiInterface;
import com.aryupay.shrijicabs.model.DirectionObject;
import com.aryupay.shrijicabs.model.LegsObject;
import com.aryupay.shrijicabs.model.LocationVendorModel;
import com.aryupay.shrijicabs.model.PolylineObject;
import com.aryupay.shrijicabs.model.RouteObject;
import com.aryupay.shrijicabs.model.StepsObject;
import com.aryupay.shrijicabs.utils.DataParser;
import com.aryupay.shrijicabs.utils.Keys;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Duration;
import com.google.maps.model.TravelMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SourceDestinationOnMapActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    ImageView ivVehiclePic, ivVendorPic;
    public static DirectionCallback directionCallback;
    private GoogleMap sdmap;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    SharedPreferences preferences;
    private static String serverKey = "AIzaSyAlxwpBTBr0LEKoV8xRD3cp8njTKPV_bZY";
    MarkerOptions origin, destination;
    Double drop_lat, drop_long, pick_lat, pick_long;
    TextView tvVehicleName, tvVehicleType, tvVehicleRegNo, tvVendorName, tvDistanceDuration;
    String droplat, droplong, pickuplat, pickuplong, vehicleName, VehicalModel, VehicalNumber, vendroName, Booking_ID, OTP, booking_status_id;
    //    String droplat, droplong, picklat, pick_long;
    ArrayList<LatLng> markerPoints;
    ShrijicabsUserSessionManager sessionManager;
    String PickPoint;
    LatLng DropPoint;
    String token;

    public static void requestDirection(LatLng orgin, LatLng destination) {

        //this.origin = origin;
        //((BaseActivity) mActivity).showProgress();
//        destination = new LatLng(origin.latitude - 1, origin.longitude - 1);
        GoogleDirection.withServerKey(serverKey)
                .from(orgin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(directionCallback);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_source_destination);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Enjoy Your Ride");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.backgroundNavColor), PorterDuff.Mode.SRC_ATOP);

        directionCallback = this;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.sdmap);
        mapFragment.getMapAsync(this);
        markerPoints = new ArrayList<LatLng>();
        ivVehiclePic = findViewById(R.id.ivVehiclePic);
        ivVendorPic = findViewById(R.id.ivVendorPic);
        tvVehicleName = findViewById(R.id.tvVehicleName);
        tvVehicleType = findViewById(R.id.tvVehicleType);
        tvVehicleRegNo = findViewById(R.id.tvVehicleRegNo);
        tvVendorName = findViewById(R.id.tvVendorName);
        tvDistanceDuration = findViewById(R.id.tvDistanceDuration);
        ivVendorPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SourceDestinationOnMapActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });


        preferences = this.getSharedPreferences("user_info", MODE_PRIVATE);
        sessionManager = new ShrijicabsUserSessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        token = user.get(Keys.KEY_TOKEN);

        droplat = preferences.getString("drop_let", "");
        droplong = preferences.getString("drop_long", "");
        pickuplat = preferences.getString("pick_lat", "");
        pickuplong = preferences.getString("pic_long", "");
        vendroName = preferences.getString("vendroName", "");
        vehicleName = preferences.getString("vehicleName", "");
        VehicalModel = preferences.getString("VehicalModel", "");
        VehicalNumber = preferences.getString("VehicalNumber", "");
        Booking_ID = preferences.getString("id", "");
        OTP = preferences.getString("otp", "");
        booking_status_id = preferences.getString("booking_status_id", "");

        Log.d("journy", "" + droplat + "-" + droplong + "-" + pickuplat + "-" + pickuplong);

        drop_lat = Double.parseDouble(droplat);
        drop_long = Double.parseDouble(droplong);
        pick_lat = Double.parseDouble(pickuplat);
        pick_long = Double.parseDouble(pickuplong);

        tvVendorName.setText(vendroName);
        tvVehicleName.setText(vehicleName);
        tvVehicleType.setText(VehicalModel);
        tvVehicleRegNo.setText(VehicalNumber);


        //Setting marker to draw route between these two points
        origin = new MarkerOptions().position(new LatLng(pick_lat, pick_long)).title("hsr").snippet("origin");
        destination = new MarkerOptions().position(new LatLng(drop_lat, drop_long)).title("e_city").snippet("destination");
        requestDirection(origin.getPosition(), destination.getPosition());

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin.getPosition(), destination.getPosition());

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

        final Handler handler = new Handler();
        final int delay = 10000; //milliseconds

//        handler.postDelayed(new Runnable() {
//            public void run() {
//
//
//                handler.postDelayed(this, delay);
//            }
//        }, delay);

        try {
            getDurationForRoute(origin.getPosition() + "", destination.getPosition() + "");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = sdmap.addMarker(markerOptions);

    /*    //move map camera
        float zoomLevel = 11.0f; //This goes up to 21
        sdmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));*/

        sdmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        sdmap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //  sdmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // sdmap.animateCamera(CameraUpdateFactory.zoomTo(19));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        sdmap = googleMap;

/*        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        sdmap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        sdmap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                sdmap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            sdmap.setMyLocationEnabled(true);
        }

        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable() {
            public void run() {
                //do something
                Call<LocationVendorModel> postCodeModelCall = ApiClient.getClient().create(ApiInterface.class).LocationVendorModel(Booking_ID, "Bearer " + token);
                postCodeModelCall.enqueue(new Callback<LocationVendorModel>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NonNull Call<LocationVendorModel> call, @NonNull Response<LocationVendorModel> response) {
                        final LocationVendorModel object = response.body();

                        if (object != null) {
                            Log.e("LOCATION==>", new Gson().toJson(response.body()));
                            LatLng sydney = new LatLng(object.getData().getVendorlocation().getLatitude(), object.getData().getVendorlocation().getLongitude());
                            sdmap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)).title("Driver Location"));
                            sdmap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        } else {

                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<LocationVendorModel> call, @NonNull Throwable t) {

                        t.printStackTrace();
                        Log.e("Category_Response", t.getMessage() + "");
                    }
                });

                handler.postDelayed(this, delay);
            }
        }, delay);


    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        // Snackbar.make(btnRequestDirection, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();

        if (direction.isOK()) {
            Route route = direction.getRouteList().get(0);


            double lat_d = Double.parseDouble(droplat);
            double long_d = Double.parseDouble(droplong);
            double piclat = Double.parseDouble(pickuplat);
            double piclong = Double.parseDouble(pickuplong);


            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;
            List<Address> addresses1 = null;
            try {
                addresses = geocoder.getFromLocation(lat_d, long_d, 1);
                addresses1 = geocoder.getFromLocation(piclat, piclong, 1);

                String pick_add = addresses.get(0).getAddressLine(0);
                String drop_add = addresses1.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String zip = addresses.get(0).getPostalCode();
                String country = addresses.get(0).getCountryName();

                Log.d("my_add", "" + drop_add);
                sdmap.clear();

                sdmap.addMarker(new MarkerOptions()
                        .position(origin.getPosition())
                        .title(drop_add)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                sdmap.addMarker(new MarkerOptions()
                        .position(destination.getPosition())
                        .title(pick_add)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                if (getApplicationContext() != null) {
                    sdmap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.BLACK));
                    setCameraWithCoordinationBounds(route);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            //btnRequestDirection.setVisibility(View.GONE);
        } else {
            Toast.makeText(getApplicationContext(), direction.getStatus() + "", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        sdmap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }


    public String getDurationForRoute(String origin, String destination) throws InterruptedException, ApiException, IOException {

        // - We need a context to access the API
        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey(serverKey)
                .build();

        // - Perform the actual request
        DirectionsResult directionsResult = DirectionsApi.newRequest(geoApiContext)
                .mode(TravelMode.DRIVING)
                .origin(origin)
                .destination(destination)
                .await();

        // - Parse the result
        DirectionsRoute route = directionsResult.routes[0];
        DirectionsLeg leg = route.legs[0];
        Duration duration = leg.duration;

        Log.e("DUration==>", duration + "");
        return duration.humanReadable;
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        //setting transportation mode
        String mode = "mode=driving";
        // Building the parameters to the web service
        final String parameters = str_origin + "&" + str_dest + "&" + mode;

        // Output format
        final String output = "json";

        // Building the url to the web service AIzaSyBYSoJSFeNG6tcSJKBuH34TkvmvN8wHZek  AIzaSyCww6hnX83Xpo5LKkvdilxlJoDUShbDLY8
        final String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + serverKey;


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response;
                String parsedDistance;
                try {
                    URL url = new URL("https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + serverKey);
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = org.apache.commons.io.IOUtils.toString(in, StandardCharsets.UTF_8);

                    final JSONObject json = new JSONObject(response);
                    JSONArray routeArray = json.getJSONArray("routes");
                    JSONObject routes = routeArray.getJSONObject(0);

                    JSONArray newTempARr = routes.getJSONArray("legs");
                    JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

                    JSONObject distOb = newDisTimeOb.getJSONObject("distance");
                    JSONObject timeOb = newDisTimeOb.getJSONObject("duration");

                    Log.i("Diatance :", distOb.getString("text"));
                    Log.i("Time :", timeOb.getString("text"));
                    tvDistanceDuration.setText("Distance : " + distOb.getString("text") + ", Duration : " + timeOb.getString("text"));

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString().trim();
            Log.d("KarandownloadUrl", data);
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();

                routes = parser.parse(jObject);
                Log.d("KAranDURA", routes.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = new ArrayList();
            PolylineOptions lineOptions = new PolylineOptions();
            String distance = "";
            String duration = "";

            for (int i = 0; i < result.size(); i++) {

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    if (j == 0) {    // Get distance from the list
                        distance = point.get("distance");
                        Log.e("distance", point + "");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = point.get("duration");
                        continue;
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);


            }

            // Drawing polyline in the Google Map
            if (points.size() != 0)
                sdmap.addPolyline(lineOptions);
        }
    }


}
