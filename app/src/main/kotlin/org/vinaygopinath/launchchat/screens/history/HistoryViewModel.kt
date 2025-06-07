package org.vinaygopinath.launchchat.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.vinaygopinath.launchchat.screens.history.domain.GetDetailedActivitiesUseCase
import javax.inject.Inject
import org.vinaygopinath.launchchat.daos.DetailedActivityDao


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val useCase: GetDetailedActivitiesUseCase,
    private val detailedActivityDao: DetailedActivityDao
) : ViewModel() {

    val detailedActivities = useCase.execute()
        .distinctUntilChanged()
        .cachedIn(viewModelScope)

    fun deleteAllActivities() {
        viewModelScope.launch {
            detailedActivityDao.clearAllData()
        }
    }



}