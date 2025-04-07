package com.team.personalschedule_xml.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.team.personalschedule_xml.data.model.Memo

@Database(entities = [Memo::class], version = 3)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun memoDao() : MemoDao
}