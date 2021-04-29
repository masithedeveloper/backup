package com.money.randing.ui.summary

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.money.randing.domain.model.Person
import com.money.randing.ui.common.PersonClickListener
import com.money.randing.ui.common.PersonComparator

class PersonSummaryAdapter(
    private val context: Context,
    private val onPersonClickListener: PersonClickListener
) : ListAdapter<Person, PersonSummaryViewHolder>(PersonComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonSummaryViewHolder {
        return PersonSummaryViewHolder.from(parent, context)
    }

    override fun onBindViewHolder(holder: PersonSummaryViewHolder, position: Int) {
        val person = getItem(position)
        holder.bind(person)
        holder.setOnPersonClickListener(onPersonClickListener)
    }
}