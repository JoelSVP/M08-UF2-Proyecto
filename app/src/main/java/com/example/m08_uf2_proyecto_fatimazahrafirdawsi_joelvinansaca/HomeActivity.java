package com.example.m08_uf2_proyecto_fatimazahrafirdawsi_joelvinansaca;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        DocumentReference docRef = db.collection("documentos").document("documento");

        db.collection("documentos").document("documento")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                String titulo = document.getString("Titulo");
                                String cuerpo = document.getString("Cuerpo");
                                EditText editTextTitulo = findViewById(R.id.editTextTitle);
                                EditText editTextCuerpo = findViewById(R.id.editTextBody);
                                editTextTitulo.setText(titulo);
                                editTextCuerpo.setText(cuerpo);
                            } else {
                                Log.d(TAG, "Success");
                            }
                        } else {
                            Log.d(TAG, "Failure", task.getException());
                        }
                    }
                });


        Button publishButton = findViewById(R.id.publishButton);
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish(docRef);
            }
        });

        setup();
    }

    private void setup(){
        setTitle("Inicio");

        // El email y el proveedor se tienen que ver en el tutorial que vimos pero no hace falta en el proyecto creo.
        // The email and the provider should be seen in the tutorial we saw but it is not needed in the project I think.

        Button logOutButton = (Button) findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this,AuthActivity.class));
            }
        });

    }

    private void publish(DocumentReference docRef){

        EditText titulo = findViewById(R.id.editTextTitle);
        EditText cuerpo = findViewById(R.id.editTextBody);

        String tituloText = String.valueOf(titulo.getText());
        String cuerpoText = String.valueOf(cuerpo.getText());

        Map<String, Object> docData = new HashMap<>();
        docData.put("Titulo",tituloText);
        docData.put("Cuerpo",cuerpoText);

        docRef.set(docData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(HomeActivity.this, "Datos guardados", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Documento actualizado con éxito");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error al actualizar el documento", e);
                    }
                });
    }


}