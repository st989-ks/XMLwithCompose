package ru.ekr.xml_with_compose.screen_compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ekr.xml_with_compose.util.DataCard
import ru.ekr.xml_with_compose.databinding.FragmentComposeBinding
import ru.ekr.xml_with_compose.util.GENERATED_COUNT
import ru.ekr.xml_with_compose.util.generatedDataCard


class FragmentCompose : Fragment() {


    private var adapterRecycler: AdapterRecyclerCompose? = null
    private val list = MutableStateFlow<List<DataCard>>(generatedDataCard(1))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentComposeBinding.inflate(inflater, container, false).apply {
        initBinding()
        setActions()
    }.root

    private fun FragmentComposeBinding.initBinding() {
        adapterRecycler = AdapterRecyclerCompose()
        recyclerComments.adapter = adapterRecycler
        viewLifecycleOwner.lifecycleScope.launch  {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                list.collectLatest{data ->
                    adapterRecycler?.submitList(data)
                }
            }
        }
    }


    private fun FragmentComposeBinding.setActions(){
        adapterRecycler?.onClickDelete {position ->
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                list.update { it.filter { itFilter -> itFilter.id != position } }
            }
        }
        adapterRecycler?.onClickInfo {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                list.update { generatedDataCard(1).plus(it) }
            }
        }
        adapterRecycler?.onClickItem {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                list.update { generatedDataCard(GENERATED_COUNT) }
            }
        }
    }
}