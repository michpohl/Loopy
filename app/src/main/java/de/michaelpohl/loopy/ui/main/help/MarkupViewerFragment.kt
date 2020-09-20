package de.michaelpohl.loopy.ui.main.help

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.franmontiel.attributionpresenter.AttributionPresenter.Builder
import com.franmontiel.attributionpresenter.entities.Attribution
import com.franmontiel.attributionpresenter.entities.License
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.Library
import de.michaelpohl.loopy.databinding.FragmentMarkupViewerBinding
import de.michaelpohl.loopy.ui.main.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_markup_viewer.*
import org.koin.android.viewmodel.ext.android.getViewModel
import ru.noties.markwon.Markwon

class MarkupViewerFragment : BaseFragment() {

    private var showButtons = false
    private lateinit var viewModel: MarkupViewerViewModel
    private lateinit var binding: FragmentMarkupViewerBinding
    private lateinit var markupString: String
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val markupFileName = arguments!!.getString("string")
            showButtons = markupFileName.contains("about")
            markupString = getMarkup(markupFileName)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = getViewModel()
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

        btn_show_app_license.setOnClickListener{onShowAppInfoClicked()}
        btn_show_licenses.setOnClickListener{onShowDependencyLicensesClicked()}

        } else {
            btn_show_app_license.visibility = View.GONE
            btn_show_licenses.visibility = View.GONE
        }

    }

    override fun getTitle(): String {
        return getString(R.string.appbar_title_player)
    }

    private fun setContentText(textContent: String) {
        Markwon.setMarkdown(textView, textContent)
    }

    fun onShowAppInfoClicked(){
        val attributionPresenter = Builder(context)

            .addAttributions(
                Attribution.Builder("Loopy Audio Looper")
                    .addCopyrightNotice("Copyright 2019 Michael Pohl")
                    .addLicense(License.APACHE)
                    .setWebsite("https://github.com/michpohl/loopy")
                    .build()
            )
            .build()
        attributionPresenter.showDialog(getString(R.string.dialog_loopy_license_title))
    }

     fun onShowDependencyLicensesClicked(){
        val attributionPresenter = Builder(context)
            .addAttributions(
                Library.AUDIOGRAM.attribution,
                Library.GSON.attribution,
                Library.MARKWON.attribution
            )
            .addAttributions(
                Attribution.Builder("AttributionPresenter")
                    .addCopyrightNotice("Copyright 2017 Francisco José Montiel Navarro")
                    .addLicense(License.APACHE)
                    .setWebsite("https://github.com/franmontiel/AttributionPresenter")
                    .build()
            )
            .build()
            attributionPresenter.showDialog(getString(R.string.dialog_licenses_title))
    }
}
