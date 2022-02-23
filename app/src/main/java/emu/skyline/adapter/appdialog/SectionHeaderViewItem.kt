/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright © 2022 Skyline Team and Contributors (https://github.com/skyline-emu/)
 */

package emu.skyline.adapter.appdialog

import android.view.View
import android.view.ViewGroup
import emu.skyline.adapter.GenericListItem
import emu.skyline.adapter.ViewBindingFactory
import emu.skyline.adapter.inflater
import emu.skyline.databinding.AppDialogSectionHeaderBinding

object AppDialogSectionHeaderBindingFactory : ViewBindingFactory {
    override fun createBinding(parent : ViewGroup) = AppDialogSectionHeaderBinding.inflate(parent.inflater(), parent, false)
}

class SectionHeaderViewItem(
    private val title : String,
    private val attribute : String? = null,
    private val onButtonClick : ((item : SectionHeaderViewItem, position : Int) -> Unit)? = null
) : GenericListItem<AppDialogSectionHeaderBinding>() {
    override fun getViewBindingFactory() = AppDialogSectionHeaderBindingFactory

    override fun bind(binding : AppDialogSectionHeaderBinding, position : Int) {
        binding.title.text = title
        if (attribute != null) {
            binding.subtitle.text = attribute
            binding.subtitle.visibility = View.VISIBLE
        } else {
            binding.subtitle.visibility = View.GONE
        }

        if (onButtonClick != null) {
            binding.button.setOnClickListener { onButtonClick.invoke(this, position) }
            binding.button.visibility = View.VISIBLE
        } else {
            binding.button.visibility = View.GONE
        }
    }
}
