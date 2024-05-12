package com.example.envelopebudgetapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper
{
    public static final String INCOME_TABLE = "INCOME_TABLE";
    public static final String COLUMN_INCOME_AMOUNT = "INCOME_AMOUNT";
    public static final String COLUMN_INCOME_DATE = "INCOME_DATE";
    public static final String COLUMN_INCOME_DETAIL = "INCOME_DETAIL";
    public static final String ID = "ID";
    public DataBaseHelper(@Nullable Context context)
    {
        super(context, "income.db", null,1);
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
    }

    // this is called if the database version number changes. It prevents previous users apps from breaking when you change the database design.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

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

    public float getTotalIncomeAmount()
    {
        float totalIncomeAmount = 0;

        String queryString = "SELECT SUM(" + COLUMN_INCOME_AMOUNT + ") FROM " + INCOME_TABLE;
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
