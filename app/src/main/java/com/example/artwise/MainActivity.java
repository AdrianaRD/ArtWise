package com.example.artwise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home: {
                        Toast.makeText(MainActivity.this, "Se ha seleccionado inicio ", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.nosotros: {
                        Toast.makeText(MainActivity.this, "Se ha seleccionado sobre nosotros", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.configuracion: {
                        Toast.makeText(MainActivity.this, "Se ha seleccionado configuraciones", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.Error: {
                        Toast.makeText(MainActivity.this, "Se ha seleccionado errores", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.Ayuda: {
                        Toast.makeText(MainActivity.this, "Se ha seleccionado ayuda", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    //metodo on click para los botones
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.btnComenzar:
                Toast.makeText(MainActivity.this, "boton comenzar", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnRepetir:
                Toast.makeText(MainActivity.this, "boton repetir", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnInfo:
                Toast.makeText(MainActivity.this, "boton informacion", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnMicro:
                Toast.makeText(MainActivity.this, "boton micro", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnVelocidad:
                Toast.makeText(MainActivity.this, "boton velocidad", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}