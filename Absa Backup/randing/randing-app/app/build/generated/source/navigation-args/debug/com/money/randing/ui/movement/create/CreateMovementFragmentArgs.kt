package com.money.randing.ui.movement.create

import android.os.Bundle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class CreateMovementFragmentArgs(
  public val personId: String,
  public val id: String? = null
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("person_id", this.personId)
    result.putString("id", this.id)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): CreateMovementFragmentArgs {
      bundle.setClassLoader(CreateMovementFragmentArgs::class.java.classLoader)
      val __personId : String?
      if (bundle.containsKey("person_id")) {
        __personId = bundle.getString("person_id")
        if (__personId == null) {
          throw IllegalArgumentException("Argument \"person_id\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"person_id\" is missing and does not have an android:defaultValue")
      }
      val __id : String?
      if (bundle.containsKey("id")) {
        __id = bundle.getString("id")
      } else {
        __id = null
      }
      return CreateMovementFragmentArgs(__personId, __id)
    }
  }
}
