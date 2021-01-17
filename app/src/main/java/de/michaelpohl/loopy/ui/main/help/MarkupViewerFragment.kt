package de.michaelpohl.loopy.ui.main.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.franmontiel.attributionpresenter.AttributionPresenter
import com.franmontiel.attributionpresenter.entities.Attribution
import com.franmontiel.attributionpresenter.entities.License
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.databinding.FragmentMarkupViewerBinding
import de.michaelpohl.loopy.ui.main.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_markup_viewer.*
import org.koin.android.ext.android.inject
import ru.noties.markwon.Markwon

class MarkupViewerFragment : BaseFragment() {

    override val viewModel: MarkupViewerViewModel by inject()
    private var showButtons = false
    private lateinit var binding: FragmentMarkupViewerBinding
    private lateinit var markupString: String
    private lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val markupFileName = requireArguments().getString("string")!!
            showButtons = markupFileName.contains("about")
            markupString = getMarkup(markupFileName)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_markup_viewer, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    fun getMarkup(sourceFileName: String): String {
        return resources.assets.open(sourceFileName).bufferedReader().use {
            it.readText()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        textView = binding.root.findViewById(R.id.tv_content)
        setContentText(markupString)
        binding.model = viewModel
        if (showButtons) {

            btn_show_app_license.setOnClickListener { onShowAppInfoClicked() }
            btn_show_licenses.setOnClickListener { onShowDependencyLicensesClicked() }
        } else {
            btn_show_app_license.visibility = View.GONE
            btn_show_licenses.visibility = View.GONE
        }
    }

    override fun getTitle(): String {
        return if (showButtons) {
            getString(R.string.title_about)
        } else {
            getString(R.string.title_help)
        }
    }

    private fun setContentText(textContent: String) {
        Markwon.setMarkdown(textView, textContent)
    }

    private fun onShowAppInfoClicked() {
        val attributionPresenter = AttributionPresenter.Builder(context)
            .addAttributions(
                Attribution.Builder("Loopy Audio Looper")
                    .addCopyrightNotice("Copyright 2017 Michael Pohl")
                    .addLicense(License.APACHE)
                    .setWebsite("https://github.com/michpohl/loopy")
                    .build()
            )
            .build()
        attributionPresenter.showDialog(getString(R.string.dialog_loopy_license_title))
    }

    private fun onShowDependencyLicensesClicked() {
        navigateTo(R.id.action_markupViewerFragment_to_licensesFragment)
    }
}
