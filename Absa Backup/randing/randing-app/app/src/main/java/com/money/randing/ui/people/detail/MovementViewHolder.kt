package com.money.randing.ui.people.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.money.randing.databinding.ViewItemMovementBinding
import com.money.randing.domain.model.Movement
import com.money.randing.ui.common.MovementClickListener
import com.money.randing.util.toCurrencyString
import com.money.randing.util.toDateString
import java.text.SimpleDateFormat
import kotlin.math.absoluteValue

class MovementViewHolder(
    private val binding: ViewItemMovementBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): MovementViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewItemMovementBinding.inflate(inflater, parent, false)
            return MovementViewHolder(binding)
        }
    }

    fun bind(movement: Movement) {
        val amountColor = ContextCompat.getColor(itemView.context, movement.type.color)
        binding.tvDate.text = movement.date.toDateString(SimpleDateFormat.SHORT)
        binding.tvAmount.text = movement.amount.absoluteValue.toCurrencyString()
        binding.tvAmount.setTextColor(amountColor)
        binding.tvDescription.text = movement.description
        binding.tvMovementType.text = itemView.context.getString(movement.type.name)
    }

    fun setOnClickListener(movement: Movement, l: MovementClickListener) {
        binding.container.setOnClickListener { l(movement) }
    }

    fun setOnLongClickListener(movement: Movement, l: MovementClickListener) {
        binding.container.setOnLongClickListener {
            l(movement)
            true
        }
    }
}