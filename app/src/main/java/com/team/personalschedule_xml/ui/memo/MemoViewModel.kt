package com.team.personalschedule_xml.ui.memo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.team.personalschedule_xml.data.model.Memo
import com.team.personalschedule_xml.data.repository.MemoRepository
import kotlinx.coroutines.launch

class MemoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MemoRepository(application)

    val memoList : LiveData<List<Memo>> = repository.getAllMemos()

    fun insertMemo(memo: Memo) {
        viewModelScope.launch { repository.insertMemo(memo) }
    }

    fun deleteMemo(memo: Memo) {
        viewModelScope.launch { repository.deleteMemo(memo) }
    }

    fun updateMemo(memo: Memo) {
        viewModelScope.launch { repository.updateMemo(memo) }
    }
}