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

public class Main2Activity extends AppCompatActivity {
    EditText IDInmueble, domicilioInmueble, precioVentaInmueble, precioRentaInmueble, fechaTransaccionInmueble, idpInmueble;
    Button insertar, consultar, eliminar, actualizar;
    BaseDatos base;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        IDInmueble = findViewById(R.id.IDInmueble);
        domicilioInmueble = findViewById(R.id.domicilioInmueble);
        precioVentaInmueble = findViewById(R.id.precioVentaInmueble);
        precioRentaInmueble = findViewById(R.id.precioRentaInmueble);
        fechaTransaccionInmueble = findViewById(R.id.fechaTransaccionInmueble);
        idpInmueble = findViewById(R.id.idpInmueble);

        insertar = findViewById(R.id.insertarInmueble);
        consultar = findViewById(R.id.consultarInmueble);
        eliminar = findViewById(R.id.eliminarInmueble);
        actualizar = findViewById(R.id.actualizarInmueble);

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

            String SQL = "UPDATE INMUEBLE SET DOMICILIO='" + domicilioInmueble.getText().toString() + "', PRECIOVENTA=" + precioVentaInmueble.getText().toString() + ", PRECIORENTA=" + precioRentaInmueble.getText().toString()+",FECHATRANSACCION='"+fechaTransaccionInmueble.getText().toString()+"' WHERE IDINMUEBLE ="+idpInmueble.getText().toString();

            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this,"Se actualizó CORRECTAMENTE",Toast.LENGTH_LONG).show();

        }catch (SQLiteException e){
            Toast.makeText(this,"ERROR: No se pudo actualizar",Toast.LENGTH_LONG).show();
        }
        habilitarBotonesYLimpiarCampos();
    }
    private void habilitarBotonesYLimpiarCampos(){
        IDInmueble.setText("");
        domicilioInmueble.setText("");
        precioVentaInmueble.setText("");
        precioRentaInmueble.setText("");
        fechaTransaccionInmueble.setText("");
        insertar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        actualizar.setText("");
        IDInmueble.setEnabled(true);
    }


    private void eliminarIdtodo(String idEliminar) {

        try{
            SQLiteDatabase tabla = base.getReadableDatabase();

            String SQL = "DELETE FROM INMUEBLE WHERE IDINMUEBLE =" + idEliminar;
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
                            Toast.makeText(Main2Activity.this,"Favor de ingresar el ID a eliminar: ",Toast.LENGTH_LONG).show();
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

            String SQL = "SELECT *FROM INMUEBLE WHERE IDINMUEBLE ="+idaBuscar;

            Cursor resultado = tabla.rawQuery(SQL,null);
            if(resultado.moveToFirst()){//mover le primer resultado obtenido de la consulta
                //si hay resulta´do
                if(origen==3){
                    //se consulto para borrar
                    String dato = idaBuscar+"&"+ resultado.getString(1)+"&"+resultado.getString(2)+
                            "&"+resultado.getString(3)+resultado.getString(4);
                    confirmacionEliminacion(dato);
                    return;
                }

                IDInmueble.setText(resultado.getString(0));
                domicilioInmueble.setText(resultado.getString(1));
                precioVentaInmueble.setText(resultado.getString(2));
                precioRentaInmueble.setText(resultado.getString(3));
                fechaTransaccionInmueble.setText(resultado.getString(4));
                if(origen==2){
                    //modificar
                    insertar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    actualizar.setText("CONFIRMAR ACTUALIZACION");
                    IDInmueble.setEnabled(false);
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
        alerta.setTitle("ATENCIÓN").setMessage("Deseas eliminar el platillo: "+nombre)
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

            String SQL = "INSERT INTO INMUEBLE VALUES(1,'%2', 3, 4,'%5', '%6')";
            SQL = SQL.replace("1", IDInmueble.getText().toString());
            SQL = SQL.replace("%2", domicilioInmueble.getText().toString());
            SQL = SQL.replace("3", precioVentaInmueble.getText().toString());
            SQL = SQL.replace("4", precioRentaInmueble.getText().toString());
            SQL = SQL.replace("%5", fechaTransaccionInmueble.getText().toString());
            SQL = SQL.replace("%6", idpInmueble.getText().toString());
            tabla.execSQL(SQL);

            Toast.makeText(this,"La insercción se realizó correctamente",Toast.LENGTH_LONG).show();
            tabla.close();

        }catch (SQLiteException e){

            Toast.makeText(this,"No se pudo realizar la insercción",Toast.LENGTH_LONG).show();

        }
    }
}
