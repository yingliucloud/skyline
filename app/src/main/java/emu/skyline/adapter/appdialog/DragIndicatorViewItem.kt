/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright © 2022 Skyline Team and Contributors (https://github.com/skyline-emu/)
 */

package emu.skyline.adapter.appdialog

import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import emu.skyline.adapter.GenericListItem
import emu.skyline.adapter.ViewBindingFactory
import emu.skyline.adapter.inflater
import emu.skyline.databinding.AppDialogDragIndicatorBinding

object AppDialogDragIndicatorBindingFactory : ViewBindingFactory {
    override fun createBinding(parent : ViewGroup) = AppDialogDragIndicatorBinding.inflate(parent.inflater(), parent, false)
}

class DragIndicatorViewItem(private val behavior : BottomSheetBehavior<View>) : GenericListItem<AppDialogDragIndicatorBinding>() {
    override fun getViewBindingFactory() = AppDialogDragIndicatorBindingFactory

    override fun bind(binding : AppDialogDragIndicatorBinding, position : Int) {
        behavior.addBottomSheetCallback(binding.dragIndicator.callback)
    }
}
