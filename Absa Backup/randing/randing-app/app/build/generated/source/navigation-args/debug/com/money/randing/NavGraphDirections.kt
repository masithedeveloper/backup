package com.money.randing

import android.os.Bundle
import androidx.navigation.NavDirections
import kotlin.Int
import kotlin.String

public class NavGraphDirections private constructor() {
  private data class ActionGlobalCreatePersonFragment(
    public val personId: String? = null
  ) : NavDirections {
    public override fun getActionId(): Int = R.id.action_global_createPersonFragment

    public override fun getArguments(): Bundle {
      val result = Bundle()
      result.putString("person_id", this.personId)
      return result
    }
  }

  private data class ActionGlobalCreateMovementFragment(
    public val personId: String,
    public val id: String? = null
  ) : NavDirections {
    public override fun getActionId(): Int = R.id.action_global_createMovementFragment

    public override fun getArguments(): Bundle {
      val result = Bundle()
      result.putString("person_id", this.personId)
      result.putString("id", this.id)
      return result
    }
  }

  private data class ActionGlobalPersonDetail(
    public val personId: Int
  ) : NavDirections {
    public override fun getActionId(): Int = R.id.action_global_personDetail

    public override fun getArguments(): Bundle {
      val result = Bundle()
      result.putInt("person_id", this.personId)
      return result
    }
  }

  public companion object {
    public fun actionGlobalCreatePersonFragment(personId: String? = null): NavDirections =
        ActionGlobalCreatePersonFragment(personId)

    public fun actionGlobalCreateMovementFragment(personId: String, id: String? = null):
        NavDirections = ActionGlobalCreateMovementFragment(personId, id)

    public fun actionGlobalPersonDetail(personId: Int): NavDirections =
        ActionGlobalPersonDetail(personId)
  }
}
