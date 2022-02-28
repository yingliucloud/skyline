/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright © 2020 Skyline Team and Contributors (https://github.com/skyline-emu/)
 */

package emu.skyline

import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.*
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import emu.skyline.adapter.GenericAdapter
import emu.skyline.adapter.GenericListItem
import emu.skyline.adapter.appdialog.*
import emu.skyline.data.AppItem
import emu.skyline.databinding.AppDialogBinding
import emu.skyline.network.TitleMetaData
import emu.skyline.network.TitleMetaService
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * This dialog is used to show extra game metadata and provide extra options such as pinning the game to the home screen
 */
class AppDialog : BottomSheetDialogFragment() {
    companion object {
        /**
         * @param item This is used to hold the [AppItem] between instances
         */
        fun newInstance(item : AppItem) : AppDialog {
            val args = Bundle()
            args.putSerializable("item", item)

            val fragment = AppDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var binding : AppDialogBinding

    private val baseAdapter = ConcatAdapter()
    private val gameInfoAdapter = GenericAdapter()
    private val updatesAdapter = GenericAdapter()
    private val dlcsAdapter = GenericAdapter()
    private val tlmdAdapter = GenericAdapter()

    private val item by lazy { requireArguments().getSerializable("item") as AppItem }

    /**
     * This inflates the layout of the dialog after initial view creation
     */
    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) = AppDialogBinding.inflate(inflater).also { binding = it }.root

    /**
     * This expands the bottom sheet so that it's fully visible and map the B button to back
     */
    override fun onStart() {
        super.onStart()

        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BUTTON_B && event.action == KeyEvent.ACTION_UP) {
                dialog?.onBackPressed()
                return@setOnKeyListener true
            }
            false
        }
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseAdapter.apply {
            addAdapter(gameInfoAdapter)
            addAdapter(updatesAdapter)
            addAdapter(dlcsAdapter)
            addAdapter(tlmdAdapter)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/skyline-emu/title-meta/")
            .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
            .build()

        val service : TitleMetaService = retrofit.create(TitleMetaService::class.java)

        service.getData(item.title).enqueue(object : Callback<TitleMetaData> {
            override fun onResponse(call : Call<TitleMetaData>, response : Response<TitleMetaData>) {
                //response.body()?.let { populateAdaptersAfterData(it) }
                TODO("NOT")
            }

            override fun onFailure(call : Call<TitleMetaData>, t : Throwable) {
                TODO("Not yet implemented")
            }
        })

        populateAdaptersBeforeData()

        binding.content.apply {
            addItemDecoration(SpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.section_spacing)))
            adapter = baseAdapter
        }
    }

    private fun populateAdaptersBeforeData() {
        populateGameInfoAdapter(null)
        //populateUpdatesAdapter()
        //populateDlcsAdapter()
    }

    private fun populateAdaptersAfterData(data : TitleMetaData) {
        populateGameInfoAdapter(data)
        populateTlmdAdapter(data)
    }

    private fun populateGameInfoAdapter(data : TitleMetaData?) {
        val entries : MutableList<GenericListItem<out ViewBinding>> = ArrayList()

        entries.apply {
            add(DragIndicatorViewItem(BottomSheetBehavior.from(requireView().parent as View)))
            add(GameInfoViewItem(requireActivity(), item, data?.version, data?.rating))
        }

        gameInfoAdapter.setItems(entries)
    }

    private fun populateUpdatesAdapter() {
        val entries : MutableList<GenericListItem<out ViewBinding>> = ArrayList()

        entries.apply {
            add(SectionHeaderViewItem(requireContext().getString(R.string.updates)))
            add(UpdatesViewItem("TODO base_version"))
        }

        updatesAdapter.apply {
            selectedPosition = 0 + 1
            setItems(entries)
        }
    }

    private fun populateDlcsAdapter() {
        val entries : MutableList<GenericListItem<out ViewBinding>> = ArrayList()

        entries.apply {
            add(SectionHeaderViewItem(requireContext().getString(R.string.dlcs)))
        }

        dlcsAdapter.setItems(entries)
    }

    private fun populateTlmdAdapter(data : TitleMetaData) {
        val entries : MutableList<GenericListItem<out ViewBinding>> = ArrayList()

        entries.apply {
            data.issues?.let { issues ->
                add(SectionHeaderViewItem(requireContext().getString(R.string.issues)) { _, _ ->
                    Snackbar.make(this@AppDialog.requireView(), data.discussion, Snackbar.LENGTH_SHORT).show()
                })
                issues.forEach { issue ->
                    add(IssuesViewItem(issue.title, issue.description))
                }
            }

            data.notes?.let { notes ->
                add(SectionHeaderViewItem(requireContext().getString(R.string.notes)))
                notes.forEach { note ->
                    add(NotesViewItem(note))
                }
            }

            data.cheats?.let { cheats ->
                add(SectionHeaderViewItem(requireContext().getString(R.string.cheats)))
                cheats.forEach { (key, cheat) ->
                    add(CheatsViewItem(cheat.title, cheat.author, cheat.description, cheat.code, false))
                }
            }
        }
        tlmdAdapter.setItems(entries)
    }

    private inner class SpacingItemDecoration(private val padding : Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect : Rect, view : View, parent : RecyclerView, state : RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.set(0, 0, 0, padding)
        }
    }

    private fun getResString(@StringRes resId : Int) = requireContext().getString(resId)
}
