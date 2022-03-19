package com.kieronquinn.app.classicpowermenu.ui.screens.settings.faq

import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSettingsFaqBinding
import com.kieronquinn.app.classicpowermenu.ui.base.AutoExpandOnRotate
import com.kieronquinn.app.classicpowermenu.ui.base.BackAvailable
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.utils.extensions.getColorResCompat
import com.kieronquinn.monetcompat.extensions.views.enableStretchOverscroll
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.core.MarkwonTheme
import org.commonmark.node.Heading

class SettingsFaqFragment: BoundFragment<FragmentSettingsFaqBinding>(FragmentSettingsFaqBinding::inflate), AutoExpandOnRotate, BackAvailable {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.isNestedScrollingEnabled = false
        val typeface = ResourcesCompat.getFont(requireContext(), R.font.google_sans_text_medium)
        val markwon = Markwon.builder(requireContext()).usePlugin(object: AbstractMarkwonPlugin() {
            override fun configureTheme(builder: MarkwonTheme.Builder) {
                typeface?.let {
                    builder.headingTypeface(it)
                    builder.headingBreakHeight(0)
                }
            }

            override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
                val origin = builder.requireFactory(Heading::class.java)
                builder.setFactory(Heading::class.java) { configuration, props ->
                    arrayOf(origin.getSpans(configuration, props), ForegroundColorSpan(requireContext().getColorResCompat(android.R.attr.textColorPrimary)))
                }
            }
        }).build()
        val markdown = requireContext().assets.open(getString(R.string.settings_about_faq_file)).bufferedReader().use { it.readText() }
        binding.markdown.text = markwon.toMarkdown(markdown)
        ViewCompat.setOnApplyWindowInsetsListener(binding.markdown){ view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.ime()).bottom
            view.updatePadding(bottom = bottomInset)
            insets
        }
        binding.root.enableStretchOverscroll()
    }

}