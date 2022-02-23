/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright © 2022 Skyline Team and Contributors (https://github.com/skyline-emu/)
 */

package emu.skyline.adapter.appdialog

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.toBitmap
import emu.skyline.EmulationActivity
import emu.skyline.R
import emu.skyline.adapter.GenericListItem
import emu.skyline.adapter.ViewBindingFactory
import emu.skyline.adapter.inflater
import emu.skyline.data.AppItem
import emu.skyline.databinding.AppDialogGameInfoBinding
import emu.skyline.loader.LoaderResult

object ControllerBindingFactory : ViewBindingFactory {
    override fun createBinding(parent : ViewGroup) = AppDialogGameInfoBinding.inflate(parent.inflater(), parent, false)
}

class GameInfoViewItem(private val context : Context, private val item : AppItem, private val testedVersion : String?, private val rating : Int?) : GenericListItem<AppDialogGameInfoBinding>() {
    override fun getViewBindingFactory() = ControllerBindingFactory

    override fun bind(binding : AppDialogGameInfoBinding, position : Int) {
        val missingIcon = context.getDrawable(R.drawable.default_icon)!!.toBitmap(256, 256)

        binding.gameIcon.setImageBitmap(item.icon ?: missingIcon)
        binding.gameTitle.text = item.title
        binding.gameSubtitle.text = item.subTitle ?: item.loaderResultString(context)
        // Make the title text view selected for marquee to work
        binding.gameTitle.isSelected = true
        binding.gameSubtitle.isSelected = true

        binding.flex.visibility = if (rating == null && testedVersion == null) View.INVISIBLE else View.VISIBLE
        binding.ratingBar.rating = (rating ?: 0).toFloat()
        binding.testedVersion.text = testedVersion?.let { context.getString(R.string.tested_on, it) } ?: context.getString(R.string.not_tested)

        binding.gamePlay.isEnabled = item.loaderResult == LoaderResult.Success
        binding.gamePlay.setOnClickListener {
            context.startActivity(Intent(context, EmulationActivity::class.java).apply { data = item.uri })
        }

        val shortcutManager = context.getSystemService<ShortcutManager>()!!
        binding.gamePin.isEnabled = shortcutManager.isRequestPinShortcutSupported

        binding.gamePin.setOnClickListener {
            val info = ShortcutInfo.Builder(context, item.title)
            info.setShortLabel(item.title)
            info.setActivity(ComponentName(context, EmulationActivity::class.java))
            info.setIcon(Icon.createWithAdaptiveBitmap(item.icon ?: missingIcon))

            val intent = Intent(context, EmulationActivity::class.java)
            intent.data = item.uri
            intent.action = Intent.ACTION_VIEW

            info.setIntent(intent)

            shortcutManager.requestPinShortcut(info.build(), null)
        }
    }
}
