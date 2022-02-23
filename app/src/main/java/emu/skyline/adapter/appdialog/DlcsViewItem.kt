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
import emu.skyline.databinding.AppDialogDlcsItemBinding

object AppDialogDlcsItemBindingFactory : ViewBindingFactory {
    override fun createBinding(parent : ViewGroup) = AppDialogDlcsItemBinding.inflate(parent.inflater(), parent, false)
}

class DlcsViewItem(
    private val title : String,
    var isEnabled : Boolean,
    private val onClick : ((item : DlcsViewItem, position : Int) -> Unit)? = null
) : GenericListItem<AppDialogDlcsItemBinding>() {
    override fun getViewBindingFactory() = AppDialogDlcsItemBindingFactory

    override fun bind(binding : AppDialogDlcsItemBinding, position : Int) {
        binding.checkbox.text = title
        binding.checkbox.isChecked = isEnabled
        binding.checkbox.isSelected = true

        binding.checkbox.setOnCheckedChangeListener { _ : View, isChecked : Boolean ->
            isEnabled = isChecked
            onClick?.invoke(this, position)
        }
    }
}
