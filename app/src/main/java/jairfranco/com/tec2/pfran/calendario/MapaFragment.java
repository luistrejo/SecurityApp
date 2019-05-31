package jairfranco.com.tec2.pfran.calendario;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapaFragment extends Fragment implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {


    private MapView mapView;
    private MapboxMap map;
    private LocationComponent locationComponent;
    private PermissionsManager permissionsManager;
    private boolean markerOnMap = false;

    public MapaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_mapa, container, false);

        mapView = layout.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return layout;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        map = mapboxMap;
        getUsersIds();
        mapboxMap.addOnMapClickListener(this);
        mapboxMap.setStyle(new Style.Builder().fromUrl(getString(R.string.mapbox_style)), style -> {
            if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
                enableLocationComponent();

            }
        });
    }


    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return false;
    }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent() {

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

            // Get an instance of the component
            locationComponent = map.getLocationComponent();
            // Activate with options
            locationComponent.activateLocationComponent(getContext(), map.getStyle());
            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.NONE);
            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        //Toast.makeText(getContext(), "Permiso", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent();
        } else {
            Toast.makeText(getContext(), "Permiso no otorgado", Toast.LENGTH_LONG).show();
        }
    }

    private void showCirclesOnMap() {


    }

    private void getUsersIds() {

        SharedPreferences prefs = getActivity().getSharedPreferences("USERID", Context.MODE_PRIVATE);
        String userID = prefs.getString("key", null);
        FirebaseDatabaseControl.setUpDataBase();
        FirebaseDatabaseControl.getDatabaseReference()
                .child("Users")
                .child("Customers")
                .child(userID)
                .child("circles")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ArrayList<String> circles = (ArrayList<String>) dataSnapshot.getValue();

                            for (String c : circles) {
                                Log.d("circulos que sigue25323", c);
                                FirebaseDatabaseControl.getDatabaseReference()
                                        .child("Users")
                                        .child("Customers")
                                        .child(c)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                Circles perfil = dataSnapshot.getValue(Circles.class);
                                                Log.d("CAMBIOS SUSCRIBE", perfil.getName());

                                                if (map.getStyle().getLayer("symbollayer-" + perfil.getId()) != null) {
                                                    map.getStyle().removeLayer("symbollayer-" + perfil.getId());
                                                    map.getStyle().removeSource("source-" + perfil.getId());
                                                }

                                                if (!markerOnMap) {
                                                    downloadImage(perfil);
                                                } else {
                                                    cargarMarker(perfil);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }

                });
    }

    public void downloadImage(Circles src) {

        AsyncTask.execute(() -> {
            try {
                URL url;
                if (src == null) {
                    url = new URL("https://cdn2.iconfinder.com/data/icons/people-80/96/Picture1-512.png");
                } else {
                    url = new URL(src.getPicUrl());
                }
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                map.getStyle().addImage("marker-" + src.getId(), getCroppedBitmap(myBitmap));
                markerOnMap = true;
                cargarMarker(src);
            } catch (IOException e) {
                // Log exception
                Log.d("ERROR", "Error de carga de img - " + e.getMessage());
            }
        });
    }

    private void cargarMarker(Circles circle) {
        getActivity().runOnUiThread(() -> {
                    Point feature = Point.fromLngLat(Double.parseDouble(circle.getLongitud()), Double.parseDouble(circle.getLatitud()));
                    GeoJsonSource geoJsonSource = new GeoJsonSource("source-" + circle.getId(), feature);
                    map.getStyle().addSource(geoJsonSource);

                    SymbolLayer markerSymbolLayer = new SymbolLayer("symbollayer-" + circle.getId(), "source-" + circle.getId())
                            .withProperties(
                                    PropertyFactory.iconImage("marker-" + circle.getId()),
                                    PropertyFactory.iconSize(.2f),
                                    PropertyFactory.iconAllowOverlap(true));

                    map.getStyle().addLayer(markerSymbolLayer);
                }
        );

    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

}
