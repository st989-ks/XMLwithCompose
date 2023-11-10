package ru.ekr.xml_with_compose.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import ru.ekr.xml_with_compose.screen_compose.FragmentCompose
import ru.ekr.xml_with_compose.screen_xml_non_stop.FragmentXML
import ru.ekr.xml_with_compose.R
import ru.ekr.xml_with_compose.databinding.FragmentMainBinding
import ru.ekr.xml_with_compose.screen_xml_swiper.FragmentXMLSwiper


class FragmentMain : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        setButton()
        return binding.root
    }

    private fun setButton() {
        binding.buttonXml.setOnClickListener {
            Toast.makeText(activity, FragmentXML::class.simpleName, Toast.LENGTH_SHORT).show()
            activity?.supportFragmentManager?.commit {
                add(R.id.container_fragment, FragmentXML())
                addToBackStack(FragmentXML::class.simpleName)
            }

        }
        binding.buttonXmlSwiper.setOnClickListener {
            Toast.makeText(activity, FragmentXMLSwiper::class.simpleName, Toast.LENGTH_SHORT).show()
            activity?.supportFragmentManager?.commit {
                add(R.id.container_fragment, FragmentXMLSwiper())
                addToBackStack(FragmentXMLSwiper::class.simpleName)
            }

        }
        binding.buttonCompose.setOnClickListener {
            Toast.makeText(activity, FragmentCompose::class.simpleName, Toast.LENGTH_SHORT).show()
            activity?.supportFragmentManager?.commit {
                add(R.id.container_fragment, FragmentCompose())
                addToBackStack(FragmentCompose::class.simpleName)
            }
        }
    }
}