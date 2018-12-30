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
import timber.log.Timber

class HelpFragment : BaseFragment() {

    private lateinit var viewModel: HelpViewModel
    private lateinit var binding: FragmentHelpBinding
    private lateinit var textView: TextView

    companion object {
        fun newInstance(): HelpFragment {
            return HelpFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_help, container, false)
        return binding.root
    }

    fun getHelpText(): String {
        val file = "help.md"
        return resources.assets.open(file).bufferedReader().use {
            it.readText()
        }
    }

    fun getAboutText(): String {
        Timber.d("Getting about text")
        val file = "about.md"
        return resources.assets.open(file).bufferedReader().use {
            it.readText()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HelpViewModel::class.java)
        viewModel.onAboutClickedListener = {
            Timber.d("Invoked")
            setContentText(getAboutText()) }
        textView = binding.root.findViewById(R.id.tv_content)
        setContentText(getHelpText())
        binding.model = viewModel

    }

    override fun getTitle(): String {
        return getString(R.string.appbar_title_help)
    }

    private fun setContentText(textContent: String) {
        Markwon.setMarkdown(textView, textContent)
    }
}