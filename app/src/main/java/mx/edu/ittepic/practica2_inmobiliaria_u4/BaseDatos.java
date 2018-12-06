package mx.edu.ittepic.practica2_inmobiliaria_u4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatos extends SQLiteOpenHelper {
    public BaseDatos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE PROPIETARIO" +
                "(" +
                "IDP INTEGER PRIMARY KEY NOT NULL," +
                "NOMBRE VARCHAR(200)," +
                "DOMICILIO VARCHAR(400)," +
                "TELEFONO VARCHAR(20)" +
                ")");

        db.execSQL("CREATE TABLE INMUEBLE(" +
                "IDINMUEBLE INTEGER PRIMARY KEY NOT NULL," +
                "DOMICILIO VARCHAR(200)," +
                "PRECIOVENTA FLOAT," +
                "PRECIORENTA FLOAT," +
                "FECHATRANSACCION DATE," +
                "IDP INTEGER," +
                "FOREIGN KEY(IDP) REFERENCES PROPIETARIO(IDP)" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
