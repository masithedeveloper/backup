package com.money.randing.data.db.dao

import androidx.room.*
import com.money.randing.data.db.entities.DBMovement
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementDao {

    @Query("SELECT * FROM movements")
    fun getAll(): Flow<List<DBMovement>>

    @Query("SELECT * FROM movements ORDER BY date ASC")
    fun getAllSortedByDate(): Flow<List<DBMovement>>

    @Query("SELECT * FROM movements WHERE person_id = :personId")
    fun getPersonMovements(personId: Int): Flow<List<DBMovement>>

    @Query("SELECT * FROM movements WHERE person_id = :personId ORDER BY date DESC")
    fun getPersonMovementsSortedByDate(personId: Int): Flow<List<DBMovement>>

    @Query("SELECT SUM(amount) FROM movements WHERE person_id = :personId")
    fun getPersonTotal(personId: Int): Flow<Double?>

    @Query("SELECT * FROM movements WHERE id = :id")
    suspend fun getMovement(id: Int): DBMovement?

    @Insert
    suspend fun insertMovement(value: DBMovement)

    @Update
    suspend fun updateMovement(value: DBMovement)

    @Delete
    suspend fun deleteMovement(value: DBMovement)

    @Query("DELETE FROM movements WHERE person_id = :personId")
    suspend fun deleteAllPersonMovements(personId: Int): Int
}