package com.barclays.absa.utils.extensions

import styleguide.utils.extensions.toRandAmount

fun Double.toRandAmount():String = this.toString().toRandAmount()