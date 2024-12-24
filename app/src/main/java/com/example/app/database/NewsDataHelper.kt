package com.example.app.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NewsDataHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "news_database.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NEWS = "news"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_IMAGE = "image"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_LINK = "link"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NEWS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL UNIQUE,
                $COLUMN_IMAGE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_LINK TEXT NOT NULL
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NEWS")
        onCreate(db)
    }

    fun insertNews(title: String, image: String, description: String, link: String): Long{
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_IMAGE, image)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_LINK, link)
        }
        return db.insertWithOnConflict(TABLE_NEWS, null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun isNewsExists(title: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NEWS WHERE $COLUMN_TITLE = ?"
        val cursor = db.rawQuery(query, arrayOf(title))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
    fun getAllNews(): List<News> {
        val newsList = mutableListOf<News>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NEWS, null, null, null,
            null, null, "$COLUMN_ID DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                val title = getString(getColumnIndexOrThrow(COLUMN_TITLE))
                val image = getString(getColumnIndexOrThrow(COLUMN_IMAGE))
                val description = getString(getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val link = getString(getColumnIndexOrThrow(COLUMN_LINK))
                newsList.add(News(title, image, description, link))
            }
        }
        cursor.close()
        return newsList
    }
    data class News(
        val title: String,
        val image: String,
        val description: String,
        val link: String
    )


}
