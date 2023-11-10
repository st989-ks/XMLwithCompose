package ru.ekr.xml_with_compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.ekr.xml_with_compose.databinding.FragmentXmlBinding


class FragmentXML : Fragment() {

    private var adapterRecycler: AdapterRecyclerXML? = null
    private var list: List<DataCard> = generatedDataCard(30)

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
        adapterRecycler?.submitList(list)

    }

    private fun FragmentXmlBinding.setDraggable(){
        ItemTouchHelper(object :SwipeToCallback(){

        }).attachToRecyclerView(recyclerComments)
    }

    private fun FragmentXmlBinding.setActions(){
        adapterRecycler?.onClickDelete {position ->
            list = list.filter { it.id != position }
            adapterRecycler?.submitList(list)
            Toast.makeText(activity, "onClickDelete", Toast.LENGTH_SHORT).show()
        }
        adapterRecycler?.onClickInfo {
            Toast.makeText(activity, "onClickInfo", Toast.LENGTH_SHORT).show()
        }
        adapterRecycler?.onClickItem {
            Toast.makeText(activity, "onClickItem", Toast.LENGTH_SHORT).show()
        }
    }


}