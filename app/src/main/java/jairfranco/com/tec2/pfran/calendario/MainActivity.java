package jairfranco.com.tec2.pfran.calendario;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    private Toolbar appbar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private SparseArray<String> drawerFragments;
    private String fragmentCurrent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appbar = findViewById(R.id.toolbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navView = findViewById(R.id.navview);
        setupNavigationDrawer();
    }

    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerFragments = new SparseArray<>();
        drawerFragments.append(R.id.menu_seccion_1, HomeFragment.class.getName());
        drawerFragments.append(R.id.menu_seccion_2, CirculosFragment.class.getName());
        drawerFragments.append(R.id.menu_seccion_3, CalendarioFragment.class.getName());

        navView.setNavigationItemSelectedListener(
                menuItem -> {
                    doSelectDrawerItemById(menuItem.getItemId());
                    return true;
                });
    }

    private void doSelectDrawerItemById(int itemId) {
        switch (itemId) {
//            case R.id.menu_seccion_3:
//                startActivity(new Intent(this, CalendarioFragment.class));
//                drawerLayout.closeDrawer(GravityCompat.START);
//                return;
//            case R.id.menu_seccion_2:
//                startActivity(new Intent(this, CirculosFragment.class));
//                drawerLayout.closeDrawer(GravityCompat.START);
//                return;
//            case R.id.menu_seccion_3:
//                if (user == null) {
//                    login();
//                    return;
//                }
//                startActivity(new Intent(this, GrabarRuta.class));
//                drawerLayout.closeDrawer(GravityCompat.START);
//                return;
            case R.id.menu_opcion_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "SecurityApp, la app para proteger a los que mas quieres. Descargala ahora:");
                startActivity(Intent.createChooser(shareIntent, "Compartir SecurityApp"));
                drawerLayout.closeDrawer(GravityCompat.START);
                return;
            case R.id.menu_opcion_2:
                //login();
                return;
        }
        if (itemId == R.id.menu_seccion_1) {
            doUpdateTitle("SecurityApp");
        } else {
            doUpdateTitle(navView.getMenu().findItem(itemId).getTitle().toString());
        }

        if (itemId == R.id.menu_seccion_2) {
            appbar.setElevation(0);
        } else {
            appbar.setElevation(10);
        }


        if (drawerFragments.get(itemId).equals(HomeFragment.class.getName())) {
            Log.d("FRAGMENTOS", "Es el mapa, no hacer nada");
            doReplaceFragment(drawerFragments.get(itemId), null);
        } else {
            doReplaceFragment(drawerFragments.get(itemId), null);
        }
            
        navView.getMenu().findItem(itemId).setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void doReplaceFragment(String fname, Bundle extras) {
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
            f = Fragment.instantiate(this, fname, extras);
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
            Log.w("Error", "Oops, la aplicación se cerró antes terminar la carga inicial. No pasa nada, cuando la abras estará bien.");
        }

    }

    private void doUpdateTitle(String sName) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(sName);
    }
}
