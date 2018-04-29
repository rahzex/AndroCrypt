package com.noobs.rahulpal.androcrypt;

/**
 * Created by Rahul Pal on 4/23/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

class TableController extends DatabaseHandler {

    TableController(Context context) {

        super(context);
    }

    boolean addNewVault(String finalKey,String vaultName,String algorithm) {

        ContentValues values = new ContentValues();
        values.put("Vault_Name", vaultName);
        values.put("KEY", finalKey);
        values.put("Algorithm", algorithm);

        SQLiteDatabase db = this.getWritableDatabase();

        boolean createSuccessful = db.insert(DATABASE_NAME, null, values) > 0;
        db.close();

        return createSuccessful;
    }

    Cursor Read() {

        String sql = "SELECT Vault_Name FROM "+ DATABASE_NAME +" ORDER BY id";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null)
            cursor.moveToFirst();
        cursor.close();
        db.close();

        return cursor;
    }

    String getAlgo(String VaultName){
        String sql = "SELECT Algorithm FROM "+ DATABASE_NAME +" WHERE Vault_Name=" +"'"+ VaultName +"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        String algo = cursor.getString(0);

        return algo;
    }

    public int Count() {

        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "SELECT id FROM " + DATABASE_NAME;
        int recordCount = db.rawQuery(sql, null).getCount();
        db.close();

        return recordCount;

    }

    boolean vaultLogin(String vaultName,String password) {

        String sql = "SELECT * FROM "+ DATABASE_NAME +" WHERE Vault_Name=" +"'"+ vaultName +"'"+ " AND KEY=" +"'"+ password +"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        boolean LoginSuccess;

        if(cursor.getCount()>0)
            LoginSuccess = true;
        else
            LoginSuccess = false;

        return LoginSuccess;
    }

    boolean ifVaultNameAlreadyExists(String vaultName){

        String sql = "SELECT * FROM "+ DATABASE_NAME +" WHERE Vault_Name= '" + vaultName +"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        boolean ifExits;

        if(cursor.getCount()>0)
            ifExits = true;
        else
            ifExits = false;

        return ifExits;
    }

}

