package com.tai_chain.sqlite;

import android.database.sqlite.SQLiteDatabase;

public interface DataSourceInterface {

    SQLiteDatabase openDatabase();
    void closeDatabase();
}
