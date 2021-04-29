package com.money.randing.ui.summary

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.money.randing.R
import com.money.randing.databinding.ViewItemPersonBinding
import com.money.randing.domain.model.Person
import com.money.randing.ui.common.PersonClickListener
import com.money.randing.util.toCurrencyString
import kotlin.math.absoluteValue

class PersonSummaryViewHolder(
    private val binding: ViewItemPersonBinding,
    private val context: Context,
) : RecyclerView.ViewHolder(binding.root) {

    private var clickListener: PersonClickListener? = null

    companion object {
        fun from(parent: ViewGroup, context: Context): PersonSummaryViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ViewItemPersonBinding.inflate(inflater, parent, false)
            return PersonSummaryViewHolder(binding, context)
        }
    }

    fun bind(person: Person) {
        binding.tvName.text = person.name
        binding.tvAmount.text = if (person.total != null) {
            person.total.absoluteValue.toCurrencyString()
        } else context.getString(R.string.no_movements)

        if (person.picture != null) {
            binding.image.setImageBitmap(person.picture)
        } else {
            val d =
                ResourcesCompat.getDrawable(context.resources, R.drawable.avatar_placeholder, null)
            binding.image.setImageDrawable(d)
        }

        binding.tvIndicator.isVisible = person.total != null
        val indicator = person.indicator
        binding.tvAmount.setTextColor(context.getColor(indicator.colorId))
        binding.tvIndicator.text = context.getString(indicator.stringId)

        binding.container.setOnClickListener {
            clickListener?.invoke(person)
        }
    }

    fun setOnPersonClickListener(l: PersonClickListener) {
        clickListener = l
    }
}