/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright © 2022 Skyline Team and Contributors (https://github.com/skyline-emu/)
 */

package emu.skyline.adapter.appdialog

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import emu.skyline.adapter.GenericListItem
import emu.skyline.adapter.ViewBindingFactory
import emu.skyline.adapter.inflater
import emu.skyline.databinding.AppDialogIssuesItemBinding

object AppDialogIssuesItemBindingFactory : ViewBindingFactory {
    override fun createBinding(parent : ViewGroup) = AppDialogIssuesItemBinding.inflate(parent.inflater(), parent, false)
}

class IssuesViewItem(private val title : String, private val description : String?) : GenericListItem<AppDialogIssuesItemBinding>() {
    private var isExpandable : Boolean? = null
    private var isExpanded = false

    override fun getViewBindingFactory() = AppDialogIssuesItemBindingFactory

    override fun bind(binding : AppDialogIssuesItemBinding, position : Int) {
        binding.title.text = title
        description?.let {
            binding.description.text = it
            binding.description.visibility = View.VISIBLE
        } ?: run { binding.description.visibility = View.GONE }

        handleExpand(binding, position)
    }

    private fun handleExpand(binding : AppDialogIssuesItemBinding, position : Int) {
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
                isExpandable = binding.title.layout.getEllipsisCount(0) > 0 || description?.isNotEmpty() == true
                setupExpandButton.invoke()
                binding.root.viewTreeObserver.removeOnPreDrawListener(this)
                return true
            }
        })

        if (isExpanded) {
            binding.expandButton.rotation = 180F
            binding.title.isSingleLine = false
            binding.description.visibility = View.VISIBLE
        } else {
            binding.expandButton.rotation = 0F
            binding.title.isSingleLine = true
            binding.description.visibility = View.GONE
        }

        setupExpandButton.invoke()
    }
}
