package com.enjoy.selaras.fragments

import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.enjoy.selaras.R
import com.enjoy.selaras.database.JournalDatabase
import com.enjoy.selaras.databinding.FragmentCreateJournalBinding
import com.enjoy.selaras.entities.Journal
import com.enjoy.selaras.helpers.RetrofitHelper
import com.enjoy.selaras.interfaces.EmotionApi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateOrUpdateJournalFragment : BaseFragment() {
    private lateinit var binding: FragmentCreateJournalBinding
    private var currentDate: String? = null
    private var journalId = -1
    private lateinit var emotionApi: EmotionApi
    private lateinit var loadingDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        journalId = requireArguments().getInt("journalId", -1)
        emotionApi = RetrofitHelper.getInstance().create(EmotionApi::class.java)
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
            CreateOrUpdateJournalFragment().apply {
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
            if (journalId != -1) {
                updateJournal()
            } else {
                saveJournal()
            }
        }

        binding.btnBack.setOnClickListener {
            binding.etTitle.clearFocus()
            binding.etContent.clearFocus()

            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun updateJournal() {
        startLoadingDialog()
        binding.etTitle.clearFocus()
        binding.etContent.clearFocus()

        launch {
            context?.let {
                val journal =
                    JournalDatabase.getDatabase(it).journalDao().getSpecificJournal(journalId)

                var emotion = ""
                if (binding.etTitle.text.toString() != journal.content) {
                    val result = emotionApi.getEmotion(
                        URLEncoder.encode(binding.etContent.text.toString(), "UTF-8").toString()
                    )


                    if (result.isSuccessful) {
                        emotion = result.body()!!.label
                    }
                }

                journal.title = binding.etTitle.text.toString()
                journal.content = binding.etContent.text.toString()
                journal.emotion = emotion

                JournalDatabase.getDatabase(it).journalDao().insertJournal(journal)

                dismissLoadingDialog()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }


    private fun saveJournal() {
        startLoadingDialog()
        binding.etTitle.clearFocus()
        binding.etContent.clearFocus()

        if (binding.etTitle.text.isNullOrEmpty()) {
            Toast.makeText(context, "Judul wajib diisi", Toast.LENGTH_SHORT).show()

            return
        }

        launch {
            context?.let {
                val result = emotionApi.getEmotion(
                    URLEncoder.encode(binding.etContent.text.toString(), "UTF-8").toString()
                )

                var emotion = ""
                if (result.isSuccessful) {
                    emotion = result.body()!!.label
                }

                val journal = Journal()

                journal.title = binding.etTitle.text.toString()
                journal.content = binding.etContent.text.toString()
                journal.dateTime = currentDate
                journal.emotion = emotion

                JournalDatabase.getDatabase(it).journalDao().insertJournal(journal)

                dismissLoadingDialog()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }


    private fun startLoadingDialog() {
        val builder = MaterialAlertDialogBuilder(binding.root.context, R.style.Theme_AlertDialog)

        with(builder) {
            setView(com.enjoy.selaras.R.layout.loading_dialog)
            setCancelable(false)

            loadingDialog = create()
        }

        loadingDialog.show()
    }

    private fun dismissLoadingDialog() {
        loadingDialog.dismiss()
    }
}