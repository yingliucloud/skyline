/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright © 2022 Skyline Team and Contributors (https://github.com/skyline-emu/)
 */

package emu.skyline.adapter.appdialog

import android.view.ViewGroup
import android.view.ViewTreeObserver
import emu.skyline.adapter.GenericListItem
import emu.skyline.adapter.ViewBindingFactory
import emu.skyline.adapter.inflater
import emu.skyline.databinding.AppDialogNotesItemBinding

object AppDialogNotesItemBindingFactory : ViewBindingFactory {
    override fun createBinding(parent : ViewGroup) = AppDialogNotesItemBinding.inflate(parent.inflater(), parent, false)
}

class NotesViewItem(private val title : String) : GenericListItem<AppDialogNotesItemBinding>() {
    companion object {
        const val MAX_LINES = 3
    }

    private var isExpandable : Boolean? = null
    private var isExpanded = false

    override fun getViewBindingFactory() = AppDialogNotesItemBindingFactory

    override fun bind(binding : AppDialogNotesItemBinding, position : Int) {
        binding.title.text = title

        handleExpand(binding, position)
    }

    private fun handleExpand(binding : AppDialogNotesItemBinding, position : Int) {
        val setupExpandButton = {
            if (isExpandable == true) {
                binding.root.setOnClickListener {
                    isExpanded = !isExpanded
                    adapter?.notifyItemChanged(position)
                }
            }
        }

        isExpandable ?: binding.root.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw() : Boolean {
                isExpandable = binding.title.layout.getEllipsisCount(MAX_LINES - 1) > 0
                setupExpandButton.invoke()
                binding.root.viewTreeObserver.removeOnPreDrawListener(this)
                return true
            }
        })

        binding.title.maxLines = if (isExpanded) Int.MAX_VALUE else MAX_LINES

        setupExpandButton.invoke()
    }
}
