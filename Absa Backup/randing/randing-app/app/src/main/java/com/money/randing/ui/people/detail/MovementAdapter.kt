package com.money.randing.ui.people.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.money.randing.domain.model.Movement
import com.money.randing.ui.common.MovementClickListener
import com.money.randing.ui.common.MovementComparator

class MovementAdapter(
) : ListAdapter<Movement, MovementViewHolder>(MovementComparator) {
    private var onMovementClick: MovementClickListener? = null
    private var onMovementLongClick: MovementClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovementViewHolder {
        return MovementViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MovementViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        onMovementClick?.let {
            holder.setOnClickListener(item, it)
        }
        onMovementLongClick?.let {
            holder.setOnLongClickListener(item, it)
        }
    }

    fun setOnClickListener(l: MovementClickListener) {
        onMovementClick = l
    }

    fun setOnLongClickListener(l: MovementClickListener) {
        onMovementLongClick = l
    }
}