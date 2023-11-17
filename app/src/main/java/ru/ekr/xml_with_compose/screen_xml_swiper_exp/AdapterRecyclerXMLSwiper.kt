package ru.ekr.xml_with_compose.screen_xml_swiper_exp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.ekr.xml_with_compose.util.DataCard
import ru.ekr.xml_with_compose.databinding.ElementRecyclerSwipeExpBinding

class AdapterRecyclerXMLSwiperExp : RecyclerView.Adapter<AdapterRecyclerXMLSwiperExp.HolderForeXMLExp>() {

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
    override fun onBindViewHolder(holder: HolderForeXMLExp, position: Int) =
        dataDiff.currentList[position]?.let { holder.bind(it, position) } ?: Unit

    fun submitList(products: List<DataCard>) = dataDiff.submitList(products)
    override fun getItemCount() = dataDiff.currentList.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderForeXMLExp {
        val binding = ElementRecyclerSwipeExpBinding.inflate(
            /* inflater = */ LayoutInflater.from(parent.context),
            /* parent = */ parent,
            /* attachToParent = */ false
        )
        return HolderForeXMLExp(binding = binding)
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

    inner class HolderForeXMLExp(
        private val binding: ElementRecyclerSwipeExpBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun getBinding() =binding
        private val context = binding.root.context
        private var id = 0
        fun getId() = id
        fun bind(item: DataCard, position: Int) {
            this.id = item.id
            binding.menuLeft.setOnClickListener {
                clickInfoCurrent.invoke(item.id)
            }
            binding.menuRight.setOnClickListener {
                clickDeleteCurrent.invoke(item.id)
            }
            binding.baseContent.root.setOnClickListener {
                clickItemCurrent.invoke(item.id)
            }
        }
    }
}