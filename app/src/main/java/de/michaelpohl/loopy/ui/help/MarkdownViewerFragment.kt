package de.michaelpohl.loopy.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.franmontiel.attributionpresenter.AttributionPresenter
import com.franmontiel.attributionpresenter.entities.Attribution
import com.franmontiel.attributionpresenter.entities.License
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.ui.base.BaseFragment
import de.michaelpohl.loopy.ui.util.MarkDownTextView
import org.koin.android.ext.android.inject

class MarkdownViewerFragment : BaseFragment() {

    override val viewModel: MarkdownViewerViewModel by inject()

    private var markupString: String? = null
    override var titleResource: Int? = null

    private lateinit var binding: de.michaelpohl.loopy.databinding.FragmentMarkupViewerBinding
    private lateinit var textView: MarkDownTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleArguments()
    }

    private fun handleArguments() {
        with(requireArguments()) {
            val markupFileName = this.getString(MARKDOWN_FILENAME_KEY)!!
            titleResource = this.getInt(SCREN_TITILE_KEY, R.string.appbar_title_player)
            viewModel.docType.value = markupFileName.toDocumentType()
            markupString = viewModel.getAssetString(markupFileName)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_markup_viewer, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        textView = binding.root.findViewById(R.id.tv_content)
        setContentText(markupString)
        binding.model = viewModel
    }

    private fun setContentText(textContent: String?) {
        textView.setMarkdownText(textContent)
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

    fun String.toDocumentType(): DocumentType {
        return when {
            this.contains("about") -> DocumentType.ABOUT
            this.contains("whatsnew") -> DocumentType.WHATSNEW
            else -> DocumentType.HELP
        }
    }

    enum class DocumentType {
        ABOUT, HELP, WHATSNEW
    }

    companion object {

        const val MARKDOWN_FILENAME_KEY = "string"
        const val SCREN_TITILE_KEY = "title"
    }
}
