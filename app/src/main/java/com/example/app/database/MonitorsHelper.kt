package com.example.app.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MonitorsHelper(context:Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "monitors.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "monitors"
        private const val COLUMN_ID_USER = "id_user"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_IP = "ip"
        private const val COLUMN_PORT = "port"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL UNIQUE,
                $COLUMN_IP TEXT NOT NULL,
                $COLUMN_PORT TEXT NOT NULL,
                $COLUMN_ID_USER INTEGER,
                FOREIGN KEY($COLUMN_ID_USER) REFERENCES users(id)
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    fun insertMonitor(name: String, ip: String, port: String, idUser: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_IP, ip)
            put(COLUMN_PORT, port)
            put(COLUMN_ID_USER, idUser)
        }
        return db.insert(TABLE_NAME, null, values)
    }
    fun getMonitorsByUser(idUser: Int): List<Map<String, String>> {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID_USER = ?"
        val cursor = db.rawQuery(query, arrayOf(idUser.toString()))
        val monitors = mutableListOf<Map<String, String>>()

        while (cursor.moveToNext()) {
            monitors.add(
                mapOf(
                    "id" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    "name" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    "ip" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IP)),
                    "port" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PORT))
                )
            )
        }
        cursor.close()
        return monitors
    }

    fun deleteMonitor(monitorId: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(monitorId.toString()))
    }



}