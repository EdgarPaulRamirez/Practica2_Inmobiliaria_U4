package mx.edu.ittepic.practica2_inmobiliaria_u4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button botonPropietario, botonInmueble;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonPropietario = findViewById(R.id.botonPropietario);
        botonInmueble = findViewById(R.id.botonInmueble);

        botonPropietario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otraventana = new Intent(MainActivity.this,Main3Activity.class);
                startActivity(otraventana);
            }
        });

        botonInmueble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otraventana2 = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(otraventana2);
            }
        });
    }
}
