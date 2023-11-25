package com.enjoy.selaras.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.enjoy.selaras.databinding.ItemRvJournalsBinding
import com.enjoy.selaras.entities.Journal

class JournalAdapter() :
    RecyclerView.Adapter<JournalAdapter.JournalViewHolder>() {
    private var listener: OnItemClickListener? = null
    private var journals = ArrayList<Journal>()
    private lateinit var binding: ItemRvJournalsBinding;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        binding = ItemRvJournalsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JournalViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return journals.size
    }

    fun setData(journalList: List<Journal>){
        journals = journalList as ArrayList<Journal>
    }

    fun setOnClickListener(listener1: OnItemClickListener){
        listener = listener1
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        val journal = journals[position]
        holder.bind(journal)

        binding.apply {
            cardView.setOnClickListener {
                listener?.onClicked(journal.id!!)
            }
        }
    }

    class JournalViewHolder(private val binding: ItemRvJournalsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(journal: Journal) {
            binding.apply {
                tvTitle.text = journal.title
                tvDateTime.text = journal.dateTime

                if (journal.content!!.length > 150) {
                    tvContent.text = journal.content!!.substring(0, 150).plus("...")
                } else {
                    tvContent.text = journal.content
                }
            }
        }
    }

    interface OnItemClickListener{
        fun onClicked(journalId:Int)
    }
}