package com.money.randing.ui.home

import androidx.lifecycle.ViewModel
import com.money.randing.domain.repository.movement.MovementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    movementRepository: MovementRepository
) : ViewModel() {

    val movements = movementRepository.requestAllMovements()
}