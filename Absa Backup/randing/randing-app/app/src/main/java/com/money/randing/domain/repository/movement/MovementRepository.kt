package com.money.randing.domain.repository.movement

import com.money.randing.domain.model.Movement
import kotlinx.coroutines.flow.Flow

interface MovementRepository {

    fun requestAllMovements(): Flow<List<Movement>>

    fun requestAllMovementsSortedByDate(): Flow<List<Movement>>

    fun requestPersonMovements(personId: Int): Flow<List<Movement>>

    fun requestPersonMovementsSortedByDate(personId: Int): Flow<List<Movement>>

    fun requestPersonBalance(personId: Int): Flow<Double?>

    suspend fun requestOneTimeMovement(id: Int): Movement?

    suspend fun createMovement(movement: Movement)

    suspend fun updateMovement(movement: Movement)

    suspend fun deleteMovement(movement: Movement)
}