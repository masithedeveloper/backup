package com.money.randing.ui.chart

import androidx.lifecycle.ViewModel
import com.money.randing.domain.repository.movement.MovementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val movementRepository: MovementRepository
) : ViewModel() {

    val movements = movementRepository.requestAllMovementsSortedByDate()
}