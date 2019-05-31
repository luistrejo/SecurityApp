package jairfranco.com.tec2.pfran.calendario;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {
    private Toolbar appbar;
    BottomNavigationView bottomNavigationView;
    private String fragmentCurrent = "";
    private SparseArray<String> drawerFragments;
    private LocationRequest locRequest;
    private GoogleApiClient apiClient;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabaseControl.setUpDataBase();

        appbar = findViewById(R.id.toolbar);
        setSupportActionBar(appbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Mapbox.getInstance(this, getString(R.string.mapbox_key));
        setInitialFragment();

        SharedPreferences prefs = getSharedPreferences("USERID", Context.MODE_PRIVATE);
        userID = prefs.getString("key", null);

        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        apiClient.connect();
        enableLocationUpdates();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        replaceFragment(drawerFragments.get(item.getItemId()));
        return true;
    }

    private void setInitialFragment() {
        drawerFragments = new SparseArray<>();
        drawerFragments.append(R.id.menu_home, HomeFragment.class.getName());
        drawerFragments.append(R.id.menu_circulos, CirculosFragment.class.getName());
        drawerFragments.append(R.id.menu_rutinas, RutinasFragment.class.getName());
        drawerFragments.append(R.id.menu_mapa, MapaFragment.class.getName());


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, new HomeFragment());
        fragmentCurrent = drawerFragments.get(0);
        fragmentTransaction.commit();
    }

    private void replaceFragment(String fname) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment current = getSupportFragmentManager().findFragmentByTag(fragmentCurrent);

        Fragment f = getSupportFragmentManager().findFragmentByTag(fname);
        if (current != null && current.isVisible() && f != null) {
            boolean iguales = current.equals(f);
            Log.d("IGUALES:", iguales + "");
            if (!iguales) {
                Log.w("FRAGMENTS", "ACTUAL: HIDE : " + current);
                transaction.hide(current);
                transaction.show(f);
                fragmentCurrent = fname;
                Log.w("FRAGMENTS", "SHOW: " + f);
            }
        } else {
            f = Fragment.instantiate(this, fname);
            if (current != null) {
                transaction.hide(current);
            }
            transaction.add(R.id.main_container, f, fname);
            fragmentCurrent = fname;
            Log.w("FRAGMENTS", "Nuevo al stack: " + f);
        }
        try {
            transaction.commitNowAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.w("BusApp", "Oops, la aplicación se cerró antes terminar la carga inicial. No pasa nada, cuando la abras estará bien.");
        }

    }

    private void enableLocationUpdates() {
        locRequest = new LocationRequest();
        locRequest.setInterval(5000);
        locRequest.setFastestInterval(5000);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        new LocationSettingsRequest.Builder()
                .addLocationRequest(locRequest)
                .build();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest, this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        FirebaseDatabaseControl.getDatabaseReference()
                .child("Users")
                .child("Customers")
                .child(userID)
                .child("longitud")
                .setValue(String.valueOf(latLng.getLongitude()));

        FirebaseDatabaseControl.getDatabaseReference()
                .child("Users")
                .child("Customers")
                .child(userID)
                .child("latitud")
                .setValue(String.valueOf(latLng.getLatitude()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                apiClient, this);
        apiClient.disconnect();
    }
}
