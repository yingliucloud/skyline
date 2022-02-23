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
import emu.skyline.databinding.AppDialogCheatsItemBinding

object AppDialogCheatsItemBindingFactory : ViewBindingFactory {
    override fun createBinding(parent : ViewGroup) = AppDialogCheatsItemBinding.inflate(parent.inflater(), parent, false)
}

class CheatsViewItem(
    private val title : String,
    private val author : String?,
    private val description : String?,
    private val code : String,
    var isEnabled : Boolean,
    private val onClick : ((item : CheatsViewItem, position : Int) -> Unit)? = null
) : GenericListItem<AppDialogCheatsItemBinding>() {
    companion object {
        const val MAX_LINES = 2
    }

    private var isExpandable : Boolean? = null
    private var isExpanded = false

    override fun getViewBindingFactory() = AppDialogCheatsItemBindingFactory

    override fun bind(binding : AppDialogCheatsItemBinding, position : Int) {
        binding.title.text = title
        author?.let {
            binding.author.text = author
            binding.author.visibility = View.VISIBLE
        } ?: run { binding.author.visibility = View.GONE }
        description?.let {
            binding.description.text = it
            binding.description.visibility = View.VISIBLE
        } ?: run { binding.description.visibility = View.GONE }
        binding.code.text = code
        binding.checkbox.isChecked = isEnabled

        handleExpand(binding, position)

        binding.checkbox.setOnCheckedChangeListener { _, isChecked : Boolean ->
            isEnabled = isChecked
            onClick?.invoke(this, position)
        }
    }

    private fun handleExpand(binding : AppDialogCheatsItemBinding, position : Int) {
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
                        binding.description.layout.getEllipsisCount(MAX_LINES - 1) > 0 ||
                        code.isNotEmpty()
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
            binding.code.visibility = View.VISIBLE
        } else {
            binding.expandButton.rotation = 0F
            binding.title.isSingleLine = true
            binding.author.isSingleLine = true
            binding.description.maxLines = MAX_LINES
            binding.code.visibility = View.GONE
        }

        setupExpandButton.invoke()
    }
}
