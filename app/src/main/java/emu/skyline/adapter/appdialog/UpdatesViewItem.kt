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
import emu.skyline.databinding.AppDialogUpdatesItemBinding

object AppDialogUpdatesItemBindingFactory : ViewBindingFactory {
    override fun createBinding(parent : ViewGroup) = AppDialogUpdatesItemBinding.inflate(parent.inflater(), parent, false)
}

class UpdatesViewItem(
    private val title : String,
    private val onClick : ((item : UpdatesViewItem, position : Int) -> Unit)? = null
) : GenericListItem<AppDialogUpdatesItemBinding>() {
    override fun getViewBindingFactory() = AppDialogUpdatesItemBindingFactory

    override fun bind(binding : AppDialogUpdatesItemBinding, position : Int) {
        binding.radioButton.text = title
        binding.radioButton.isChecked = adapter?.selectedPosition == position
        binding.radioButton.isSelected = true

        binding.radioButton.setOnCheckedChangeListener { _ : View, isChecked : Boolean ->
            if (isChecked) {
                adapter?.selectedPosition = position
                onClick?.invoke(this, position)
                adapter?.itemCount?.let { adapter?.notifyItemRangeChanged(0, it) }
            }
        }
    }
}
