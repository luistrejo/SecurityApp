package jairfranco.com.tec2.pfran.calendario;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static jairfranco.com.tec2.pfran.calendario.LoginActivity.PREFERENCE_FILENAME;


public class RegisterActivity extends AppCompatActivity {

    private EditText nName, nLastName, nEmail, nPassword, n2Password, nTelephone;
    private Button nRegistration;
    private TextView myTextView;
    private FirebaseAuth nAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ProgressBar progressBar;
    Integer counter = 1;
    private String Uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nName = findViewById(R.id.Rnombre);
        nLastName = findViewById(R.id.Rapellido_pat);
        nEmail = findViewById(R.id.Remail);
        nPassword = findViewById(R.id.Rpassword);
        n2Password = findViewById(R.id.R2password);
        nRegistration = findViewById(R.id.Rregistration);
        nTelephone = findViewById(R.id.Rtel);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(1000);


        //Recepcion de datos.
        Bundle parametros = this.getIntent().getExtras();
        if (parametros != null) {
            String dato = parametros.getString("dato");
            nTelephone.setText(dato);
        }
        SharedPreferences gameSettings = getSharedPreferences(PREFERENCE_FILENAME, MODE_PRIVATE);
        Uid = gameSettings.getString("UID", "x");


        nName = (EditText) findViewById(R.id.Rnombre);
        nLastName = (EditText) findViewById(R.id.Rapellido_pat);
        nEmail = (EditText) findViewById(R.id.Remail);
        nPassword = (EditText) findViewById(R.id.Rpassword);
        n2Password = (EditText) findViewById(R.id.R2password);
        nRegistration = (Button) findViewById(R.id.Rregistration);
        nTelephone = (EditText) findViewById(R.id.Rtel);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(1000);


        nAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
//                    Intent intent = new Intent(RegisterActivity.this,  CustomerMapActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivityForResult(intent,100);
//                    finish();
//                    return;
                }
            }
        };


        nRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nRegistration.setVisibility(View.INVISIBLE);
                counter = 1;
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
                switch (v.getId()) {
                    case R.id.btnIngresar:
                        new MyAsyncTask().execute(10);
                        break;
                }

                final String nombre = nName.getText().toString().trim();
                final String apepat = nLastName.getText().toString().trim();


                final String email = nEmail.getText().toString().trim();

                final String password = nPassword.getText().toString().trim();
                final String password2 = n2Password.getText().toString().trim();


                final String telsincod = nTelephone.getText().toString().trim();
                final String ccp = "+52";
                String newString = telsincod.replace("(", "").replace(")", "").replace("-", "").replace(" ", "");
                final String telfinal = ccp + newString.trim();


                final Query telquery = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").orderByChild("phone").equalTo(telfinal);

                telquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && email != null && password != null && !TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(apepat) && nombre != null && apepat != null && !TextUtils.isEmpty(telsincod) && telsincod != null) {
                            if (password.equals(password2)) {
                                if (telsincod.length() < 15) {
                                    progressBar.setVisibility(View.GONE);
                                    nRegistration.setVisibility(View.VISIBLE);
                                    Toast.makeText(RegisterActivity.this, "Ingresa un Telefono Valido.", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        progressBar.setVisibility(View.GONE);
                                        nRegistration.setVisibility(View.VISIBLE);
                                        Toast.makeText(RegisterActivity.this, "El Telefono que Ingresaste ya se Encuentra Registrado.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String user_id = Uid;
                                        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id);
                                        progressBar.setVisibility(View.INVISIBLE);

                                        Map newPost = new HashMap();

                                        newPost.put("name", nombre);
                                        newPost.put("lastname", apepat);
                                        newPost.put("phone", telfinal);
                                        newPost.put("email", email);
                                        newPost.put("password", password);


                                        current_user_db.setValue(newPost);
                                        Toast.makeText(RegisterActivity.this, "REGISTRO EXITOSO", Toast.LENGTH_LONG).show();


                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;


                                    }
                                }

                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            nRegistration.setVisibility(View.VISIBLE);
                            Toast.makeText(RegisterActivity.this, "Debes Llenar Todos los Campos.", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        nAuth.removeAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        nAuth.removeAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onBackPressed() {

        Intent intent2 = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent2);
        super.onBackPressed();
    }

    class MyAsyncTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            for (; counter <= params[0]; counter++) {
                try {
                    Thread.sleep(1000);
                    publishProgress(counter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Tarea completa!. =)";
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            progressBar.setProgress(values[0]);
        }


    }


}
