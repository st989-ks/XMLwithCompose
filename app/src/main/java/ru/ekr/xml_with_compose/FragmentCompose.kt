package ru.ekr.xml_with_compose

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ru.ekr.xml_with_compose.databinding.FragmentComposeBinding
import ru.ekr.xml_with_compose.databinding.FragmentXmlBinding


class FragmentCompose : Fragment() {


    private var adapterRecycler: AdapterRecyclerCompose? = null
    private var list: List<DataCard> = generatedDataCard(3000)

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
        adapterRecycler?.submitList(list)
    }


    private fun FragmentComposeBinding.setActions(){
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