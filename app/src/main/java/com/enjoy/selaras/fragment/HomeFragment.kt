package com.enjoy.selaras.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.enjoy.selaras.R
import com.enjoy.selaras.adapter.JournalAdapter
import com.enjoy.selaras.database.JournalDatabase
import com.enjoy.selaras.databinding.FragmentHomeBinding
import com.enjoy.selaras.entities.Journal
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch


class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding
    private var journalAdapter = JournalAdapter { show -> showHideDelete(show) }
    private var journalList = ArrayList<Journal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private fun showHideDelete(show: Boolean) {
        binding.apply {
            if (show) {
                btnDelete.show()
                btnAddJournal.hide()
                gradientAppBarBg.setImageResource(com.enjoy.selaras.R.drawable.gradient_app_bar_bg_destructive)

                val typedValue = TypedValue()
                requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorErrorContainer, typedValue, true)
                val color = typedValue.data
                activity?.window?.navigationBarColor = color
            } else {
                btnDelete.hide()
                btnAddJournal.show()
                gradientAppBarBg.setImageResource(com.enjoy.selaras.R.drawable.gradient_app_bar_bg_default)

                val typedValue = TypedValue()
                requireContext().theme.resolveAttribute(com.google.android.material.R.attr.backgroundColor, typedValue, true)
                val color = typedValue.data
                activity?.window?.navigationBarColor = color
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        launch {
            context?.let {
                val journals = JournalDatabase.getDatabase(it).journalDao().getAllJournal()

                val activeQuery = binding.searchBar.query
                if (activeQuery.toString() != "") {
                    val find = journals.filter { journal ->
                        (journal.title?.lowercase()
                            ?.contains(activeQuery.toString()) == true || journal.content?.lowercase()
                            ?.contains(activeQuery.toString()) == true)
                    }
                    journalAdapter.setData(find)
                } else {
                    journalAdapter.setData(journals)
                }

                journalList = journals as ArrayList<Journal>
                recyclerView.adapter = journalAdapter
            }
        }

        journalAdapter.setOnClickListener(onClicked)

        binding.btnAddJournal.setOnClickListener {
            replaceFragment(CreateOrUpdateJournalFragment.newInstance())
        }

        binding.btnDelete.setOnClickListener {
            basicAlert()
        }

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val find = findJournals(p0.toString(), journalList)

                journalAdapter.setData(find)
                journalAdapter.notifyDataSetChanged()

                return true
            }

        })

    }

    private val onClicked = object : JournalAdapter.OnItemClickListener {
        override fun onClicked(journalId: Int) {
            val activeQuery = binding.searchBar.query
            if (activeQuery.isNotEmpty()) {
                binding.apply {
                    searchBar.clearFocus()
                }
            }

            val fragment: Fragment
            val bundle = Bundle()
            bundle.putInt("journalId", journalId)
            fragment = CreateOrUpdateJournalFragment.newInstance()
            fragment.arguments = bundle

            replaceFragment(fragment)
        }
    }

    private fun deleteJournals(idList: List<Int>) {
        launch {
            context?.let {
                val journalsToDelete = journalList.filter { journal -> idList.contains(journal.id) }

                if (journalsToDelete.isNotEmpty()) {
                    JournalDatabase.getDatabase(it).journalDao().deleteJournals(journalsToDelete)
                    journalAdapter.resetSelectedList()

                    val journals = JournalDatabase.getDatabase(it).journalDao().getAllJournal()

                    val activeQuery = binding.searchBar.query
                    if (activeQuery.toString() != "") {
                        val find =
                            findJournals(activeQuery.toString(), journals as ArrayList<Journal>)

                        journalAdapter.setData(find)
                    } else {
                        journalAdapter.setData(journals)
                    }

                    journalList = journals as ArrayList<Journal>
                    journalAdapter.notifyDataSetChanged()
                    showHideDelete(false)
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()

        with(fragmentTransition) {
            setCustomAnimations(
                androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom,
                androidx.appcompat.R.anim.abc_popup_exit,
                androidx.appcompat.R.anim.abc_popup_enter,
                androidx.appcompat.R.anim.abc_shrink_fade_out_from_bottom
            )
            replace(R.id.frame_layout, fragment)
            addToBackStack(fragment.javaClass.simpleName)
            commit()
        }
    }

    private fun basicAlert() {
        val builder = MaterialAlertDialogBuilder(binding.root.context, R.style.Theme_AlertDialog)

        val positiveButtonClick = { _: DialogInterface, _: Int ->
            deleteJournals(journalAdapter.getSelectedList())
        }

        val negativeButtonClick = { _: DialogInterface, _: Int -> }

        with(builder)
        {
            setTitle(
                "Kamu yakin ingin menghapus ".plus(journalAdapter.getSelectedList().size)
                    .plus(" jurnal?")
            )
            setMessage("Jurnal yang dipilih akan terhapus permanen dari perangkat kamu.")
            setPositiveButton("Hapus", DialogInterface.OnClickListener(positiveButtonClick))
            setNegativeButton("Batal", DialogInterface.OnClickListener(negativeButtonClick))
            show()
        }
    }

    private fun findJournals(keyword: String, journals: ArrayList<Journal>): List<Journal> {
        return journals.filter { journal ->
            (journal.title?.lowercase()
                ?.contains(keyword) == true || journal.content?.lowercase()
                ?.contains(keyword) == true) || journal.dateTime?.contains(keyword) == true
        }
    }
}