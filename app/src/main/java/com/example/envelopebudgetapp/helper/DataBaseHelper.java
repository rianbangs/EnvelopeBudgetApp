package com.example.envelopebudgetapp.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.envelopebudgetapp.model.EnvelopDetailModel;
import com.example.envelopebudgetapp.model.EnvelopModel;
import com.example.envelopebudgetapp.model.IncomeModel;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper
{
    public static final String INCOME_TABLE = "INCOME_TABLE";
    public static final String COLUMN_INCOME_AMOUNT = "INCOME_AMOUNT";
    public static final String COLUMN_INCOME_DATE = "INCOME_DATE";
    public static final String COLUMN_INCOME_DETAIL = "INCOME_DETAIL";
    public static final String ID = "ID";


    //Envelop Parent table -----------------------------------------------------------------------------------
    public static final String ENVELOPE_TABLE = "ENVELOPE_TABLE";
    public static final String ENV_NAME = "ENV_NAME";
    public static final String DATE_ADDED = "DATE_ADDED";

     //Envelop details table --------------------------------------------------------------------------------
    public static final String ENVELOPE_DETAIL_TABLE = "ENVELOPE_DETAIL_TABLE";
    public static final String ENV_DETAIL_DESCRIPTION = "ENV_DETAIL_DESCRIPTION";
    public static final String DETAIL_TYPE = "DETAIL_TYPE";
    public static final String ENV_AMOUNT = "ENV_AMOUNT";
    public static final String ENV_DATE = "ENV_DATE";
    public static final String ENV_ID = "ENV_ID";

    public DataBaseHelper(@Nullable Context context)
    {
        super(context, "income.db", null,4);
    }
    //this is called the first time a database is accessed. There should be in here to create a new database.
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createTableStatement="CREATE TABLE "+INCOME_TABLE+" (" +
                                        "ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                        COLUMN_INCOME_AMOUNT+" FLOAT,"+
                                        COLUMN_INCOME_DATE+" TEXT, "+
                                        COLUMN_INCOME_DETAIL+" TEXT)";
        db.execSQL(createTableStatement);

        String createEnvelopTable = "CREATE TABLE "+ENVELOPE_TABLE+" (" +
                                    "ENV_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                                     ENV_NAME+" TEXT," +
                                     DATE_ADDED+" TEXT)";
        db.execSQL(createEnvelopTable);

        String createEnvelopeDetailTable = "CREATE TABLE "+ENVELOPE_DETAIL_TABLE+"(" +
                                            "ENV_DET_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                                            ENV_DETAIL_DESCRIPTION+" TEXT," +
                                            DETAIL_TYPE+ " TEXT," +
                                            ENV_AMOUNT+" FLOAT," +
                                            ENV_DATE+" TEXT," +
                                            ENV_ID+" INTEGER)";
        db.execSQL(createEnvelopeDetailTable);
    }

    // this is called if the database version number changes. It prevents previous users apps from breaking when you change the database design.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (oldVersion < 3) {
            // Perform database schema changes for version 2
            // For example, add the ENVELOPE_TABLE
            String createEnvelopTable = "CREATE TABLE " + ENVELOPE_TABLE + " (" +
                    "ENV_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ENV_NAME + " TEXT," +
                    DATE_ADDED + " TEXT)";
            db.execSQL(createEnvelopTable);

            // Add other necessary schema changes for version 2 here
        }

        if(oldVersion < 4)
        {
            String createEnvelopeDetailTable = "CREATE TABLE "+ENVELOPE_DETAIL_TABLE+"(" +
                    "ENV_DET_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ENV_DETAIL_DESCRIPTION+" TEXT," +
                    DETAIL_TYPE+ " TEXT," +
                    ENV_AMOUNT+" FLOAT," +
                    ENV_DATE+" TEXT," +
                    ENV_ID+" INTEGER)";
            db.execSQL(createEnvelopeDetailTable);
        }
    }
//ENVELOPE_DETAIL_TABLE functions--------------------
    public List<EnvelopDetailModel> getEnvList(EnvelopModel envelopModel,String filter)
    {
        String filterQuery ="";
        if(filter == "specific")
        {
            filterQuery = " WHERE "+ENV_ID+"="+envelopModel.getEnv_id();
        }

        return getEnvelopeDetailList(filterQuery);
    }



    public List<EnvelopDetailModel> getOneEnvDet(EnvelopDetailModel envelopDetailModel)
    {
        String filterQuery =" WHERE ENV_DET_ID="+envelopDetailModel.getEnv_det_id();
        return getEnvelopeDetailList(filterQuery);
    }

    public List<EnvelopDetailModel> getEnvelopeDetailList(String filterQuery)
    {
        List<EnvelopDetailModel> envelopeDetailList = new ArrayList<>();

        String queryString = "SELECT * FROM "+ENVELOPE_DETAIL_TABLE+ filterQuery;
        SQLiteDatabase db =  this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst())
        {
            //loop through the cursor (result set) and create objects. Put them into the return list
            do{
                int  ENV_DET_ID = cursor.getInt(0);
                String ENV_DETAIL_DESCRIPTION = cursor.getString(1);
                String DETAIL_TYPE = cursor.getString(2);
                float ENV_AMOUNT = cursor.getFloat(3);
                String ENV_DATE = cursor.getString(4);
                int ENV_ID = cursor.getInt(5);
                EnvelopDetailModel newEnvelopeDetail = new EnvelopDetailModel(ENV_DET_ID,ENV_DETAIL_DESCRIPTION,DETAIL_TYPE,ENV_AMOUNT,ENV_DATE,ENV_ID);
                envelopeDetailList.add(newEnvelopeDetail);
            }
            while(cursor.moveToNext());
        }
        else
        {

        }
        //close
        cursor.close();
        db.close();
        return envelopeDetailList;
    }

    public boolean addEnvelopeDetail(EnvelopDetailModel envelopDetailModel)
{

    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues cv = new ContentValues();
    cv.put(ENV_DETAIL_DESCRIPTION, envelopDetailModel.getEnv_detail_description());
    cv.put(DETAIL_TYPE,envelopDetailModel.getDetail_type());
    cv.put(ENV_AMOUNT,envelopDetailModel.getEnv_amount());
    cv.put(ENV_DATE,envelopDetailModel.getEvn_date());
    cv.put(ENV_ID,envelopDetailModel.getEnv_id());

    long insert = db.insert(ENVELOPE_DETAIL_TABLE, null, cv);
    if(insert == -1)
    {
        return false;
    }
    else
    {
        return true;
    }

}

    public boolean updateOneDetail(EnvelopDetailModel envelopDetailModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("ENV_DETAIL_DESCRIPTION", envelopDetailModel.getEnv_detail_description());
        cv.put("DETAIL_TYPE", envelopDetailModel.getDetail_type());
        cv.put("ENV_AMOUNT", envelopDetailModel.getEnv_amount());
        cv.put("ENV_DATE", envelopDetailModel.getEvn_date());

        String whereClause = "ENV_DET_ID = ?";
        String[] whereArgs = { String.valueOf(envelopDetailModel.getEnv_det_id()) };

        int update = db.update("ENVELOPE_DETAIL_TABLE", cv, whereClause, whereArgs);
        return update > 0;
    }

    public boolean deleteEnvelope_detail(EnvelopDetailModel envelopDetailModel,String whereClause,EnvelopModel envelopModel) {
        SQLiteDatabase db = this.getWritableDatabase();
//        String whereClause = "ENV_DET_ID = ?";
//        String[] whereArgs = {String.valueOf(envelopDetailModel.getEnv_det_id())};
        String wherevalue = "";
        if(whereClause == "ENV_DET_ID = ?")
        {
              wherevalue = String.valueOf(envelopDetailModel.getEnv_det_id());
        }
        else
        {
              wherevalue = String.valueOf(envelopModel.getEnv_id());
        }

        String[] whereArgs = {wherevalue};
        int deletedRows = db.delete(ENVELOPE_DETAIL_TABLE, whereClause, whereArgs);
        return deletedRows > 0; // Return true if any rows were deleted, false otherwise
    }


//ENVELOPE_TABLE functions


public List<EnvelopModel> getEnvelopeList()
{
    List<EnvelopModel> envelopeList = new ArrayList<>();

    String queryString = "SELECT * FROM "+ENVELOPE_TABLE;
    SQLiteDatabase db =  this.getReadableDatabase();
    Cursor cursor = db.rawQuery(queryString,null);

    if(cursor.moveToFirst())
    {
        //loop through the cursor (result set) and create objects. Put them into the return list
        do{
            int  env_id = cursor.getInt(0);
            String env_name = cursor.getString(1);
            String date_added = cursor.getString(2);
            EnvelopModel newEnvelope = new EnvelopModel(env_id,env_name,date_added);
            envelopeList.add(newEnvelope);
        }
        while(cursor.moveToNext());
    }

    //close
    cursor.close();
    db.close();
    return envelopeList;
}



    public boolean addEnvelope(EnvelopModel envelopModel)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ENV_NAME, envelopModel.getEnv_name());
        cv.put(DATE_ADDED,envelopModel.getDate_added());
        long insert = db.insert(ENVELOPE_TABLE, null, cv);
        if(insert == -1)
        {
            return false;
        }
        else
        {
            return true;
        }

    }

    public boolean deleteABudget(EnvelopModel envelopModel,String whereClause) {
        SQLiteDatabase db = this.getWritableDatabase();
        //String whereClause = "ENV_ID = ?";
        String[] whereArgs = {String.valueOf(envelopModel.getEnv_id())};
        int deletedRows = db.delete(ENVELOPE_TABLE, whereClause, whereArgs);
        return deletedRows > 0; // Return true if any rows were deleted, false otherwise
    }

    public boolean updateABudget(EnvelopModel envelopModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("ENV_NAME", envelopModel.getEnv_name());
        cv.put("DATE_ADDED", envelopModel.getDate_added());


        String whereClause = "ENV_ID = ?";
        String[] whereArgs = { String.valueOf(envelopModel.getEnv_id()) };

        int update = db.update(ENVELOPE_TABLE, cv, whereClause, whereArgs);
        return update > 0;
    }

//INCOME_TABLE  functions -----------------------------------------------------------------
    public boolean addIncome(IncomeModel incomeModel)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_INCOME_AMOUNT, incomeModel.getIncome());
        cv.put(COLUMN_INCOME_DATE, incomeModel.getIncomeDate());
        cv.put(COLUMN_INCOME_DETAIL, incomeModel.getIncomeDetail());
        long insert = db.insert(INCOME_TABLE, null, cv);
        if(insert == -1)
        {
            return false;
        }
        else
        {
            return true;
        }

    }


    public boolean updateIncome(IncomeModel incomeModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_INCOME_AMOUNT, incomeModel.getIncome());
        cv.put(COLUMN_INCOME_DATE, incomeModel.getIncomeDate());
        cv.put(COLUMN_INCOME_DETAIL, incomeModel.getIncomeDetail());

        // Define 'where' part of query.
        String selection = " ID = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(incomeModel.getId()) };

        // Issue SQL statement.
        int count = db.update(
                INCOME_TABLE,   // The table to update
                cv,             // The new values
                selection,      // The columns for the WHERE clause
                selectionArgs   // The values for the WHERE clause
        );

        db.close();

        // Return true if the update affected at least one row, false otherwise
        return count > 0;
    }


    public List<IncomeModel> getIncomeList()
    {
        List<IncomeModel> incomeList = new ArrayList<>();

        String queryString = "SELECT * FROM "+INCOME_TABLE;
        SQLiteDatabase db =  this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);
        if(cursor.moveToFirst())
        {
            //loop through the cursor (result set) and create objects. Put them into the return list
            do{
                   int  incomeID = cursor.getInt(0);
                   float incomeAmount = cursor.getFloat(1);
                   String incomeDate = cursor.getString(2);
                   String incomeDetail = cursor.getString(3);
                   IncomeModel newIncome = new IncomeModel(incomeID,incomeAmount,incomeDate,incomeDetail);
                   incomeList.add(newIncome);
            }
            while(cursor.moveToNext());
        }
        else
        {

        }
        //close
        cursor.close();
        db.close();
        return incomeList;
    }

    public boolean deleteIncome(IncomeModel incomeModel)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM "+INCOME_TABLE+" WHERE "+ID+" = "+incomeModel.getId();
        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean clean_ENVELOPE_DETAIL_TABLE() {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "ENV_ID NOT IN (SELECT ENV_ID FROM " + INCOME_TABLE + ")";
        int deletedRows = 0;
        try {
//            deletedRows = db.delete(ENVELOPE_DETAIL_TABLE, whereClause, null);
            deletedRows = db.delete(ENVELOPE_DETAIL_TABLE, whereClause, null);
        } catch (Exception e) {
            Log.e("DataBaseHelper", "Error cleaning ENVELOPE_DETAIL_TABLE", e);
        }
        return deletedRows > 0; // Return true if any rows were deleted, false otherwise
    }

    public void truncate_ENVELOPE_DETAIL_TABLE() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + ENVELOPE_DETAIL_TABLE);
            Log.i("DataBaseHelper", "Table " + ENVELOPE_DETAIL_TABLE + " truncated successfully.");
        } catch (Exception e) {
            Log.e("DataBaseHelper", "Error truncating table " + ENVELOPE_DETAIL_TABLE, e);
        }
    }


    public float getTotalIncomeAmount()
    {
        float totalIncomeAmount = 0;

        String queryString = "SELECT (SUM(" + COLUMN_INCOME_AMOUNT + ") - " +
                                     "IFNULL((SELECT SUM("+ENV_AMOUNT+") FROM "+ENVELOPE_DETAIL_TABLE+" WHERE "+DETAIL_TYPE+"= 'Allocated Income'),0 ))" +
                              "FROM " + INCOME_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            totalIncomeAmount = cursor.getFloat(0);
        }

        cursor.close();
        db.close();

        return totalIncomeAmount;
    }



    public float getRemainingAllocation(EnvelopModel envelopModel)
    {
        float totalIncomeAmount = 0;

        String queryString = "SELECT (" +
                "                        SUM("+ENV_AMOUNT+")- " +
                                        "IFNULL((SELECT SUM("+ENV_AMOUNT+") FROM "+ENVELOPE_DETAIL_TABLE+" WHERE "+DETAIL_TYPE+"= 'Expense' AND "+ENV_ID+"="+envelopModel.getEnv_id()+"), 0) " +
                                     ")" +
                             "FROM " + ENVELOPE_DETAIL_TABLE +" WHERE "+DETAIL_TYPE+"= 'Allocated Income' AND "+
                             ENV_ID+" = "+envelopModel.getEnv_id();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            totalIncomeAmount = cursor.getFloat(0);
        }

        cursor.close();
        db.close();

        return totalIncomeAmount;
    }



}
