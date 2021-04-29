package com.money.randing.ui.people.detail

import android.os.Bundle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.Int
import kotlin.jvm.JvmStatic

public data class PersonDetailFragmentArgs(
  public val personId: Int
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putInt("person_id", this.personId)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): PersonDetailFragmentArgs {
      bundle.setClassLoader(PersonDetailFragmentArgs::class.java.classLoader)
      val __personId : Int
      if (bundle.containsKey("person_id")) {
        __personId = bundle.getInt("person_id")
      } else {
        throw IllegalArgumentException("Required argument \"person_id\" is missing and does not have an android:defaultValue")
      }
      return PersonDetailFragmentArgs(__personId)
    }
  }
}
