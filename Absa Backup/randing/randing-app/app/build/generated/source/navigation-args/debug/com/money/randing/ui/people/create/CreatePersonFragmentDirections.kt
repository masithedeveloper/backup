package com.money.randing.ui.people.create

import android.os.Bundle
import androidx.navigation.NavDirections
import com.money.randing.NavGraphDirections
import com.money.randing.R
import kotlin.Int
import kotlin.String

public class CreatePersonFragmentDirections private constructor() {
  private data class ActionCreatePersonToCreateMovement(
    public val personId: String,
    public val id: String? = null
  ) : NavDirections {
    public override fun getActionId(): Int = R.id.action_createPerson_to_createMovement

    public override fun getArguments(): Bundle {
      val result = Bundle()
      result.putString("person_id", this.personId)
      result.putString("id", this.id)
      return result
    }
  }

  public companion object {
    public fun actionCreatePersonToCreateMovement(personId: String, id: String? = null):
        NavDirections = ActionCreatePersonToCreateMovement(personId, id)

    public fun actionGlobalCreatePersonFragment(personId: String? = null): NavDirections =
        NavGraphDirections.actionGlobalCreatePersonFragment(personId)

    public fun actionGlobalCreateMovementFragment(personId: String, id: String? = null):
        NavDirections = NavGraphDirections.actionGlobalCreateMovementFragment(personId, id)

    public fun actionGlobalPersonDetail(personId: Int): NavDirections =
        NavGraphDirections.actionGlobalPersonDetail(personId)
  }
}
