package com.money.randing.ui.movement.person_search

import android.os.Bundle
import androidx.navigation.NavDirections
import com.money.randing.NavGraphDirections
import com.money.randing.R
import kotlin.Int
import kotlin.String

public class PersonSearchFragmentDirections private constructor() {
  private data class ActionPersonSearchToCreateMovement(
    public val personId: String,
    public val id: String? = null
  ) : NavDirections {
    public override fun getActionId(): Int = R.id.action_personSearch_to_createMovement

    public override fun getArguments(): Bundle {
      val result = Bundle()
      result.putString("person_id", this.personId)
      result.putString("id", this.id)
      return result
    }
  }

  private data class ActionPersonSearchToCreatePerson(
    public val personId: String
  ) : NavDirections {
    public override fun getActionId(): Int = R.id.action_personSearch_to_createPerson

    public override fun getArguments(): Bundle {
      val result = Bundle()
      result.putString("person_id", this.personId)
      return result
    }
  }

  public companion object {
    public fun actionPersonSearchToCreateMovement(personId: String, id: String? = null):
        NavDirections = ActionPersonSearchToCreateMovement(personId, id)

    public fun actionPersonSearchToCreatePerson(personId: String): NavDirections =
        ActionPersonSearchToCreatePerson(personId)

    public fun actionGlobalCreatePersonFragment(personId: String? = null): NavDirections =
        NavGraphDirections.actionGlobalCreatePersonFragment(personId)

    public fun actionGlobalCreateMovementFragment(personId: String, id: String? = null):
        NavDirections = NavGraphDirections.actionGlobalCreateMovementFragment(personId, id)

    public fun actionGlobalPersonDetail(personId: Int): NavDirections =
        NavGraphDirections.actionGlobalPersonDetail(personId)
  }
}
