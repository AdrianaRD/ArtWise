package com.example.artwise;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH_CONNECT = 2;

    //variable de la interfaz
    private Button scanButton;//boton comenzar
    private Button repetirButton;//boton Repetir
    private Button informacion;
    private Button velocidad;

    private String text;
    private String titulo;
    private String descripcion;
    private int conversionRSSI;
    //****
    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mBluetoothReceiver;

    private BluetoothLeScanner bluetoothLeScanner;
    private boolean scanning;
    private Handler handler;
    //to speech variable
    private TextToSpeech tts;
    //array donde guardaremos los beacons leidos
    private ArrayList<String> leidos = new ArrayList<String>();
    private FirebaseFirestore db;

    //permisos
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static String[] PERMISSIONS_LOCATION = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };

    private static final int REQUEST_AUDIO_PERMISSION = 1;

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

        //activacion de ubicacion
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsIntent);
        }


        //activacion de bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // Aquí se solicita el permiso de conexión Bluetooth si aún no se ha concedido.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                        REQUEST_BLUETOOTH_CONNECT);
                return;
            }
            startActivity(enableBtIntent);
        }

        //instancia de la interfaz
        scanButton = findViewById(R.id.btnComenzar);
        repetirButton = findViewById(R.id.btnRepe);
        informacion=findViewById(R.id.btnInformacion);
        velocidad=findViewById(R.id.btnVelocidad);

        //instancia de los bluetooth
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        handler = new Handler();

        // Inicializamos la instancia de TextToSpeech
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
                    int result = tts.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, "Idioma no soportado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error al inicializar TextToSpeech", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //conexion a base de datos
        db = FirebaseFirestore.getInstance();

        db.collection("BEACONS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        checkPermissions();


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
                scaneo();
                break;
            case R.id.btnRepe:
                repeOBRA();
                break;
            case R.id.btnInformacion:
                infoOBRA();
                break;
            case R.id.btnMicro:
                Toast.makeText(MainActivity.this, "boton micro", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnVelocidad:
                Toast.makeText(MainActivity.this, "boton velocidad", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //metodos para solicitar permisos
    private void checkPermissions(){
        int permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    1
            );
        } else if (permission2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_LOCATION,
                    1
            );
        }
    }
    //****************************************************************
    //METODOOO ESCANEOO
    public void scaneo(){
        try {
            //Activación de ubicación
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsIntent);
                return; // Salimos del método porque el usuario aún no ha habilitado la ubicación.
            }

            //Activación de Bluetooth
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
                return;
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Aquí se solicita el permiso de conexión Bluetooth si aún no se ha concedido.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                            REQUEST_BLUETOOTH_CONNECT);
                    return;
                }
                startActivity(enableBtIntent);
                registerBluetoothReceiver(); // Registra el BroadcastReceiver para esperar la activación del Bluetooth.
                return; // Salimos del método porque el usuario aún no ha habilitado Bluetooth.
            }

            // Si llegamos aquí, es porque la ubicación y el Bluetooth están habilitados. Podemos empezar a escanear.
            startScanning();
        } catch (Exception e) {
            // Manejamos la excepción aquí y podemos hacer cualquier cosa, como mostrar un mensaje de error.
            Log.e(TAG, "Error: " + e.getMessage());
            Toast.makeText(this, "Configurando." + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void registerBluetoothReceiver() {
        // Registra el BroadcastReceiver para detectar cuando el Bluetooth esté habilitado.
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mBluetoothReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    if (state == BluetoothAdapter.STATE_ON) {
                        startScanning();
                    }
                }
            }
        };
        registerReceiver(mBluetoothReceiver, filter);
    }

//*********************************************
    public void repeOBRA(){
        if (titulo != null && titulo !="") {
            tts.speak(titulo, TextToSpeech.QUEUE_ADD, null, null);
        }else{
            tts.speak("debes comenzar trayecto.", TextToSpeech.QUEUE_ADD, null, null);
        }
    }

    public void infoOBRA(){
        if (descripcion != null && descripcion !="") {
            tts.speak(descripcion, TextToSpeech.QUEUE_ADD, null, null);
        }else{
            tts.speak("debes comenzar trayecto.", TextToSpeech.QUEUE_ADD, null, null);
        }
    }


//********************************

    private void startScanning() {
        if (!scanning) {
            // Iniciar el escaneo
            scanning = true;
            scanButton.setText("Buscando");
            text = "Comenzamos.";
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //stopScanning();
                }
            }, 0); // Escanear durante 5 segundo
            bluetoothLeScanner.startScan(new BeaconScanCallback());
        } else {
            // Detener el escaneo
            stopScanning();
        }

    }

    private void stopScanning() {
        if (scanning) {
            scanning = false;
            scanButton.setText("Comenzar");
            text = "Viaje terminado.";
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
            leidos.clear();
            titulo="";
            descripcion="";
            bluetoothLeScanner.stopScan(new BeaconScanCallback());
        }
    }


    private class BeaconScanCallback extends ScanCallback {
        private List<ScanFilter> filters;

        public BeaconScanCallback(List<ScanFilter> filters) {
            this.filters = filters;
        }

        public BeaconScanCallback() {
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            ScanRecord scanRecord = result.getScanRecord();
            String nombre=""+result.getDevice().getName();
            String cod=result.getDevice().getAddress();
            int distancia =result.getRssi();


            if(scanButton.getText().equals("Buscando")) {
                if (!leidos.contains(cod)) {
                    Log.d("MainActivity", "Beacon descubierto: " + result.getDevice().getAddress() + " " + result.getDevice().getName() + " " + result.getRssi());

                    // Obtener los datos del documento "cod" de la colección "BEACONS" de Firebase Firestore
                    db.collection("BEACONS")
                            .document(cod)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        // Obtener los valores de los campos "titulo" y "descripcion"
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            titulo = document.getString("titulo");
                                            descripcion = document.getString("descripcion");
                                            conversionRSSI=Integer.parseInt(document.getString("rssi"));

                                            if(distancia>=conversionRSSI && !leidos.contains(cod)){
                                                leidos.add(cod);
                                                tts.speak(titulo, TextToSpeech.QUEUE_ADD, null, null);

                                            }

                                        } else {
                                            Log.d(TAG, "El documento no existe");
                                        }
                                    } else {
                                        Log.d(TAG, "Error al obtener los datos del documento", task.getException());
                                    }
                                }
                            });
                }
            }
        }

    }

}