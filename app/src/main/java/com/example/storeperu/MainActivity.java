package com.example.storeperu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class MainActivity extends AppCompatActivity {

    EditText edtUser, edtPassword;
    Button btnLogin;

    private void loadUI(){
        edtUser = findViewById(R.id.edtUser);
        edtPassword = findViewById(R.id.edtPassword);

        btnLogin = findViewById(R.id.btnLogin);
    }

    private void resetUI(){
        edtUser.setText(null);
        edtPassword.setText(null);
    }

    private void iniciarSesion(){
        String user, password;
        user = edtUser.getText().toString();
        password = edtPassword.getText().toString();

        if(user.isEmpty()){
            edtUser.setError("Requerido");
            edtUser.requestFocus();
            return;
        }

        if(password.isEmpty()){
            edtPassword.setError("Requerido");
            edtPassword.requestFocus();
            return;
        }

        if(user.equals("admin") && password.equals("123")){
            resetUI();
            Intent intent = new Intent(getApplicationContext(), Indice.class);
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(), "Credenciales invalidas", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        loadUI();
        btnLogin.setOnClickListener( v -> { iniciarSesion(); });
    }
}