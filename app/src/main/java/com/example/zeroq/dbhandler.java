package com.example.zeroq;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class dbhandler extends SQLiteOpenHelper {
    public dbhandler(@Nullable Context context) {
        super(context, "DB_NAME", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String mytable = " CREATE TABLE productsList( codeNumber INTEGER ,name TEXT ,prices INTEGER ,quantity INTEGER); ";
        Log.d("table", mytable);
        db.execSQL(mytable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Boolean addlist(String codeNumber, String name, String prices, String quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("codeNumber", codeNumber);
        values.put("name", name);
        values.put("prices", prices);
        values.put("quantity",quantity);
        Long res=  db.insert("productsList",null,values);
        if(res==-1) {
            return false;
        }else {
            return true;
        }
    }

    public void delete (){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from  productsList");
        db.close();
    }
    public Cursor getdata (){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM productsList" ,null);
        return  cursor;

    }
    public boolean  update(String codeno,String price, String quantities) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put("prices"   ,price);
        values.put("quantity",quantities);
        db.update("productsList",values,"codeNumber = ?",new String[] {codeno});
        return true;
    }

}
