package com.enjoy.selaras.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.enjoy.selaras.database.JournalDatabase
import com.enjoy.selaras.databinding.FragmentCreateJournalBinding
import com.enjoy.selaras.entities.Journal
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateJournalFragment : BaseFragment() {
    private lateinit var binding: FragmentCreateJournalBinding
    private var currentDate: String? = null
    private var journalId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        journalId = requireArguments().getInt("journalId", -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateJournalBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateJournalFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (journalId != -1) {
            launch {
                context?.let {
                    val journal =
                        JournalDatabase.getDatabase(it).journalDao().getSpecificJournal(journalId)

                    binding.etTitle.setText(journal.title)
                    binding.etContent.setText(journal.content)
                    binding.tvDateTime.visibility = View.GONE
                    binding.headerTitle.text = journal.dateTime
                }
            }
        } else {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "id"))
            currentDate = sdf.format(Date())

            binding.tvDateTime.text = currentDate
        }

        binding.btnDone.setOnClickListener {
            binding.etTitle.clearFocus()
            binding.etContent.clearFocus()

            if (journalId != -1) {
                updateJournal()
            } else {
                saveJournal()
            }
        }

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun updateJournal() {
        launch {
            context?.let {
                val journal =
                    JournalDatabase.getDatabase(it).journalDao().getSpecificJournal(journalId)
                journal.title = binding.etTitle.text.toString()
                journal.content = binding.etContent.text.toString()

                JournalDatabase.getDatabase(it).journalDao().insertJournal(journal)

                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }


    private fun saveJournal() {
        if (binding.etTitle.text.isNullOrEmpty()) {
            Toast.makeText(context, "Judul wajib diisi", Toast.LENGTH_SHORT).show()

            return
        }

        launch {
            val journal = Journal()

            journal.title = binding.etTitle.text.toString()
            journal.content = binding.etContent.text.toString()
            journal.dateTime = currentDate

            context?.let {
                JournalDatabase.getDatabase(it).journalDao().insertJournal(journal)

                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }
}