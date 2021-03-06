package com.mdtt.scott.treasuretrackerfordetectorists;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Scott on 1/25/2018.
 */

@SuppressWarnings("WeakerAccess")
public class MySQliteHelper extends SQLiteOpenHelper {

    // Database Version
    //58 is live version so don't change this without migration in place
    private static final int DATABASE_VERSION = 58;
    // Database Name
    private static final String DATABASE_NAME = "findsDB";
    // Treasures table name
    private static final String TABLE_TREASURE = "Treasure";

    // Treasure Table Columns names
    private static final String colTreasureID="TreasureID";
    private static final String colTreasureType="TreasureType";
    private static final String colTreasureCountry="TreasureCountry";
    private static final String colTreasureDenomination="TreasureDenomination";
    private static final String colTreasureSeries="TreasureSeries";
    private static final String colTreasureName="TreasureName";
    private static final String colTreasureYear="TreasureYear";
    private static final String colTreasureMint="TreasureMint";
    private static final String colTreasureMaterial="TreasureMaterial";
    private static final String colTreasureWeight="TreasureWeight";
    private static final String colTreasureLocationFound="TreasureLocationFound";
    private static final String colTreasureDateFound="TreasureDateFound";
    private static final String colTreasureInfo="TreasureInfo";
    private static final String colTreasurePhotoPath="TreasurePhotoPath";

    // Clad table name
    private static final String TABLE_CLAD = "Clad";

    // Clad Table Columns names
    private static final String colCladID="CladID";
    private static final String colCladCurrency="CladCurrency";
    private static final String colCladAmount="CladAmount";
    private static final String colCladLocationFound="CladLocationFound";
    private static final String colCladDateFound="CladDateFound";


    public MySQliteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        //Log.d("myTag", "WE RECREATED DATABASE TABLES!");

        //creates treasure table
        String createTreasureTable = "CREATE TABLE "+TABLE_TREASURE +
                " ( "+colTreasureID+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                colTreasureType+" TEXT NOT NULL," +
                colTreasureCountry+" TEXT," +
                colTreasureDenomination+" TEXT," +
                colTreasureSeries+" TEXT," +
                colTreasureName+" TEXT," +
                colTreasureYear+" TEXT," +
                colTreasureMint+" TEXT," +
                colTreasureMaterial+" TEXT," +
                colTreasureWeight+" TEXT," +
                colTreasureLocationFound+" TEXT," +
                colTreasureDateFound+" TEXT," +
                colTreasureInfo+" TEXT," +
                colTreasurePhotoPath+" TEXT)";

        //Log.d("myTag", createTreasureTable);
        db.execSQL(createTreasureTable);

        //creates treasure table
        String createCladTable = "CREATE TABLE "+TABLE_CLAD +
                " ( "+colCladID+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                colCladCurrency+" TEXT NOT NULL," +
                colCladAmount+" DOUBLE NOT NULL," +
                colCladLocationFound+" TEXT," +
                colCladDateFound+" TEXT)";

        //Log.d("myTag", createCladTable);
        db.execSQL(createCladTable);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if(oldVersion <= 58)
        {
            //db.execSQL("DROP TABLE IF EXISTS "+TABLE_TREASURE);
            //db.execSQL("DROP TABLE IF EXISTS "+TABLE_CLAD);
        }
        onCreate(db);
    }

    //query used to return id,year,denomination,foundyear,photopath of all coins to populate grid view.
    //paramater determines sorting: AddDate, TreasureYear, TreasureFoundYear
    public ArrayList<Treasure> getAllCoins(String sortType)
    {
        ArrayList<Treasure> treasureList = new ArrayList<>();
        String selectQuery;
        if(sortType.equals("TreasureYear"))
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureYear+","+colTreasureSeries+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+","+colTreasureCountry+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='coin' ORDER BY (CASE WHEN "+sortType+" IS \"\" THEN 1 ELSE 0 END), "+sortType;
        }
        else if(sortType.equals("TreasureLocationFound"))
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureYear+","+colTreasureSeries+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+","+colTreasureCountry+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='coin' ORDER BY (CASE WHEN "+sortType+" IS \"\" THEN 1 ELSE 0 END), "+"lower("+sortType+")";
        }
        else if(sortType.equals("TreasureCountry"))
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureYear+","+colTreasureSeries+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+","+colTreasureCountry+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='coin' ORDER BY "+"lower("+sortType+") ASC";
        }
        else if(sortType.equals("TreasureDateFound"))
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureYear+","+colTreasureSeries+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+","+colTreasureCountry+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='coin' ORDER BY "+sortType+" DESC";
        }
        //most recently added
        else
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureYear+","+colTreasureSeries+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+","+colTreasureCountry+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='coin' ORDER BY "+sortType+" DESC";
        }

        //Log.d("myTag", selectQuery);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Log.d("myTag", "query retrieved.");

        if(cursor.moveToFirst()){
            do {
                treasureList.add(new Treasure(cursor.getInt(cursor.getColumnIndex(colTreasureID)), null, cursor.getString(cursor.getColumnIndex(colTreasureCountry)), null, cursor.getString(cursor.getColumnIndex(colTreasureSeries)), null, cursor.getString(cursor.getColumnIndex(colTreasureYear)), null, null,null ,cursor.getString(cursor.getColumnIndex(colTreasureLocationFound)), cursor.getString(cursor.getColumnIndex(colTreasureDateFound)), null, cursor.getString(cursor.getColumnIndex(colTreasurePhotoPath))));
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return treasureList;
    }

    //query used to return id,year,name,foundyear,photopath of all tokens to populate grid view.
    //paramater determines sorting
    public ArrayList<Treasure> getAllTokens(String sortType)
    {
        ArrayList<Treasure> treasureList = new ArrayList<>();
        String selectQuery;
        if(sortType.equals("TreasureYear"))
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureYear+","+colTreasureName+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='token' ORDER BY (CASE WHEN "+sortType+" IS \"\" THEN 1 ELSE 0 END), "+sortType;
        }
        else if(sortType.equals("TreasureName"))
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureYear+","+colTreasureName+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='token' ORDER BY (CASE WHEN "+sortType+" IS \"\" THEN 1 ELSE 0 END), "+"lower("+sortType+")";
        }
        else if(sortType.equals("TreasureLocationFound"))
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureYear+","+colTreasureName+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='token' ORDER BY (CASE WHEN "+sortType+" IS \"\" THEN 1 ELSE 0 END), "+"lower("+sortType+")";
        }
        //treasure date found, most recently added
        else
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureYear+","+colTreasureName+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='token' ORDER BY "+sortType+" DESC";
        }

        //Log.d("myTag", selectQuery);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Log.d("myTag", "query retrieved.");

        if(cursor.moveToFirst()){
            do {
                treasureList.add(new Treasure(cursor.getInt(cursor.getColumnIndex(colTreasureID)), null, null, null, null, cursor.getString(cursor.getColumnIndex(colTreasureName)), cursor.getString(cursor.getColumnIndex(colTreasureYear)), null, null,null ,cursor.getString(cursor.getColumnIndex(colTreasureLocationFound)), cursor.getString(cursor.getColumnIndex(colTreasureDateFound)), null, cursor.getString(cursor.getColumnIndex(colTreasurePhotoPath))));
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return treasureList;
    }

    //query used to return id,name,material,foundyear,photopath of all jewelry to populate grid view.
    //paramater determines sorting
    public ArrayList<Treasure> getAllJewelry(String sortType)
    {
        ArrayList<Treasure> treasureList = new ArrayList<>();
        String selectQuery;

        if(sortType.equals("TreasureMaterial"))
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureMaterial+","+colTreasureName+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+","+colTreasureWeight+","+colTreasureYear+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='jewelry' ORDER BY lower("+sortType+") ASC";
        }
        else if(sortType.equals("TreasureWeight"))
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureMaterial+","+colTreasureName+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+","+colTreasureWeight+","+colTreasureYear+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='jewelry' ORDER BY (CASE WHEN "+sortType+" IS \"\" THEN 1 ELSE 0 END), "+sortType;
        }
        else if(sortType.equals("TreasureYear"))
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureMaterial+","+colTreasureName+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+","+colTreasureWeight+","+colTreasureYear+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='jewelry' ORDER BY (CASE WHEN "+sortType+" IS \"\" THEN 1 ELSE 0 END), "+sortType;
        }
        else if(sortType.equals("TreasureLocationFound"))
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureMaterial+","+colTreasureName+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+","+colTreasureWeight+","+colTreasureYear+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='jewelry' ORDER BY (CASE WHEN "+sortType+" IS \"\" THEN 1 ELSE 0 END), lower("+sortType+")";
        }
        //treasure date found, most recently added
        else
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureMaterial+","+colTreasureName+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+","+colTreasureWeight+","+colTreasureYear+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='jewelry' ORDER BY "+sortType+" DESC";
        }

        //Log.d("myTag", selectQuery);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Log.d("myTag", "query retrieved.");

        if(cursor.moveToFirst()){
            do {
                treasureList.add(new Treasure(cursor.getInt(cursor.getColumnIndex(colTreasureID)), null, null, null, null, cursor.getString(cursor.getColumnIndex(colTreasureName)), cursor.getString(cursor.getColumnIndex(colTreasureYear)), null, cursor.getString(cursor.getColumnIndex(colTreasureMaterial)),cursor.getString(cursor.getColumnIndex(colTreasureWeight)) ,cursor.getString(cursor.getColumnIndex(colTreasureLocationFound)), cursor.getString(cursor.getColumnIndex(colTreasureDateFound)), null, cursor.getString(cursor.getColumnIndex(colTreasurePhotoPath))));
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return treasureList;
    }

    //query used to return id,name,material,foundyear,photopath of all relics to populate grid view.
    //paramater determines sorting
    public ArrayList<Treasure> getAllRelics(String sortType)
    {
        ArrayList<Treasure> treasureList = new ArrayList<>();
        String selectQuery;

        if(sortType.equals("TreasureYear") || sortType.equals("TreasureLocationFound"))
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureName+","+colTreasureYear+","+colTreasureDateFound+","
                +colTreasurePhotoPath+","+colTreasureLocationFound+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='relic' ORDER BY (CASE WHEN "+sortType+" IS \"\" THEN 1 ELSE 0 END), "+"lower("+sortType+")";
        }
        //treasure date found, most recently added
        else
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureName+","+colTreasureYear+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='relic' ORDER BY "+sortType+" DESC";
        }

        //Log.d("myTag", selectQuery);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Log.d("myTag", "query retrieved.");

        if(cursor.moveToFirst()){
            do {
                treasureList.add(new Treasure(cursor.getInt(cursor.getColumnIndex(colTreasureID)), null, null, null, null, cursor.getString(cursor.getColumnIndex(colTreasureName)), cursor.getString(cursor.getColumnIndex(colTreasureYear)), null, null,null ,cursor.getString(cursor.getColumnIndex(colTreasureLocationFound)), cursor.getString(cursor.getColumnIndex(colTreasureDateFound)), null, cursor.getString(cursor.getColumnIndex(colTreasurePhotoPath))));
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return treasureList;
    }

    public ArrayList<Treasure> getAllCollections(String sortType)
    {
        ArrayList<Treasure> treasureList = new ArrayList<>();
        String selectQuery;

        if(sortType.equals("TreasureLocationFound"))
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureName+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='collection' ORDER BY (CASE WHEN "+sortType+" IS \"\" THEN 1 ELSE 0 END), "+"lower("+sortType+")";
        }
        //treasure date found, most recently added
        else
        {
            selectQuery = "SELECT "+colTreasureID+", "+colTreasureName+","+colTreasureDateFound+","
                    +colTreasurePhotoPath+","+colTreasureLocationFound+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureType+"='collection' ORDER BY "+sortType+" DESC";
        }

        //Log.d("myTag", selectQuery);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Log.d("myTag", "query retrieved.");

        if(cursor.moveToFirst()){
            do {
                treasureList.add(new Treasure(cursor.getInt(cursor.getColumnIndex(colTreasureID)), null, null, null, null, cursor.getString(cursor.getColumnIndex(colTreasureName)), null, null, null,null,cursor.getString(cursor.getColumnIndex(colTreasureLocationFound)), cursor.getString(cursor.getColumnIndex(colTreasureDateFound)), null, cursor.getString(cursor.getColumnIndex(colTreasurePhotoPath))));
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        //Log.d("myTag", ""+treasureList.size());

        return treasureList;
    }

    public Treasure getDetailedTreasure(int treasureId)
    {
        Treasure treasure;
        String selectQuery = "SELECT * FROM "+TABLE_TREASURE+" WHERE "+colTreasureID+"="+treasureId;
        //Log.d("myTag", selectQuery);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Log.d("myTag", "query retrieved.");
        if(cursor.moveToFirst()){
            treasure = new Treasure(cursor.getInt(cursor.getColumnIndex(colTreasureID)), cursor.getString(cursor.getColumnIndex(colTreasureType)), cursor.getString(cursor.getColumnIndex(colTreasureCountry)), cursor.getString(cursor.getColumnIndex(colTreasureDenomination)), cursor.getString(cursor.getColumnIndex(colTreasureSeries)), cursor.getString(cursor.getColumnIndex(colTreasureName)), cursor.getString(cursor.getColumnIndex(colTreasureYear)), cursor.getString(cursor.getColumnIndex(colTreasureMint)), cursor.getString(cursor.getColumnIndex(colTreasureMaterial)),cursor.getString(cursor.getColumnIndex(colTreasureWeight)) ,cursor.getString(cursor.getColumnIndex(colTreasureLocationFound)), cursor.getString(cursor.getColumnIndex(colTreasureDateFound)), cursor.getString(cursor.getColumnIndex(colTreasureInfo)), cursor.getString(cursor.getColumnIndex(colTreasurePhotoPath)));
            cursor.close();
            db.close();
            return treasure;
        }
        else
        {
            cursor.close();
            db.close();
            return null;
        }
    }


    public ArrayList<Clad> getAllClad(String sortType)
    {
        ArrayList<Clad> cladList = new ArrayList<>();
        String selectQuery;

        if(sortType.equals("CladLocationFound"))
        {
            selectQuery = "SELECT "+colCladID+","+colCladCurrency+","+colCladAmount+","+colCladLocationFound+","
                    +colCladDateFound+" FROM "+TABLE_CLAD+" ORDER BY (CASE WHEN "+sortType+" IS \"\" THEN 1 ELSE 0 END), "+sortType;
        }
        //treasure date found, most recently added
        else
        {
            selectQuery = "SELECT "+colCladID+","+colCladCurrency+","+colCladAmount+","+colCladLocationFound+","
                    +colCladDateFound+" FROM "+TABLE_CLAD+" ORDER BY "+sortType+" DESC";
        }

        //Log.d("myTag", selectQuery);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Log.d("myTag", "query retrieved.");

        if(cursor.moveToFirst()){
            do {
                cladList.add(new Clad(cursor.getInt(cursor.getColumnIndex(colCladID)), cursor.getString(cursor.getColumnIndex(colCladCurrency)), cursor.getDouble(cursor.getColumnIndex(colCladAmount)), cursor.getString(cursor.getColumnIndex(colCladLocationFound)), cursor.getString(cursor.getColumnIndex(colCladDateFound))));
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cladList;
    }

    public HashMap<String, Integer> getSummaryTreasure()
    {
        HashMap<String, Integer> summaryList = new HashMap<>();
        String selectQuery = "SELECT "+colTreasureType+", COUNT("+colTreasureType+") FROM "+TABLE_TREASURE+" GROUP BY "+colTreasureType;
        //Log.d("myTag", selectQuery);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Log.d("myTag", "query retrieved.");

        if(cursor.moveToFirst()){
            do {
                //Log.d("myTag", "cursor entry...");
                //Log.d("myTag", cursor.getString(0)+": "+cursor.getInt(1));
                summaryList.put(cursor.getString(0), cursor.getInt(1));
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return summaryList;
    }

    public LinkedHashMap<String, Double> getSummaryClad()
    {
        LinkedHashMap<String, Double> summaryList = new LinkedHashMap<>();
        String selectQuery = "SELECT "+colCladCurrency+", SUM("+colCladAmount+") FROM "+TABLE_CLAD+" GROUP BY "+colCladCurrency+ " ORDER BY SUM("+colCladAmount+") DESC";
        //Log.d("myTag", selectQuery);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Log.d("myTag", "query retrieved.");

        if(cursor.moveToFirst()){
            do {
                //Log.d("myTag", "cursor entry...");
                //Log.d("myTag", cursor.getString(0)+": "+cursor.getDouble(1));
                summaryList.put(cursor.getString(0), cursor.getDouble(1));
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return summaryList;
    }

    public ArrayList<Treasure> getYearlySummaryTreasure()
    {
        ArrayList<Treasure> treasureList = new ArrayList<>();
        String selectQuery;

            selectQuery = "SELECT "+colTreasureType+","+colTreasureDateFound+" FROM "+TABLE_TREASURE;

        //Log.d("myTag", selectQuery);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Log.d("myTag", "query retrieved.");

        if(cursor.moveToFirst()){
            do {
                treasureList.add(new Treasure(0, cursor.getString(cursor.getColumnIndex(colTreasureType)), null, null, null, null, null, null, null, null, null, cursor.getString(cursor.getColumnIndex(colTreasureDateFound)), null, null));
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return treasureList;
    }

    public ArrayList<Clad> getYearlySummaryClad()
    {
        ArrayList<Clad> cladList = new ArrayList<>();
        String selectQuery;

        selectQuery = "SELECT "+colCladCurrency+","+colCladAmount+","+colCladDateFound+" FROM "+TABLE_CLAD;

        //Log.d("myTag", selectQuery);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Log.d("myTag", "query retrieved.");

        if(cursor.moveToFirst()){
            do {
                cladList.add(new Clad(0, cursor.getString(cursor.getColumnIndex(colCladCurrency)), cursor.getDouble(cursor.getColumnIndex(colCladAmount)), null, cursor.getString(cursor.getColumnIndex(colCladDateFound))));
            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cladList;
    }

    public long addTreasure(Treasure treasure)
    {
        //get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        //create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(colTreasureCountry, treasure.getTreasureCountry()); // get country
        values.put(colTreasureName, treasure.getTreasureName()); // get name
        values.put(colTreasureType, treasure.getTreasureType()); // get type
        values.put(colTreasureDenomination, treasure.getTreasureDenomination()); // get denomination
        values.put(colTreasureSeries, treasure.getTreasureSeries()); // get series
        values.put(colTreasureYear, treasure.getTreasureYear()); // get year
        values.put(colTreasureMint, treasure.getTreasureMint()); // get mint
        values.put(colTreasureMaterial, treasure.getTreasureMaterial()); // get material
        values.put(colTreasureWeight, treasure.getTreasureWeight()); // get weight
        values.put(colTreasureDateFound, treasure.getTreasureDateFound()); // get datefound
        values.put(colTreasureLocationFound, treasure.getTreasureLocationFound()); // get locationfound
        values.put(colTreasureInfo, treasure.getTreasureInfo()); // get info
        values.put(colTreasurePhotoPath, treasure.getTreasurePhotoPath()); // get photopath

        //result will contain the row ID of the newly inserted row, or -1 if an error occurred.
        long result = db.insert(TABLE_TREASURE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        //close
        db.close();
        return result;
    }

    public long editTreasure(Treasure treasure)
    {
        //get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = colTreasureID + " = ?";

        //create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(colTreasureCountry, treasure.getTreasureCountry()); // get country
        values.put(colTreasureName, treasure.getTreasureName()); // get name
        values.put(colTreasureType, treasure.getTreasureType()); // get type
        values.put(colTreasureDenomination, treasure.getTreasureDenomination()); // get denomination
        values.put(colTreasureSeries, treasure.getTreasureSeries()); // get series
        values.put(colTreasureYear, treasure.getTreasureYear()); // get year
        values.put(colTreasureMint, treasure.getTreasureMint()); // get mint
        values.put(colTreasureMaterial, treasure.getTreasureMaterial()); // get material
        values.put(colTreasureWeight, treasure.getTreasureWeight()); // get weight
        values.put(colTreasureDateFound, treasure.getTreasureDateFound()); // get datefound
        values.put(colTreasureLocationFound, treasure.getTreasureLocationFound()); // get locationfound
        values.put(colTreasureInfo, treasure.getTreasureInfo()); // get info

        //result will contain the row ID of the newly inserted row, or -1 if an error occurred.
        long result = db.update(TABLE_TREASURE, values, whereClause, new String[]{Integer.toString(treasure.getTreasureId())});

        //close
        db.close();
        return result;
    }

    public long addClad(Clad clad)
    {
        //get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        //create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(colCladCurrency, clad.getCladCurrency()); // get currency
        values.put(colCladAmount, clad.getCladAmount()); // get amount
        values.put(colCladLocationFound, clad.getCladLocationFound()); // get location found
        values.put(colCladDateFound, clad.getCladDateFound()); // get date found

        //result will contain the row ID of the newly inserted row, or -1 if an error occurred.
        long result = db.insert(TABLE_CLAD, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        //close
        db.close();
        return result;
    }

    public void deleteTreasure(String treasureID)
    {
        //get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_TREASURE, colTreasureID+"=?",new String[]{treasureID});
        db.close();
    }

    public void deleteClad(String cladID)
    {
        //get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_CLAD, colCladID+"=?",new String[]{cladID});
        db.close();
    }

    //function used for fetching data for exporting database
    public Cursor raw() {

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT "+colTreasureType+","+colTreasureDenomination+","+colTreasureSeries+","+colTreasureName+","+colTreasureYear+","+colTreasureMint+","+colTreasureMaterial+","+colTreasureWeight+","+colTreasureLocationFound+","+colTreasureDateFound+","+colTreasureInfo+" FROM " + TABLE_TREASURE , new String[]{});
    }

    public void updateOldDates() {

        //get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        //get all rows that are still using old date formats
        String selectQuery = "SELECT "+colTreasureID+","+colTreasureDateFound+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureDateFound+" LIKE '%/%/____'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex(colTreasureID));
                String oldDate = cursor.getString(cursor.getColumnIndex(colTreasureDateFound));
                String[] splitDate = oldDate.split("/");

                String newDate = splitDate[2]+"/"+splitDate[0]+"/"+splitDate[1];

                //now update the date to proper yyyy/mm/dd format so it can be sorted correctly
                ContentValues cv = new ContentValues();
                cv.put(colTreasureDateFound,newDate);

                db.update(TABLE_TREASURE, cv, colTreasureID+"="+id, null);

            } while(cursor.moveToNext());
        }

        //get all rows that are still using old date formats
        selectQuery = "SELECT "+colCladID+","+colCladDateFound+" FROM "+TABLE_CLAD+" WHERE "+colCladDateFound+" LIKE '%/%/____'";
        cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex(colCladID));
                String oldDate = cursor.getString(cursor.getColumnIndex(colCladDateFound));
                String[] splitDate = oldDate.split("/");

                String newDate = splitDate[2]+"/"+splitDate[0]+"/"+splitDate[1];

                //now update the date to proper yyyy/mm/dd format so it can be sorted correctly
                ContentValues cv = new ContentValues();
                cv.put(colCladDateFound,newDate);

                db.update(TABLE_CLAD, cv, colCladID+"="+id, null);

            } while(cursor.moveToNext());
        }

        //find out if user has any old treasure or clad rows using treasureDateFound with missing zero in front of month or day. i.e. 2019/1/9.
        // Update to 2019/01/09 to allow for proper sorting
        selectQuery = "SELECT "+colTreasureID+","+colTreasureDateFound+" FROM "+TABLE_TREASURE+" WHERE "+colTreasureDateFound+" LIKE '____/_/%' OR "+colTreasureDateFound+" LIKE '____/%/_'";

        cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex(colTreasureID));
                String oldDate = cursor.getString(cursor.getColumnIndex(colTreasureDateFound));
                String[] splitDate = oldDate.split("/");

                for(int i=0; i<=2; i++)
                {
                    if(splitDate[i].length() == 1)
                    {
                        splitDate[i] = "0"+splitDate[i];
                    }
                }

                String newDate = splitDate[0]+"/"+splitDate[1]+"/"+splitDate[2];

                //now update the date to proper yyyy/mm/dd format so it can be sorted correctly
                ContentValues cv = new ContentValues();
                cv.put(colTreasureDateFound,newDate);

                db.update(TABLE_TREASURE, cv, colTreasureID+"="+id, null);

            } while(cursor.moveToNext());
        }

        selectQuery = "SELECT "+colCladID+","+colCladDateFound+" FROM "+TABLE_CLAD+" WHERE "+colCladDateFound+" LIKE '____/_/%' OR "+colCladDateFound+" LIKE '____/%/_'";

        cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex(colCladID));
                String oldDate = cursor.getString(cursor.getColumnIndex(colCladDateFound));
                String[] splitDate = oldDate.split("/");

                for(int i=0; i<=2; i++)
                {
                    if(splitDate[i].length() == 1)
                    {
                        splitDate[i] = "0"+splitDate[i];
                    }
                }

                String newDate = splitDate[0]+"/"+splitDate[1]+"/"+splitDate[2];

                //now update the date to proper yyyy/mm/dd format so it can be sorted correctly
                ContentValues cv = new ContentValues();
                cv.put(colCladDateFound,newDate);

                db.update(TABLE_CLAD, cv, colCladID+"="+id, null);

            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }
}
