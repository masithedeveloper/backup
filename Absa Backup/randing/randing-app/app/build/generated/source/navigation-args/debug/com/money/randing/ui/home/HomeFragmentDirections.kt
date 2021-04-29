package com.money.randing.ui.home

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.money.randing.NavGraphDirections
import com.money.randing.R
import kotlin.Int
import kotlin.String

public class HomeFragmentDirections private constructor() {
  public companion object {
    public fun actionHomeToPersonSearch(): NavDirections =
        ActionOnlyNavDirections(R.id.action_home_to_personSearch)

    public fun actionGlobalCreatePersonFragment(personId: String? = null): NavDirections =
        NavGraphDirections.actionGlobalCreatePersonFragment(personId)

    public fun actionGlobalCreateMovementFragment(personId: String, id: String? = null):
        NavDirections = NavGraphDirections.actionGlobalCreateMovementFragment(personId, id)

    public fun actionGlobalPersonDetail(personId: Int): NavDirections =
        NavGraphDirections.actionGlobalPersonDetail(personId)
  }
}
