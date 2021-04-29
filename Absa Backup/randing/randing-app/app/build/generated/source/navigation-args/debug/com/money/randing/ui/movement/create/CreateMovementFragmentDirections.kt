package com.money.randing.ui.movement.create

import androidx.navigation.NavDirections
import com.money.randing.NavGraphDirections
import kotlin.Int
import kotlin.String

public class CreateMovementFragmentDirections private constructor() {
  public companion object {
    public fun actionGlobalCreatePersonFragment(personId: String? = null): NavDirections =
        NavGraphDirections.actionGlobalCreatePersonFragment(personId)

    public fun actionGlobalCreateMovementFragment(personId: String, id: String? = null):
        NavDirections = NavGraphDirections.actionGlobalCreateMovementFragment(personId, id)

    public fun actionGlobalPersonDetail(personId: Int): NavDirections =
        NavGraphDirections.actionGlobalPersonDetail(personId)
  }
}
