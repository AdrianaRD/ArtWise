package com.example.artwise;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PF_Main extends AppCompatActivity {

    RecyclerView recyclerView;
    List<PF_Preguntas> pf_preguntasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pf_main);

        recyclerView = findViewById(R.id.recyclerView);

        initData();
        setRecyclerView();
    }

    private void setRecyclerView() {
        PF_Adapter pf_adapter = new PF_Adapter(pf_preguntasList);
        recyclerView.setAdapter(pf_adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void initData() {

        pf_preguntasList = new ArrayList<>();
        pf_preguntasList.add(new PF_Preguntas("¿Necesito tener una conexión a Internet para usar la aplicación en el museo?",
                "No es necesario tener conexión a internet para utilizar la aplicación. Sin embargo, recomendamos conectarse a internet de vez en cuando para actualizar la base de datos correctamente."));
        pf_preguntasList.add(new PF_Preguntas("¿Cómo puedo saber qué información se proporciona en la aplicación sobre cada obra de arte?",
                "La aplicación tiene una configuración estándar que muestra información básica sobre una obra cuando se detecta un beacon. Al hacer clic en el botón '+ Información', se puede obtener información más detallada y curiosa sobre la obra."));
        pf_preguntasList.add(new PF_Preguntas("¿La aplicación está disponible en varios idiomas?",
                "Actualmente solo está en español, pero en futuras actualizaciones estará disponible en otros idiomas como inglés, francés, portugués y alemán."));
        pf_preguntasList.add(new PF_Preguntas("¿Cómo puedo reportar problemas técnicos o errores en la aplicación? ",
                "En el menú desplegable está la opción 'Errores', que permite a los usuarios reportar errores encontrados en la aplicación mediante correo electrónico. "));

        pf_preguntasList.add(new PF_Preguntas("¿Cómo puedo encontrar los beacons en el museo para poder usar la aplicación?",
                "Los beacons están ubicados estratégicamente junto a cada obra, lo que permite que la información sobre las obras se lea a medida que se visite el museo."));
        pf_preguntasList.add(new PF_Preguntas("¿La aplicación proporciona información sobre la historia y la cultura detrás de las obras de arte?",
                "Si, al hacer clic en el botón '+ Información', se mostrará información más detallada sobre cada obra. "));
        pf_preguntasList.add(new PF_Preguntas("¿La aplicación requiere que active la ubicación en mi dispositivo móvil para funcionar?  ",
                "Si, para el correcto funcionamiento de la aplicación es imprescindible tanto la conectividad de la ubicación como la del bluetooth."));
    }
}
