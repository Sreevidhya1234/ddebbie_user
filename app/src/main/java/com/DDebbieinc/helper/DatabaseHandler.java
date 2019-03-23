package com.DDebbieinc.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.DDebbieinc.entity.PromoNotification;

import java.util.ArrayList;

/**
 * Created by appsplanet on 17/8/16.
 */


public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "promoManager";

    // promocode table name
    private static final String TABLE_PROMOCODE = "promocode";

  /*  {
        "action":"PROMO_PUSH",
            "promo_code":"DDPC20",
            "promo_valid_from":"17 Aug, 2016",
            "promo_valid_to":"18 Aug, 2016",
            "title":"Promo codeDDPC20",
            "body":"Use promo code DDPC20 to get discount of 20",
            "link":"",
            "image":""
    }*/
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_PROMOCODE = "promo_code";
    private static final String KEY_VALID_FROM = "promo_valid_from";
    private static final String KEY_VALID_TO = "promo_valid_to";
    private static final String KEY_BODY = "body";
    private static final String KEY_LINK = "link";
    private static final String KEY_IMAGE = "image";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROMOCEDE_TABLE = "CREATE TABLE " + TABLE_PROMOCODE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_PROMOCODE + " TEXT," + KEY_VALID_FROM + " TEXT,"
                + KEY_VALID_TO + " TEXT," + KEY_BODY + " TEXT,"
                + KEY_LINK + " TEXT,"  + KEY_IMAGE + " TEXT"+ ")";
        db.execSQL(CREATE_PROMOCEDE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROMOCODE);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void addPromo(PromoNotification promoNotification) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PROMOCODE, promoNotification.getPromo_code());
        values.put(KEY_VALID_FROM, promoNotification.getPromo_valid_from());
        values.put(KEY_VALID_TO, promoNotification.getPromo_valid_to());
        values.put(KEY_TITLE, promoNotification.getTitle());
        values.put(KEY_BODY, promoNotification.getBody());
        values.put(KEY_LINK, promoNotification.getLink());
        values.put(KEY_IMAGE, promoNotification.getImage());


        // Inserting Row
        db.insert(TABLE_PROMOCODE, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    PromoNotification getPromo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PROMOCODE, new String[] { KEY_ID,KEY_PROMOCODE, KEY_VALID_FROM, KEY_VALID_TO,
                        KEY_TITLE, KEY_BODY,KEY_LINK,  KEY_IMAGE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        PromoNotification promoNotification = new PromoNotification(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
        // return promo
        return promoNotification;
    }

    // Getting All Promo
    public ArrayList<PromoNotification> getAllPromo() {
        ArrayList<PromoNotification> promotList = new ArrayList<PromoNotification>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PROMOCODE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PromoNotification promoNotification = new PromoNotification();
                promoNotification.set_id(Integer.parseInt(cursor.getString(0)));
                promoNotification.setTitle(cursor.getString(1));
                promoNotification.setBody(cursor.getString(2));
                promoNotification.setImage(cursor.getString(3));
                promoNotification.setLink(cursor.getString(4));
                promoNotification.setPromo_code(cursor.getString(5));
                promoNotification.setPromo_valid_from(cursor.getString(6));
                promoNotification.setPromo_valid_to(cursor.getString(7));

                // Adding contact to list
                promotList.add(promoNotification);
            } while (cursor.moveToNext());
        }

        // return contact list
        return promotList;
    }

    // Updating single promo
    public int updatePromo(PromoNotification promoNotification) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, promoNotification.getTitle());
        values.put(KEY_BODY, promoNotification.getBody());
        values.put(KEY_IMAGE, promoNotification.getImage());
        values.put(KEY_LINK, promoNotification.getLink());
        values.put(KEY_PROMOCODE, promoNotification.getPromo_code());
        values.put(KEY_VALID_FROM, promoNotification.getPromo_valid_from());
        values.put(KEY_VALID_TO, promoNotification.getPromo_valid_to());
        // updating row
        return db.update(TABLE_PROMOCODE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(promoNotification.get_id()) });
    }

    // Deleting single promo
    public void deletePromo(PromoNotification promoNotification) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROMOCODE, KEY_ID + " = ?",
                new String[] { String.valueOf(promoNotification.get_id()) });
        db.close();
    }


    // Getting promo Count
    public int getPromoCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PROMOCODE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}