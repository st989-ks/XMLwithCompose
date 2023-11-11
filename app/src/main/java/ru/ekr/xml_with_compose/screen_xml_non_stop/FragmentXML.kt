package ru.ekr.xml_with_compose.screen_xml_non_stop

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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ekr.xml_with_compose.util.DataCard
import ru.ekr.xml_with_compose.util.SwipeToCallback
import ru.ekr.xml_with_compose.databinding.FragmentXmlBinding
import ru.ekr.xml_with_compose.util.GENERATED_COUNT
import ru.ekr.xml_with_compose.util.generatedDataCard


class FragmentXML : Fragment() {

    private var adapterRecycler: AdapterRecyclerXML? = null
    private val list = MutableStateFlow<List<DataCard>>(generatedDataCard(1))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentXmlBinding.inflate(inflater, container, false).apply {
        initBinding()
        setDraggable()
        setActions()
    }.root

    private fun FragmentXmlBinding.initBinding() {
        adapterRecycler = AdapterRecyclerXML()
        recyclerComments.adapter = adapterRecycler
        viewLifecycleOwner.lifecycleScope.launch  {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                list.collectLatest{data ->
                    adapterRecycler?.submitList(data)
                }
            }
        }
    }

    private fun FragmentXmlBinding.setDraggable(){
        ItemTouchHelper(object : SwipeToCallback(){
            override fun canMovePosition(item: RecyclerView.ViewHolder) = true

            override fun onSwipeEndToStart(
                viewHolder: AdapterRecyclerXML.HolderForeXML,
            ) {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    list.update { it.filter { itFilter -> itFilter.id != viewHolder.getId() } }
                }
            }

            override fun onSwipeStartToEnd(
                viewHolder: AdapterRecyclerXML.HolderForeXML
            ) {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    list.update { generatedDataCard(1,it.last().id).plus(it) }
                }
                this.onClearView(viewHolder)
            }

        }).attachToRecyclerView(recyclerComments)
    }

    private fun FragmentXmlBinding.setActions(){
        adapterRecycler?.onClickDelete {position ->
            Toast.makeText(activity, "onClickDelete", Toast.LENGTH_SHORT).show()
        }
        adapterRecycler?.onClickInfo {
            Toast.makeText(activity, "onClickInfo", Toast.LENGTH_SHORT).show()
        }
        adapterRecycler?.onClickItem {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                list.update { generatedDataCard(GENERATED_COUNT) }
            }
        }
    }
}