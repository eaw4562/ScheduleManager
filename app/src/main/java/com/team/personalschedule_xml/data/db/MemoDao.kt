package com.team.personalschedule_xml.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.team.personalschedule_xml.data.model.Memo

@Dao
interface MemoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemo(memo: Memo): Long

    @Delete
    suspend fun deleteMemo(memo: Memo)

    @Update
    suspend fun updateMemo(memo: Memo)

    @Query("SELECT * FROM memo ORDER BY createdAt DESC")
    fun getAllMemos() : LiveData<List<Memo>>
}