package com.example.opilane.snake_game;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class RegisterActivity extends AppCompatActivity {

    EditText eesNimi, pereNimi, email, salasõna;
    Button btn_pilt, btn_registreeri;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    String _eesNimi, _pereNimi, _email, _salasõna;
    ImageView profiiliPilt;
    private StorageReference storageReference;
    private static final int CAMERA_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        eesNimi = findViewById(R.id.eesNimi);
        pereNimi = findViewById(R.id.pereNimi);
        email = findViewById(R.id.email);
        salasõna = findViewById(R.id.password);
        btn_registreeri = findViewById(R.id.btnRegistreeri);
        progressDialog = new ProgressDialog(this);
        profiiliPilt = findViewById(R.id.profile_pic);
        btn_pilt = findViewById(R.id.btnTeePilt);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        btn_pilt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pilt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(pilt, CAMERA_REQUEST_CODE);
            }
        });

        btn_registreeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (valideeri()) {
                    String k_email = email.getText().toString().trim();
                    String k_salasõna = salasõna.getText().toString().trim();
                    firebaseAuth.createUserWithEmailAndPassword(k_email, k_salasõna).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.setMessage("Andmete edastamisega läheb aega, palun kannatust!");
                                progressDialog.show();
                                saadaEmailiKinnitus();
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                }
                            }

                        }
                    });
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            profiiliPilt.setImageBitmap(bitmap);
        }
    }



    private boolean valideeri() {
        boolean tulemus = false;
        _eesNimi = eesNimi.getText().toString();
        _pereNimi = pereNimi.getText().toString();
        _email = email.getText().toString();
        _salasõna = salasõna.getText().toString();

        if (_eesNimi.isEmpty() || _pereNimi.isEmpty() || _email.isEmpty() || _salasõna.isEmpty()) {
            teade("Täida kõik väljad!");
        } else {
            tulemus = true;
        }
        return tulemus;
    }

    private void saadaEmailiKinnitus() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //saadame kasutaja sisestatud andmed firebasedatabase'i
                        saadaKasutajaAndmed();
                        teade("Registreerimine õnnestus, teile saadeti kinnitus email!");
                        finish();
                        firebaseAuth.signOut(); //logid välja, et saaksid valideerida ennast ning uuesti sisse logida siis
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    } else {
                        teade("Kinnitus emaili ei saadetud!");
                    }
                }
            });
        }
    }

private void saadaKasutajaAndmed(){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());
        Userprofiledata userProfileData=new Userprofiledata(_eesNimi,_pereNimi,_email);
        databaseReference.setValue(userProfileData);
        }

public void teade(String message){Toast.makeText(this,message, Toast.LENGTH_SHORT).show();}
}