package com.team.personalschedule_xml.data.repository

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.team.personalschedule_xml.data.db.MemoDao
import com.team.personalschedule_xml.data.db.MemoDatabase
import com.team.personalschedule_xml.data.db.ScheduleDatabase
import com.team.personalschedule_xml.data.model.Memo

class MemoRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        MemoDatabase::class.java,
        "memo_database"
    )
        .addMigrations(MIGRATION_2_3)
        .fallbackToDestructiveMigration() // 개발 중 임시, 추후 제거 추천
        .build()

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Memo ADD COLUMN labelName TEXT NOT NULL DEFAULT ''")
            }
        }
    }

    private val memoDao = db.memoDao()

    fun getAllMemos() = memoDao.getAllMemos()

    suspend fun insertMemo(memo: Memo) = memoDao.insertMemo(memo)

    suspend fun deleteMemo(memo: Memo) = memoDao.deleteMemo(memo)

    suspend fun updateMemo(memo: Memo) = memoDao.updateMemo(memo)
}