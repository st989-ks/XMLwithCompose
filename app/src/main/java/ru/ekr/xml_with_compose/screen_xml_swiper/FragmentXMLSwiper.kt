package ru.ekr.xml_with_compose.screen_xml_swiper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.ekr.xml_with_compose.util.DataCard
import ru.ekr.xml_with_compose.util.SwipeToCallback
import ru.ekr.xml_with_compose.databinding.FragmentXmlBinding
import ru.ekr.xml_with_compose.databinding.FragmentXmlSwiperBinding
import ru.ekr.xml_with_compose.util.generatedDataCard


class FragmentXMLSwiper : Fragment() {

    private var adapterRecycler: AdapterRecyclerXMLSwiper? = null
    private var list: List<DataCard> = generatedDataCard(30000).toList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentXmlSwiperBinding.inflate(inflater, container, false).apply {
        initBinding()

        setActions()
    }.root

    private fun FragmentXmlSwiperBinding.initBinding() {
        adapterRecycler = AdapterRecyclerXMLSwiper()
        recyclerComments.adapter = adapterRecycler
        adapterRecycler?.submitList(list)

    }

    private fun FragmentXmlSwiperBinding.setActions(){
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