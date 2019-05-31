package jairfranco.com.tec2.pfran.calendario;

import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

/**
 * Created by LuisTrejo on 20/05/2019.
 */

public class FirebaseDatabaseControl {

    private static Query mQuery;
    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference databaseReference;
    private static AdapterRoutine adapterRoutine;
    private static ArrayList<RoutineObject> mAdapterItems;
    private static ArrayList<String> mAdapterKeys;

    public static DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public static void setUpDataBase() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            //firebaseDatabase.setPersistenceEnabled(true);
            databaseReference = firebaseDatabase.getReference();
        }
    }

    public static Query getQuery(String userId) {
        mQuery = databaseReference.child("Users").child("Customers").child(userId).child("Routines");
        return mQuery;
    }

    public static AdapterRoutine Adapter(String userId) {
        if (adapterRoutine == null) {
            adapterRoutine = new AdapterRoutine(getQuery(userId), RoutineObject.class, mAdapterItems, mAdapterKeys);
            return adapterRoutine;
        } else {
            return adapterRoutine;
        }
    }
}
