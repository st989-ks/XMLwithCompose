package ru.ekr.xml_with_compose.screen_xml_swiper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.ekr.xml_with_compose.util.DataCard
import ru.ekr.xml_with_compose.databinding.ElementRecyclerCommentsBinding
import ru.ekr.xml_with_compose.databinding.ElementRecyclerSwipeBinding

class AdapterRecyclerXMLSwiper : RecyclerView.Adapter<AdapterRecyclerXMLSwiper.HolderForeXML>() {

    private var clickInfoCurrent: (position: Int) -> Unit = {}
    private var clickDeleteCurrent: (position: Int) -> Unit = {}
    private var clickItemCurrent: (position: Int) -> Unit = {}

    fun onClickInfo(listener: (position: Int) -> Unit) {
        clickInfoCurrent = listener
    }

    fun onClickDelete(listener: (position: Int) -> Unit) {
        clickDeleteCurrent = listener
    }

    fun onClickItem(listener: (position: Int) -> Unit) {
        clickItemCurrent = listener
    }

    object DiffUtils : DiffUtil.ItemCallback<DataCard>() {
        override fun areItemsTheSame(
            oldItem: DataCard,
            newItem: DataCard
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: DataCard,
            newItem: DataCard
        ): Boolean = oldItem == newItem
    }

    private var dataDiff: AsyncListDiffer<DataCard> = AsyncListDiffer(this, DiffUtils)
    override fun onBindViewHolder(holder: HolderForeXML, position: Int) =
        dataDiff.currentList[position]?.let { holder.bind(it, position) } ?: Unit

    fun submitList(products: List<DataCard>) = dataDiff.submitList(products)
    override fun getItemCount() = dataDiff.currentList.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderForeXML {
        val binding = ElementRecyclerSwipeBinding.inflate(
            /* inflater = */ LayoutInflater.from(parent.context),
            /* parent = */ parent,
            /* attachToParent = */ false
        )
        return HolderForeXML(binding = binding)
    }

    override fun findRelativeAdapterPositionIn(
        adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
        viewHolder: RecyclerView.ViewHolder,
        localPosition: Int
    ): Int {
        return if (adapter === this) {
            localPosition
        } else RecyclerView.NO_POSITION
    }

    inner class HolderForeXML(
        private val binding: ElementRecyclerSwipeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun getBinding() =binding
        private val context = binding.root.context
        private var id = 0
        fun getId() = id
        fun bind(item: DataCard, position: Int) {
            this.id = item.id
            binding.textTop.text = item.title
            binding.textBottom.text = item.body
            binding.menuViewLeft.setOnClickListener {
                clickInfoCurrent.invoke(item.id)
                binding.root.smoothCloseMenu()
            }
            binding.menuViewRight.setOnClickListener {
                clickDeleteCurrent.invoke(item.id)
                binding.root.smoothCloseMenu()
            }
            binding.contentView.setOnClickListener {
                clickItemCurrent.invoke(item.id)
                binding.root.smoothCloseMenu()
            }
        }
    }
}