package com.example.m08_uf2_proyecto_fatimazahrafirdawsi_joelvinansaca;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import com.example.m08_uf2_proyecto_fatimazahrafirdawsi_joelvinansaca.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {

    private int counter = 0;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private ActivityMainBinding binding;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        setup();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(AuthActivity.this, HomeActivity.class));
            finish();
        }
    }

    private void setup(){
        setTitle("Autenticación");

        final EditText editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        final EditText editTextPass = (EditText) findViewById(R.id.editTextPassword);

        Button signUpbutton = (Button) findViewById(R.id.signUpButton);
        Button logInButton = (Button) findViewById(R.id.logInButton);

        signUpbutton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPass.getText().toString();

                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(AuthActivity.this, "Los campos no pueden estar vacios", Toast.LENGTH_SHORT).show();
                } else {
                    // Toast para saber que pasa.
                    // Toast for know the state of the authentication
                    Toast.makeText(AuthActivity.this,"Registrando usuario...", Toast.LENGTH_SHORT).show();
                    // Metodo para registrarse e iniciar sesion.
                    // Method or function for registering and logging in
                    registerUser(email, password);


                    /*db.collection("users").document("userCount")
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        long count = document.getLong("count");
                                        if (count < 2){
                                            // Metodo para registrarse e iniciar sesion.
                                            // Method or function for registering and logging in
                                            registerUser(email, password);
                                        } else {
                                            Log.e(TAG, "ERROR 1");
                                        }
                                    }else {
                                        registerUser(email, password);
                                    }
                                }else {
                                    Log.e(TAG, "ERROR 2");
                                }
                            });
                     */
                }
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPass.getText().toString();

                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(AuthActivity.this, "Los campos no pueden estar vacios", Toast.LENGTH_SHORT).show();
                } else {
                    // Toast
                    Toast.makeText(AuthActivity.this,"Iniciando sesion...", Toast.LENGTH_SHORT).show();
                    // Metodo para iniciar sesion.
                    loginUser(email, password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            startActivity(new Intent(AuthActivity.this, HomeActivity.class));

                            // TOKEN




                            FirebaseUser user = task.getResult().getUser();
                            user.getIdToken(true)
                                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                                @SuppressLint("RestrictedApi")
                                                @Override
                                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                    String idToken = task.getResult().getToken();
                                                    Log.d(TAG, "onComplete: " + idToken);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @SuppressLint("RestrictedApi")
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e(TAG, "onFailure: ERROR", e);
                                                }
                                            });

                            finish();
                            Toast.makeText(AuthActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(AuthActivity.this,"Los datos son incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AuthActivity.this,"Error al iniciar sesion", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUser(String email, String password) {

        if (counter == 2) {
            Toast.makeText(AuthActivity.this, "Alcanzado el maximo de cuentas: 2", Toast.LENGTH_SHORT).show();
        } else {

            counter++;
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String id = mAuth.getCurrentUser().getUid();
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", id);
                            map.put("email", email);
                            map.put("password",password);

                            mFirestore.collection("user").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    if (task.isSuccessful()) {
                                        // Registrar usuario
                                        // Register the user
                                        startActivity(new Intent(AuthActivity.this, HomeActivity.class));
                                        finish();

                                        Toast.makeText(AuthActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Si falla envia mensaje de error
                                        // If it fails, it sends an error message
                                        Toast.makeText(AuthActivity.this, "Registro fallido", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(this,new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AuthActivity.this, "Error al registrarse", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}