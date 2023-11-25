package com.enjoy.selaras.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.enjoy.selaras.R
import com.enjoy.selaras.adapter.JournalAdapter
import com.enjoy.selaras.database.JournalDatabase
import com.enjoy.selaras.databinding.FragmentHomeBinding
import com.enjoy.selaras.entities.Journal
import kotlinx.coroutines.launch
import java.util.Locale

class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding
    private var journalAdapter = JournalAdapter()
    private var journalList = ArrayList<Journal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        launch {
            context?.let {
                var journals = JournalDatabase.getDatabase(it).journalDao().getAllJournal()
                journalAdapter!!.setData(journals)
                journalList = journals as ArrayList<Journal>
                recyclerView.adapter = journalAdapter
            }
        }

        journalAdapter!!.setOnClickListener(onClicked)

        binding.addJournalButton.setOnClickListener {
            replaceFragment(CreateJournalFragment.newInstance(), true)
        }

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                var tempArr = journalList.filter {
                    (it.title?.lowercase()
                        ?.contains(p0.toString()) == true || it.content?.lowercase()
                        ?.contains(p0.toString()) == true)
                }

                journalAdapter.setData(tempArr)
                journalAdapter.notifyDataSetChanged()

                return true
            }

        })

    }

    private val onClicked = object : JournalAdapter.OnItemClickListener {
        override fun onClicked(journalId: Int) {
            var fragment: Fragment
            var bundle = Bundle()
            bundle.putInt("journalId", journalId)
            fragment = CreateJournalFragment.newInstance()
            fragment.arguments = bundle

            replaceFragment(fragment, true)
        }

    }

    private fun replaceFragment(fragment: Fragment, transition: Boolean) {
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()

        if (transition) {
            fragmentTransition.setCustomAnimations(
                androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom,
                androidx.appcompat.R.anim.abc_popup_exit,
                androidx.appcompat.R.anim.abc_popup_enter,
                androidx.appcompat.R.anim.abc_shrink_fade_out_from_bottom
            )
        }

        fragmentTransition.replace(R.id.frame_layout, fragment)
            .addToBackStack(fragment.javaClass.simpleName).commit()
    }
}