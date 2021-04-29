package com.money.randing.ui.people.home

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.money.randing.domain.model.Person
import com.money.randing.ui.common.PersonClickListener
import com.money.randing.ui.common.PersonComparator

class PersonAdapter(
    private val context: Context,
    private val onPersonClickListener: PersonClickListener
) : ListAdapter<Person, PersonViewHolder>(PersonComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        return PersonViewHolder.from(parent, context)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = getItem(position)
        holder.bind(person)
        holder.setOnPersonClickListener(onPersonClickListener)
    }
}