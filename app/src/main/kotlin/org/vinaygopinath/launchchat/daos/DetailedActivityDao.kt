package org.vinaygopinath.launchchat.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.vinaygopinath.launchchat.models.DetailedActivity

@Dao
interface DetailedActivityDao {

    @Query(
        """
            SELECT * FROM activities ORDER BY occurred_at DESC LIMIT 2
        """
    )
    @Transaction
    fun getRecentDetailedActivities(): Flow<List<DetailedActivity>>

    @Query(
        """
            SELECT *
            FROM activities
            ORDER BY occurred_at DESC
            LIMIT :pageSize OFFSET :pageNumber * :pageSize
        """
    )
    @Transaction
    suspend fun getDetailedActivities(pageSize: Int, pageNumber: Int): List<DetailedActivity>

    @Query("DELETE FROM actions")
    suspend fun deleteAllActions()

    @Query("DELETE FROM activities")
    suspend fun deleteAllActivities()

    @Transaction
    suspend fun clearAllData() {
        deleteAllActions()  // Delete actions first
        deleteAllActivities() // Then delete activities
    }
}
