package com.example.logintarea;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;
    private Button buttonLogin;
    private Button buttonCreateAccount;
    private CheckBox checkboxKeepLoggedIn;
    private TextView textViewTestUserInfo;
    private Button buttonTestLogin;

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_KEEP_LOGGED_IN = "keepLoggedIn";

    private SharedPreferences sharedPreferences;
    private DatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        checkboxKeepLoggedIn = findViewById(R.id.mantenerSesion);
        textViewTestUserInfo = findViewById(R.id.textViewTestUserInfo);
        buttonTestLogin = findViewById(R.id.buttonTestLogin);

        loadSavedCredentialsIfKept();
        checkSavedLogin();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateLoginWithDatabase();
            }
        });

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Registrar.class);
                startActivity(intent);
            }
        });

        buttonTestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextUsername.setText("admin");
                editTextPassword.setText("pass123");
                checkboxKeepLoggedIn.setChecked(true);
                validateLoginWithDatabase();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadSavedCredentialsIfKept() {
        boolean keepLoggedIn = sharedPreferences.getBoolean(PREF_KEEP_LOGGED_IN, false);
        if (keepLoggedIn) {
            String savedUsername = sharedPreferences.getString(PREF_USERNAME, "");
            String savedPassword = sharedPreferences.getString(PREF_PASSWORD, "");
            editTextUsername.setText(savedUsername);
            editTextPassword.setText(savedPassword);
            checkboxKeepLoggedIn.setChecked(true);
        }
    }

    private void checkSavedLogin() {
        boolean keepLoggedIn = sharedPreferences.getBoolean(PREF_KEEP_LOGGED_IN, false);
        String savedUsername = sharedPreferences.getString(PREF_USERNAME, null);
        String savedPassword = sharedPreferences.getString(PREF_PASSWORD, null);

        if (keepLoggedIn && savedUsername != null && savedPassword != null) {
            Log.i(TAG, "Mantener sesión activado para: " + savedUsername);
            Toast.makeText(MainActivity.this, "Bienvenido de vuelta, " + savedUsername + "!", Toast.LENGTH_SHORT).show();
            navigateToHome();
        } else {
            Log.i(TAG, "No hay sesión activa guardada o 'mantener sesión' está desactivado.");
        }
    }

    private void validateLoginWithDatabase() {
        String enteredUsername = editTextUsername.getText() != null ? editTextUsername.getText().toString().trim() : "";
        String enteredPassword = editTextPassword.getText() != null ? editTextPassword.getText().toString() : "";
        boolean keepLoggedIn = checkboxKeepLoggedIn.isChecked();

        if (enteredUsername.isEmpty()) {
            editTextUsername.setError("El nombre de usuario es requerido");
            editTextUsername.requestFocus();
            return;
        }
        if (enteredPassword.isEmpty()) {
            editTextPassword.setError("La contraseña es requerida");
            editTextPassword.requestFocus();
            return;
        }

        if (dbHelper.checkUser(enteredUsername, enteredPassword)) {
            Log.i(TAG, "Login exitoso para: " + enteredUsername);
            Toast.makeText(MainActivity.this, getString(R.string.acceso_concedido), Toast.LENGTH_SHORT).show();
            saveOrClearCredentials(enteredUsername, enteredPassword, keepLoggedIn);
            navigateToHome();
        } else {
            Log.w(TAG, "Login fallido para: " + enteredUsername);
            Toast.makeText(MainActivity.this, getString(R.string.datos_incorrectos), Toast.LENGTH_SHORT).show();
            editTextPassword.setText("");
            saveOrClearCredentials(null, null, false);
        }
    }

    private void saveOrClearCredentials(String username, String password, boolean keepLoggedIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (keepLoggedIn && username != null && password != null) {
            Log.i(TAG, "Guardando credenciales para: " + username + " con 'mantener sesión'.");
            editor.putBoolean(PREF_KEEP_LOGGED_IN, true);
            editor.putString(PREF_USERNAME, username);
            editor.putString(PREF_PASSWORD, password);
        } else {
            Log.i(TAG, "Limpiando credenciales guardadas.");
            editor.remove(PREF_KEEP_LOGGED_IN);
            editor.remove(PREF_USERNAME);
            editor.remove(PREF_PASSWORD);
        }
        editor.apply();
    }

    private void navigateToHome() {
        Intent intent = new Intent(MainActivity.this, Home.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
