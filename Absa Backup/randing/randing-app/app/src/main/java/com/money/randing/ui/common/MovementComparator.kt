package com.money.randing.ui.common

import androidx.recyclerview.widget.DiffUtil
import com.money.randing.domain.model.Movement

object MovementComparator : DiffUtil.ItemCallback<Movement>() {
    override fun areItemsTheSame(oldItem: Movement, newItem: Movement): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movement, newItem: Movement): Boolean {
        return oldItem == newItem
    }
}