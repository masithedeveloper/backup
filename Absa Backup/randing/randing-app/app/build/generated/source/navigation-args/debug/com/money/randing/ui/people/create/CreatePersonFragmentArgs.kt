package com.money.randing.ui.people.create

import android.os.Bundle
import androidx.navigation.NavArgs
import kotlin.String
import kotlin.jvm.JvmStatic

public data class CreatePersonFragmentArgs(
  public val personId: String? = null
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("person_id", this.personId)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): CreatePersonFragmentArgs {
      bundle.setClassLoader(CreatePersonFragmentArgs::class.java.classLoader)
      val __personId : String?
      if (bundle.containsKey("person_id")) {
        __personId = bundle.getString("person_id")
      } else {
        __personId = null
      }
      return CreatePersonFragmentArgs(__personId)
    }
  }
}
