package com.example.opilane.snake_game;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    EditText email, salasõna;
    Button btn_login;
    TextView katsed, registreeri, unustatud;
    // kui sisestad kolm korda, siis küsib "kas unustasid parooli?"

    int loendaja = 3;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        salasõna = findViewById(R.id.password);
        btn_login = findViewById(R.id.btnLogin);
        unustatud = findViewById(R.id.unustatud);
        registreeri = findViewById(R.id.registreerimine);
        katsed = findViewById(R.id.katsed);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser kasutaja = firebaseAuth.getCurrentUser();
        if (kasutaja != null) {
            finish();
            startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
        }
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valideeri(email.getText().toString(), salasõna.getText().toString());

            }
        });
          registreeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registreerimine = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registreerimine);
            }
        });


        unustatud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent unustatud_sala = new Intent(LoginActivity.this, PasswordActivity.class);
                startActivity(unustatud_sala);
            }
        });
    }
    private void valideeri(String email, String salasõna) {
        progressDialog.setMessage("Andmete edastamisega läheb aega, palun kannatust!");
        progressDialog.show();


        firebaseAuth.signInWithEmailAndPassword(email, salasõna).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    kontrolliEmaili();
                } else {
                    teade("Sisse logimine ebaõnnestus!");
                    loendaja --;
                    progressDialog.dismiss();
                    if (loendaja < 3) {
                        katsed.setText("Katseid on jäänud veel " + String.valueOf(loendaja));
                    }
                    if (loendaja == 0) {
                        btn_login.setEnabled(false);
                        katsed.setText("Võta ühendust administraatoriga");
                    }
                }
            }
        });
    }

    private void kontrolliEmaili() {
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        boolean emailiKontroll = firebaseUser.isEmailVerified();
        if (emailiKontroll) {
            finish();
            startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
        } else {
            teade("Kinnitage oma eposti aadress!");
        }
    }


    public void teade(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
