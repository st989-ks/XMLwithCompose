package ru.ekr.xml_with_compose.screen_xml_swiper_exp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ekr.xml_with_compose.databinding.FragmentXmlSwiperExpBinding
import ru.ekr.xml_with_compose.util.DataCard
import ru.ekr.xml_with_compose.util.GENERATED_COUNT
import ru.ekr.xml_with_compose.util.generatedDataCard


class FragmentXMLSwiperExp : Fragment() {

    private var adapterRecycler: AdapterRecyclerXMLSwiperExp? = null
    private val list = MutableStateFlow<List<DataCard>>(generatedDataCard(1))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentXmlSwiperExpBinding.inflate(inflater, container, false).apply {
        initBinding()
        setActions()
    }.root

    private fun FragmentXmlSwiperExpBinding.initBinding() {
        adapterRecycler = AdapterRecyclerXMLSwiperExp()
        recyclerCommentsExp.adapter = adapterRecycler
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                list.collectLatest { data ->
                    adapterRecycler?.submitList(data)
                }
            }
        }
    }

    private fun FragmentXmlSwiperExpBinding.setActions() {
        adapterRecycler?.onClickDelete { position ->
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                if (list.value.size > 2) list.update {
                    it.filter { itFilter -> itFilter.id != position }
                }
            }
        }
        adapterRecycler?.onClickInfo {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                list.update {
                    it.plus(generatedDataCard(
                        1,
                        it.maxByOrNull { maxIt -> maxIt.id }?.id))
                }
            }
        }
        adapterRecycler?.onClickItem {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                list.update { generatedDataCard(5) }
            }
        }
    }
}