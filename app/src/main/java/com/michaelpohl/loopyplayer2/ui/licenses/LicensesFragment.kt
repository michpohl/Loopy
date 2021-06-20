package com.michaelpohl.loopyplayer2.ui.licenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelpohl.delegationadapter.adapter
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.find
import com.michaelpohl.loopyplayer2.ui.base.BaseFragment
import org.koin.android.ext.android.inject

class LicensesFragment : BaseFragment() {

    private val licensesAdapter = adapter<Libraries.Library> {
        delegates = listOf(
            LicenseItemDelegate()
        )
    }

    override val titleResource = R.string.title_licenses

    override val viewModel: LicensesViewModel by inject()

    lateinit var recycler: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_licenses, container, false)
        recycler = view.find(R.id.rv_licenses)
        recycler.adapter = licensesAdapter
        viewModel.licenses?.let {
            licensesAdapter.update(it.libraries)
        }
        return view
    }
}
