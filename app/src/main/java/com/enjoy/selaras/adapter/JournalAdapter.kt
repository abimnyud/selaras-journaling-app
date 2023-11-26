package com.enjoy.selaras.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.enjoy.selaras.R
import com.enjoy.selaras.databinding.ItemRvJournalsBinding
import com.enjoy.selaras.entities.Journal
import com.google.android.material.color.MaterialColors

class JournalAdapter(val showHideDelete: (Boolean) -> Unit) :
    RecyclerView.Adapter<JournalAdapter.JournalViewHolder>() {
    private var clickListener: OnItemClickListener? = null
    private var journals = ArrayList<Journal>()
    private lateinit var binding: ItemRvJournalsBinding;
    private var selectedList = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        binding = ItemRvJournalsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JournalViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return journals.size
    }

    fun getSelectedList(): List<Int> {
        return selectedList
    }

    fun resetSelectedList() {
        selectedList.clear()
    }

    fun setData(journalList: List<Journal>) {
        journals = journalList as ArrayList<Journal>
    }

    fun setOnClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        holder.bind(journals[position], position, clickListener)
    }

    private fun deselectItem(journal: Journal, position: Int): Boolean {
        selectedList.remove(journal.id)

        journals[position].selected = false
        notifyItemChanged(position)

        showHideDelete(selectedList.isNotEmpty())

        return true
    }

    fun markSelectedItem(journal: Journal, position: Int): Boolean {
        if (selectedList.contains(journal.id)) {
            deselectItem(journal, position)

            return true
        }

        selectedList.add(journal.id!!)

        journal.selected = true

        notifyItemChanged(position)
        showHideDelete(true)

        return true
    }

    inner class JournalViewHolder(private val binding: ItemRvJournalsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(journal: Journal, position: Int, clickListener: OnItemClickListener?) {
            binding.apply {
                tvDateTime.text = journal.dateTime

                if (journal.title!!.trim().length > 50) {
                    tvTitle.text = journal.title!!.trim().substring(0, 36).plus("...")
                } else {
                    tvTitle.text = journal.title
                }

                if (journal.content!!.trim().length > 150) {
                    tvContent.text = journal.content!!.trim().substring(0, 150).plus("...")
                } else {
                    tvContent.text = journal.content
                }

                if (journal.selected) {
                    cardView.setCardBackgroundColor(MaterialColors.getColor(binding.cardView, com.google.android.material.R.attr.colorTertiaryContainer))
                    tvContent.setTextColor(MaterialColors.getColor(binding.cardView, com.google.android.material.R.attr.colorOnTertiaryContainer))
                    tvTitle.setTextColor(MaterialColors.getColor(binding.cardView, com.google.android.material.R.attr.colorOnTertiaryContainer))
                    tvDateTime.setTextColor(MaterialColors.getColor(binding.cardView, com.google.android.material.R.attr.colorOnTertiaryContainer))
                } else {
                    cardView.setCardBackgroundColor(MaterialColors.getColor(binding.cardView, com.google.android.material.R.attr.colorSurfaceContainerHighest))
                    tvContent.setTextColor(MaterialColors.getColor(binding.cardView, com.google.android.material.R.attr.colorOnSurface))
                    tvTitle.setTextColor(MaterialColors.getColor(binding.cardView, com.google.android.material.R.attr.colorOnSurface))
                    tvDateTime.setTextColor(MaterialColors.getColor(binding.cardView, com.google.android.material.R.attr.colorOnSurface))
                }

                cardView.setOnClickListener {
                    if (selectedList.isNotEmpty()) {
                        markSelectedItem(journal, position)
                    } else {
                        selectedList.clear()
                        clickListener!!.onClicked(journal.id!!)
                    }
                }

                cardView.setOnLongClickListener { markSelectedItem(journal, position) }
            }
        }
    }

    interface OnItemClickListener {
        fun onClicked(journalId: Int)
    }
}