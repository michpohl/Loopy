package de.michaelpohl.loopy.ui.main.help

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.databinding.FragmentHelpBinding
import de.michaelpohl.loopy.ui.main.BaseFragment
import ru.noties.markwon.Markwon

class MarkupViewerFragment : BaseFragment() {

    private lateinit var viewModel: MarkupViewerViewModel
    private lateinit var binding: FragmentHelpBinding
    private lateinit var markupString: String
    private lateinit var textView: TextView

    companion object {
        fun newInstance(markupFileName: String): MarkupViewerFragment {
            val fragment = MarkupViewerFragment()
            val args = Bundle()
            args.putString("fileName", markupFileName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val markupFileName = arguments!!.getString("fileName")
            markupString = getMarkup(markupFileName)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_help, container, false)
        return binding.root
    }

    fun getMarkup(sourceFileName: String): String {
        return resources.assets.open(sourceFileName).bufferedReader().use {
            it.readText()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MarkupViewerViewModel::class.java)
        textView = binding.root.findViewById(R.id.tv_content)
        setContentText(markupString)
        binding.model = viewModel
    }

    override fun getTitle(): String {
        return getString(R.string.appbar_title_player)
    }

    private fun setContentText(textContent: String) {
        Markwon.setMarkdown(textView, textContent)
    }
}