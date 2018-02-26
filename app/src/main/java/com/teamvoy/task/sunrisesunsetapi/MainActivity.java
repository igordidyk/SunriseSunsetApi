package com.teamvoy.task.sunrisesunsetapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView placeNameTV, placeAddress;
    private TextView sunriseTV, sunsetTV, dayLengthTV;
    private Button pickerButton;

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final String SUNRISE_SUNSET_API_URL = "https://api.sunrise-sunset.org/json?";
    private static final String STATUS_OK = "OK";
    private static final String STATUS_INVALID_REQUEST = "INVALID_REQUEST";
    private static final String STATUS_INVALID_DATE = "INVALID_DATE";

    private static final String STATUS_UNKNOWN_ERROR = "UNKNOWN_ERROR";


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        placeNameTV = findViewById(R.id.placeNameTV);
        placeAddress = findViewById(R.id.placeAddress);

        sunriseTV = findViewById(R.id.sunriseTV);
        sunsetTV = findViewById(R.id.sunsetTV);
        dayLengthTV = findViewById(R.id.dayLengthTV);
        pickerButton = findViewById(R.id.getPickerPlace);
        pickerButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();

            double latitude = place.getLatLng().latitude;
            double longitude = place.getLatLng().longitude;

            getSunriseSunsetData(latitude, longitude);


            placeNameTV.setText("Coordinates of place: " + name);
            placeAddress.setText("Address: " + address);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getSunriseSunsetData(double latitude, double longitude) {
        RequestQueue request = null;
        if (request == null) {
            request = Volley.newRequestQueue(this);
        }
        String urlJsonObj = SUNRISE_SUNSET_API_URL + "lat=" + String.valueOf(latitude) + "&" + "lng=" + String.valueOf(longitude);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    String status = response.getString("status");
                    if (status.equalsIgnoreCase(STATUS_OK)) {
                        JSONObject results = response.getJSONObject("results");
                        String sunrise = results.getString("sunrise");
                        String sunset = results.getString("sunset");
                        String day_length = results.getString("day_length");
                        sunriseTV.setText("Sunrise time: " + sunrise);
                        sunsetTV.setText("Sunset time: " + sunset);
                        dayLengthTV.setText("Day length: " + day_length);
                    } else if (status.equalsIgnoreCase(STATUS_INVALID_REQUEST)) {
                        Toast.makeText(MainActivity.this, "Either latitude or longitude parameters are missing or invalid", Toast.LENGTH_LONG).show();
                    } else if (status.equalsIgnoreCase(STATUS_INVALID_DATE)) {
                        Toast.makeText(MainActivity.this, "Date parameter is missing or invalid", Toast.LENGTH_LONG).show();
                    } else if (status.equalsIgnoreCase(STATUS_UNKNOWN_ERROR)) {
                        Toast.makeText(MainActivity.this, "Request could not be processed due to a server error. The request may succeed if you try again.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        request.add(jsonObjReq);

    }
}



