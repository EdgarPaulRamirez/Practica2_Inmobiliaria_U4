package mx.edu.ittepic.practica2_inmobiliaria_u4;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main3Activity extends AppCompatActivity {
    EditText identificador, nombre, domicilio, telefono;
    Button insertar, consultar, eliminar, actualizar;
    BaseDatos base;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

         identificador =  findViewById(R.id.identificador);
        nombre = findViewById(R.id.nombre);
        domicilio = findViewById(R.id.domicilio);
        telefono = findViewById(R.id.telefono);

        insertar = findViewById(R.id.insertar);
        consultar = findViewById(R.id.consultar);
        eliminar = findViewById(R.id.eliminar);
        actualizar = findViewById(R.id.actualizar);

        base = new BaseDatos(this,"inmobiliaria",null,1);

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoInsertar();
            }
        });

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(1);
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actualizar.getText().toString().startsWith("CONFIRMAR ACTUALIZACION")){
                    confirmacionActualizacion();
                }else{
                    pedirID(2);
                }

            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(3);
            }
        });

    }

    private void confirmacionActualizacion(){
        AlertDialog.Builder confir = new AlertDialog.Builder(this);
        confir.setTitle("ATENCIÓN").setMessage("¿Seguro que deseeas realiar los cambios?")
                .setPositiveButton("Si, estoy seguro", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        aplicarActualizar();
                        dialog.dismiss();
                    }
                }).setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesYLimpiarCampos();
                dialog.cancel();
            }
        }).show();
    }
    private void aplicarActualizar(){
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL = "UPDATE PROPIETARIO SET NOMBRE='" + nombre.getText().toString() + "', DOMICILIO='" + domicilio.getText().toString() + "', TELEFONO='" + telefono.getText().toString()+"'WHERE IDP="+identificador.getText().toString();

            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this,"Se actualizó CORRECTAMENTE",Toast.LENGTH_LONG).show();

        }catch (SQLiteException e){
            Toast.makeText(this,"ERROR: No se pudo actualizar",Toast.LENGTH_LONG).show();
        }
        habilitarBotonesYLimpiarCampos();
    }
    private void habilitarBotonesYLimpiarCampos(){
        identificador.setText("");
        nombre.setText("");
        domicilio.setText("");
        telefono.setText("");
        insertar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        actualizar.setText("");
        identificador.setEnabled(true);
    }


    private void eliminarIdtodo(String idEliminar) {

        try{
            SQLiteDatabase tabla = base.getReadableDatabase();

            String SQL = "DELETE FROM PROPIETARIO WHERE IDP=" + idEliminar;
            tabla.execSQL(SQL);
            tabla.close();

            Toast.makeText(this, "Se eleminó CORRECTAMENTE ", Toast.LENGTH_LONG).show();
        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR: No se pudo Eliminar", Toast.LENGTH_LONG).show();
        }

    }

    /////////////////////////para pedir el numero
    private void pedirID(final int origen){
        final EditText pidoID = new EditText(this);
        pidoID.setInputType(InputType.TYPE_CLASS_NUMBER);
        pidoID.setHint("Valor entero mayor de 0");
        String mensaje ="Escriba el id a buscar";
        String mensajeTitulo = "Buscando...";

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);

        if(origen ==2){
            mensaje ="Ecriba el id a modificar";
            mensajeTitulo = "Modificando...";
        }
        if(origen ==3){
            mensaje ="Favor de ingresar el ID a eliminar:";
            mensajeTitulo = "Eliminando...";
        }

        alerta.setTitle(mensajeTitulo).setMessage(mensaje)
                .setView(pidoID)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(pidoID.getText().toString().isEmpty()){
                            Toast.makeText(Main3Activity.this,"Favor de ingresar el ID a eliminar: ",Toast.LENGTH_LONG).show();
                            return;
                        }
                        buscarDato(pidoID.getText().toString(), origen);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar",null).show();
    }
    //////////////////////////////////////////eliminar
    private void buscarDato(String idaBuscar, int origen){
        try{

            SQLiteDatabase tabla = base.getReadableDatabase();

            String SQL = "SELECT *FROM PROPIETARIO WHERE IDP="+idaBuscar;

            Cursor resultado = tabla.rawQuery(SQL,null);
            if(resultado.moveToFirst()){//mover le primer resultado obtenido de la consulta
                //si hay resulta´do
                if(origen==3){
                    //se consulto para borrar
                    String dato = idaBuscar+"&"+ resultado.getString(1)+"&"+resultado.getString(2)+
                            "&"+resultado.getString(3);
                    confirmacionEliminacion(dato);
                    return;
                }

                identificador.setText(resultado.getString(0));
                nombre.setText(resultado.getString(1));
                domicilio.setText(resultado.getString(2));
                telefono.setText(resultado.getString(3));
                if(origen==2){
                    //modificar
                    insertar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    actualizar.setText("CONFIRMAR ACTUALIZACION");
                    identificador.setEnabled(false);
                }
            }else {
                //no hay resultado!
                Toast.makeText(this,"ERROR: No se encontró el resultado",Toast.LENGTH_LONG).show();
            }
            tabla.close();

        }catch (SQLiteException e){
            Toast.makeText(this,"ERROR: No se pudo realizar la busqueda",Toast.LENGTH_LONG).show();
        }
    }

    private void confirmacionEliminacion(String dato) {


        String datos[] = dato.split("&");
        final String id = datos[0];
        String nombre = datos[1];

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCIÓN").setMessage("Deseas eliminar el propietario con el nombre: "+nombre)
                .setPositiveButton("Si, estoy de acuerdo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        eliminarIdtodo(id);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar",null).show();
    }


    private void codigoInsertar(){
        try {

            //metodo que compete a la inserccion,
            SQLiteDatabase tabla = base.getWritableDatabase();
                /*String SQL= "INSERT INTO PERSONA VALUES("+identificacion.getText().toString()+",'"+nombre.getText().toString()
                +"',"+edad.getText().toString()+",'"+genero.getText().toString()+"')";*/

            String SQL = "INSERT INTO PROPIETARIO VALUES(1,'%2','%3','%4')";
            SQL = SQL.replace("1", identificador.getText().toString());
            SQL = SQL.replace("%2", nombre.getText().toString());
            SQL = SQL.replace("%3", domicilio.getText().toString());
            SQL = SQL.replace("%4", telefono.getText().toString());
            tabla.execSQL(SQL);

            Toast.makeText(this,"Si se pudo",Toast.LENGTH_LONG).show();
            tabla.close();
            habilitarBotonesYLimpiarCampos();

        }catch (SQLiteException e){

            Toast.makeText(this,"No se pudo realizar la inserción",Toast.LENGTH_LONG).show();

        }
    }
}
