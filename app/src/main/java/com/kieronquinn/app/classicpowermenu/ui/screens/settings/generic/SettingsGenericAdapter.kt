package com.kieronquinn.app.classicpowermenu.ui.screens.settings.generic

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.classicpowermenu.databinding.*
import com.kieronquinn.app.classicpowermenu.model.settings.SettingsItem
import com.kieronquinn.app.classicpowermenu.model.settings.SettingsItemType
import com.kieronquinn.app.classicpowermenu.utils.Links
import com.kieronquinn.app.classicpowermenu.utils.MultiTapDetector
import com.kieronquinn.app.classicpowermenu.utils.extensions.applyMonet
import com.kieronquinn.app.classicpowermenu.utils.openLink
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.views.applyMonet

class SettingsGenericAdapter(context: Context, private var items: List<SettingsItem>, var isAdapterEnabled: Boolean): RecyclerView.Adapter<SettingsGenericAdapter.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val layoutInflater by lazy {
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    private val monet by lazy {
        MonetCompat.getInstance()
    }

    private val visibleItems
        get() = items.filter { it.visible.invoke() }

    override fun getItemCount(): Int = visibleItems.size

    override fun getItemViewType(position: Int): Int {
        return visibleItems[position].itemType.ordinal
    }

    override fun getItemId(position: Int): Long {
        return when(val item = visibleItems[position]){
            is SettingsItem.SwitchSetting -> item.title.hashCode().toLong()
            is SettingsItem.Setting -> item.title.hashCode().toLong()
            is SettingsItem.Warning -> item.title.hashCode().toLong()
            is SettingsItem.Header -> item.title.hashCode().toLong()
            is SettingsItem.AboutSetting -> item.title.hashCode().toLong()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(SettingsItemType.values()[viewType]){
            SettingsItemType.HEADER -> ViewHolder.Header(
                ItemHeaderBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
            SettingsItemType.WARNING -> ViewHolder.SettingsWarning(
                ItemSettingWarningBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
            SettingsItemType.SETTING -> ViewHolder.SettingsSetting(
                ItemSettingBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
            SettingsItemType.ABOUT_SETTING -> ViewHolder.SettingsAboutSetting(ItemSettingAboutBinding.inflate(layoutInflater, parent, false))
            SettingsItemType.SWITCH_SETTING -> ViewHolder.SettingsSwitchSetting(
                ItemSettingSwitchBinding.inflate(layoutInflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = visibleItems[holder.bindingAdapterPosition]
        when(holder){
            is ViewHolder.Header -> setupHeader(holder.binding, item as SettingsItem.Header)
            is ViewHolder.SettingsWarning -> setupWarning(holder.binding, item as SettingsItem.Warning)
            is ViewHolder.SettingsSetting -> setupSetting(holder.binding, item as SettingsItem.Setting)
            is ViewHolder.SettingsAboutSetting -> setupTripleTapActionSetting(holder.binding, item as SettingsItem.AboutSetting)
            is ViewHolder.SettingsSwitchSetting -> setupSettingSwitch(holder.binding, item as SettingsItem.SwitchSetting)
        }
    }

    private fun setupHeader(binding: ItemHeaderBinding, item: SettingsItem.Header) = with(binding) {
        itemHeadingTitle.text = item.title
        itemHeadingTitle.setTextColor(monet.getAccentColor(itemHeadingTitle.context))
    }

    private fun setupSetting(binding: ItemSettingBinding, item: SettingsItem.Setting) = with(binding) {
        val isEnabled = isAdapterEnabled && item.enabled()
        root.alpha = if(isEnabled) 1f else 0.5f
        itemSettingTitle.text = item.title
        if(item.content.isNullOrEmpty()){
            itemSettingContent.isVisible = false
        }else{
            itemSettingContent.isVisible = true
            itemSettingContent.text = item.content
        }
        if(item.icon != 0) {
            itemSettingIcon.setImageResource(item.icon)
        }else{
            itemSettingIcon.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        root.isEnabled = isEnabled
        item.tapAction?.let { action ->
            if(isEnabled) {
                root.setOnClickListener {
                    action.invoke()
                }
            }else{
                root.setOnClickListener(null)
            }
        }
        if(!item.centerIconVertically){
            binding.root.gravity = Gravity.NO_GRAVITY
        }
    }

    private fun setupWarning(binding: ItemSettingWarningBinding, item: SettingsItem.Warning) = with(binding) {
        val isEnabled = isAdapterEnabled && item.enabled()
        root.alpha = if(isEnabled) 1f else 0.5f
        root.backgroundTintList = ColorStateList.valueOf(monet.getPrimaryColor(root.context, false))
        itemSettingWarningTitle.text = item.title
        if(item.content.isNullOrEmpty()){
            itemSettingWarningContent.isVisible = false
        }else{
            itemSettingWarningContent.isVisible = true
            itemSettingWarningContent.text = item.content
        }
        if(item.icon != 0) {
            itemSettingWarningIcon.setImageResource(item.icon)
        }else{
            itemSettingWarningIcon.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        root.isEnabled = isEnabled
        item.tapAction?.let { action ->
            if(isEnabled) {
                root.setOnClickListener {
                    action.invoke()
                }
            }else{
                root.setOnClickListener(null)
            }
        }
        if(!item.centerIconVertically){
            binding.root.gravity = Gravity.NO_GRAVITY
        }
    }

    private fun setupTripleTapActionSetting(binding: ItemSettingAboutBinding, item: SettingsItem.AboutSetting) = with(binding) {
        itemSettingTitle.text = item.title
        if(item.content.isNullOrEmpty()){
            itemSettingContent.isVisible = false
        }else{
            itemSettingContent.isVisible = true
            itemSettingContent.text = item.content
        }
        if(item.icon != 0) {
            itemSettingIcon.setImageResource(item.icon)
        }else{
            itemSettingIcon.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        item.tripleTapAction?.let { action ->
            MultiTapDetector(root){ tapCount, lastTap ->
                if(lastTap && tapCount >= 3){
                    action.invoke()
                }
            }
        }
        val background = monet.getBackgroundColorSecondary(root.context) ?: monet.getPrimaryColor(root.context)
        arrayOf(chipDonate, chipTwitter, chipGithub, chipXda).forEach {
            it.chipBackgroundColor = ColorStateList.valueOf(background)
        }
        chipDonate.setOnClickListener {
            it.context.openLink(Links.LINK_DONATE)
        }
        chipTwitter.setOnClickListener {
            it.context.openLink(Links.LINK_TWITTER)
        }
        chipGithub.setOnClickListener {
            it.context.openLink(Links.LINK_GITHUB)
        }
        chipXda.setOnClickListener {
            it.context.openLink(Links.LINK_XDA)
        }
    }

    private fun setupSettingSwitch(binding: ItemSettingSwitchBinding, item: SettingsItem.SwitchSetting) = with(binding) {
        val isEnabled = isAdapterEnabled && item.enabled()
        root.alpha = if(isEnabled) 1f else 0.5f
        itemSettingSwitchTitle.text = item.title
        itemSettingSwitchSwitch.applyMonet(monet)
        if(item.content.isNullOrEmpty()){
            itemSettingSwitchContent.isVisible = false
        }else{
            itemSettingSwitchContent.isVisible = true
            itemSettingSwitchContent.text = item.content
        }
        if(item.icon != 0) {
            itemSettingSwitchIcon.setImageResource(item.icon)
        }else{
            itemSettingSwitchIcon.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        itemSettingSwitchSwitch.setOnCheckedChangeListener(null)
        itemSettingSwitchSwitch.isChecked = item.setting.get()
        if(isEnabled) {
            itemSettingSwitchSwitch.setOnCheckedChangeListener { button, isChecked ->
                if (item.tapAction != null) {
                    if (item.tapAction.invoke(isChecked)) {
                        item.setting.set(isChecked)
                    } else {
                        button.isChecked = !isChecked
                    }
                } else {
                    item.setting.set(isChecked)
                }
            }
            root.setOnClickListener {
                itemSettingSwitchSwitch.toggle()
            }
        }else{
            root.setOnClickListener(null)
            itemSettingSwitchSwitch.isEnabled = false
        }
        root.isEnabled = isEnabled
        if(!item.centerIconVertically){
            binding.root.gravity = Gravity.NO_GRAVITY
        }
    }

    sealed class ViewHolder(open val binding: ViewBinding): RecyclerView.ViewHolder(binding.root) {
        data class Header(override val binding: ItemHeaderBinding): ViewHolder(binding)
        data class SettingsSetting(override val binding: ItemSettingBinding): ViewHolder(binding)
        data class SettingsWarning(override val binding: ItemSettingWarningBinding): ViewHolder(binding)
        data class SettingsAboutSetting(override val binding: ItemSettingAboutBinding): ViewHolder(binding)
        data class SettingsSwitchSetting(override val binding: ItemSettingSwitchBinding): ViewHolder(binding)
    }

}