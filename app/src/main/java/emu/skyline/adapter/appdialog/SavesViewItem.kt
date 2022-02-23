/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright © 2022 Skyline Team and Contributors (https://github.com/skyline-emu/)
 */

package emu.skyline.adapter.appdialog

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import emu.skyline.adapter.GenericListItem
import emu.skyline.adapter.ViewBindingFactory
import emu.skyline.adapter.inflater
import emu.skyline.databinding.AppDialogSavesItemBinding

object AppDialogSavesItemBindingFactory : ViewBindingFactory {
    override fun createBinding(parent : ViewGroup) = AppDialogSavesItemBinding.inflate(parent.inflater(), parent, false)
}

class SavesViewItem(
    private val title : String,
    private val author : String,
    private val description : String,
    private val onClick : ((item : SavesViewItem, position : Int) -> Unit)? = null
) : GenericListItem<AppDialogSavesItemBinding>() {
    companion object {
        const val MAX_LINES = 2
    }

    private var isExpandable : Boolean? = null
    private var isExpanded = false
    private var isActive = false

    override fun getViewBindingFactory() = AppDialogSavesItemBindingFactory

    override fun bind(binding : AppDialogSavesItemBinding, position : Int) {
        binding.title.text = title
        binding.author.text = author
        binding.description.text = description
        binding.progress.isIndeterminate = true // temp

        handleExpand(binding, position)
    }

    private fun handleExpand(binding : AppDialogSavesItemBinding, position : Int) {
        val setupExpandButton = {
            if (isExpandable == true) {
                binding.expandButton.visibility = View.VISIBLE
                binding.root.setOnClickListener {
                    isExpanded = !isExpanded
                    adapter?.notifyItemChanged(position)
                }
            } else {
                binding.expandButton.visibility = View.GONE
            }
        }

        isExpandable ?: binding.root.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw() : Boolean {
                isExpandable = binding.title.layout.getEllipsisCount(0) > 0 ||
                        binding.author.layout.getEllipsisCount(0) > 0 ||
                        binding.description.layout.getEllipsisCount(MAX_LINES - 1) > 0
                setupExpandButton.invoke()
                binding.root.viewTreeObserver.removeOnPreDrawListener(this)
                return true
            }
        })

        if (isExpanded) {
            binding.expandButton.rotation = 180F
            binding.title.isSingleLine = false
            binding.author.isSingleLine = false
            binding.description.maxLines = Int.MAX_VALUE
        } else {
            binding.expandButton.rotation = 0F
            binding.title.isSingleLine = true
            binding.author.isSingleLine = true
            binding.description.maxLines = MAX_LINES
        }

        setupExpandButton.invoke()
    }
}
