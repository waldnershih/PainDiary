package com.example.paindiary.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.paindiary.R;
import com.example.paindiary.databinding.MapFragmentBinding;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.io.IOException;
import java.util.List;

public class MapFragment extends Fragment {
    private static final String ICON_ID = "ICON_ID";
    private MapFragmentBinding mapBinding;
    private MapView mapView;
    private Geocoder geocoder;
    private SymbolManager symbolManager;
    private Symbol symbol;

    public MapFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String token = getString(R.string.mapbox_access_token);
        Mapbox.getInstance(getActivity(), token);

        mapBinding = MapFragmentBinding.inflate(inflater, container, false);
        View view = mapBinding.getRoot();
        getActivity().setTitle("Map");

        double[] latLngArray = {91d, 181d};

        geocoder = new Geocoder(getActivity());
        mapView = mapBinding.mapView;
        mapView.onCreate(savedInstanceState);

        String userAddress = getUserAddress();

        if (userAddress != null) {
            try {
                List<Address> addresses = geocoder.getFromLocationName(userAddress, 1);
                Address address = addresses.get(0);
                latLngArray[0] = address.getLatitude();
                latLngArray[1] = address.getLongitude();
                mapBinding.editAddress.setHint(userAddress);
                setInitialMap(latLngArray[0], latLngArray[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mapBinding.editAddress.setHint("Default address: Monash Caulfield campus");
            setInitialMap(-37.876823, 145.045837);
        }

        mapBinding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newAddress = mapBinding.editAddress.getEditText().getText().toString();
                Log.d("asdf", newAddress);
                if (!validateAddressText(newAddress)) {
                    Toast.makeText(getActivity(), "Address input cannot be empty !", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(newAddress, 1);
                        if (addresses != null && addresses.size() > 0) {
                            Address address = addresses.get(0);
                            latLngArray[0] = address.getLatitude();
                            latLngArray[1] = address.getLongitude();
                            setUserAddress(newAddress);
                            resetMap(latLngArray[1], latLngArray[0]);
                        } else {
                            Toast.makeText(getActivity(), "Address not found !", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(),
                                "Unable to get an address !" + "\n" + "Please check your internet or contact the company!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    private void addRedMarkerImageToStyle(Style style) {
        style.addImage(ICON_ID,
                BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.red_marker)),
                true);
    }

    private void setInitialMap(double lat, double lng) {
        if (lat != 91d && lng != 181d) {
            final LatLng latLng = new LatLng(lat, lng);

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                    mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {

                            CameraPosition position = new CameraPosition.Builder()
                                    .target(latLng)
                                    .zoom(13)
                                    .build();

                            mapboxMap.setCameraPosition(position);
                            addRedMarkerImageToStyle(style);

                            // Create symbol manager object.
                            symbolManager = new SymbolManager(mapView, mapboxMap, style);

                            // Set non-data-driven properties.
                            symbolManager.setIconAllowOverlap(true);
                            symbolManager.setTextAllowOverlap(true);


                            // Create a symbol at the specified location.
                            SymbolOptions symbolOptions = new SymbolOptions()
                                    .withLatLng(latLng)
                                    .withIconImage(ICON_ID)
                                    .withIconColor("RED")
                                    .withIconSize(1.3f);


                            symbol = symbolManager.create(symbolOptions);
                        }
                    });
                }
            });
        }
    }

    private void resetMap(double lng, double lat) {
        if (lat != 91d && lng != 181d) {
            final LatLng latLng = new LatLng(lat, lng);
            //lat and long are hardcoded here but could be provided at run time

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                    mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {

                            CameraPosition position = new CameraPosition.Builder()
                                    .target(latLng)
                                    .zoom(13)
                                    .build();

                            mapboxMap.setCameraPosition(position);
                            addRedMarkerImageToStyle(style);

                            resetRedMarker(lng, lat);
                        }
                    });
                }
            });
        }
    }

    private void resetRedMarker(double lng, double lat) {
        symbol.setIconRotate(0.0f);
        symbol.setGeometry(Point.fromLngLat(lng, lat));
        symbolManager.update(symbol);
    }

    private String getUserAddress() {
        SharedPreferences sharedPref = requireActivity()
                .getApplicationContext()
                .getSharedPreferences("Address", Context.MODE_PRIVATE);
        return sharedPref.getString("Address",null);
    }

    private void setUserAddress(String newAddress) {
        SharedPreferences sharedPref = requireActivity()
                .getApplicationContext()
                .getSharedPreferences("Address", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putString("Address", newAddress);
        spEditor.apply();
    }

    private boolean validateAddressText(String addressText) {
        if (addressText == null) {
            return false;
        }

        if (addressText.isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapBinding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}


/**
 * Reference
 * https://javapapers.com/android/android-geocoding-to-get-latitude-longitude-for-an-address/
 * https://docs.mapbox.com/android/plugins/guides/annotation/
 * https://stackoverflow.com/questions/37348547/get-latitude-and-longitude-from-the-address-with-mapbox
 * https://github.com/mapbox/mapbox-plugins-android/blob/master/app/src/main/java/com/mapbox/mapboxsdk/plugins/testapp/activity/annotation/SymbolActivity.java
 * https://docs.mapbox.com/android/plugins/examples/global-location-search/
 */
