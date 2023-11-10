package ru.ekr.xml_with_compose.screen_compose

import android.view.ViewGroup
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.ekr.xml_with_compose.util.DataCard
import ru.ekr.xml_with_compose.R
import kotlin.math.roundToInt

class AdapterRecyclerCompose : RecyclerView.Adapter<AdapterRecyclerCompose.HolderForeCompose>() {

    private var clickInfoCurrent: (position: Int) -> Unit = {}
    private var cLickDeleteCurrent: (position: Int) -> Unit = {}
    private var clickItemCurrent: (position: Int) -> Unit = {}

    fun onClickInfo(listener: (position: Int) -> Unit) {
        clickInfoCurrent = listener
    }

    fun onClickDelete(listener: (position: Int) -> Unit) {
        cLickDeleteCurrent = listener
    }

    fun onClickItem(listener: (position: Int) -> Unit) {
        clickItemCurrent = listener
    }

    object DiffUtils : DiffUtil.ItemCallback<DataCard>() {
        override fun areItemsTheSame(oldItem: DataCard, newItem: DataCard)
                : Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DataCard, newItem: DataCard)
                : Boolean = oldItem.hashCode() == newItem.hashCode()
    }

    private var dataDiff: AsyncListDiffer<DataCard> = AsyncListDiffer(this, DiffUtils)

    override fun onBindViewHolder(holder: HolderForeCompose, position: Int) {
        dataDiff.currentList[position]?.let { holder.bind(it, position) }
    }

    fun submitList(products: List<DataCard>) {
        dataDiff.submitList(products)
    }

    override fun getItemCount() = dataDiff.currentList.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderForeCompose {
        return HolderForeCompose(ComposeView(parent.context))
    }

    inner class HolderForeCompose(
        private val compose: ComposeView
    ) : RecyclerView.ViewHolder(compose) {

        fun bind(item: DataCard, position: Int) {
            compose.setContent {
                val clickInfo = remember(key1 = item) { { clickInfoCurrent.invoke(item.id) } }
                val clickDelete = remember(key1 = item) { { cLickDeleteCurrent.invoke(item.id) } }
                val clickItem = remember(key1 = item) { { clickItemCurrent.invoke(item.id) } }
                OrderCardWithSwipe(
                    onClickInfo = clickInfo,
                    onCLickDelete = clickDelete,
                    onClickItem = clickItem,
                    textTitle = item.title,
                    textBody = item.body
                )
            }
        }
    }


    @Composable
    fun OrderCardWithSwipe(
        textTitle: String,
        textBody: String,
        onClickInfo: () -> Unit,
        onClickItem: () -> Unit,
        onCLickDelete: () -> Unit
    ) {

        val density = LocalDensity.current
        val scope = rememberCoroutineScope()
        var heightContentDp by remember { mutableStateOf(0.dp) }
        var widthDeletePx by remember { mutableIntStateOf(0) }
        var offsetXDynamics by remember { mutableFloatStateOf(0f) }
        val offsetXLimit by remember(widthDeletePx) { mutableFloatStateOf(-widthDeletePx.toFloat()) }
        val halfOffsetXLimit = offsetXLimit * 0.7

        Box(
            modifier = Modifier
                .onGloballyPositioned {
                    heightContentDp = with(density) { it.size.height.toDp() }
                }
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable {
                        onClickInfo.invoke()
                        scope.launch {
                            animate(offsetXDynamics, 0f) { value, velocity ->
                                offsetXDynamics = value
                            }
                        }
                    }
                    .wrapContentWidth()
                    .size(heightContentDp)
                    .animateContentSize()
                    .align(Alignment.CenterEnd)
                    .background(Color.Green.copy(alpha = 0.8f))
                    .onGloballyPositioned {
                        widthDeletePx = it.size.width
                    }
            ) {
                Icon(
                    modifier = Modifier
                        .size(heightContentDp * 0.5f)
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = ""
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable {
                        onCLickDelete.invoke()
                        scope.launch {
                            animate(offsetXDynamics, 0f) { value, velocity ->
                                offsetXDynamics = value
                            }
                        }
                    }
                    .wrapContentWidth()
                    .size(heightContentDp)
                    .animateContentSize()
                    .align(Alignment.CenterEnd)
                    .background(Color.Red.copy(alpha = 0.8f))
                    .onGloballyPositioned {
                        widthDeletePx = it.size.width
                    }
            ) {
                Icon(
                    modifier = Modifier
                        .size(heightContentDp * 0.5f)
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = ""
                )
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(offsetXDynamics.roundToInt(), 0) }
                    .draggable(
                            orientation = Orientation.Horizontal,
                            onDragStopped = {
                                this.launch {
                                    when {
                                        offsetXDynamics < halfOffsetXLimit -> animate(
                                            offsetXDynamics,
                                            offsetXLimit
                                        ) { value, velocity ->
                                            offsetXDynamics = value
                                        }

                                        offsetXDynamics > -halfOffsetXLimit -> animate(
                                            offsetXDynamics,
                                            -offsetXLimit
                                        ) { value, velocity ->
                                            offsetXDynamics = value
                                        }

                                        else -> animate(offsetXDynamics, 0f) { value, velocity ->
                                            offsetXDynamics = value
                                        }
                                    }
                                }
                            },
                            state = rememberDraggableState { delta ->
                                val newOffset = offsetXDynamics + delta
                                offsetXDynamics = newOffset.coerceIn(offsetXLimit, -offsetXLimit)
                            }
                        )

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = android.R.color.holo_blue_dark))
                        .clickable(onClick = onClickItem)
                        .padding(16.dp)
                ) {

                    Text(text = textTitle)
                    Box(modifier = Modifier.size(16.dp))
                    Text(text = textBody)
                }
            }

            Box(modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Black))
        }
    }

}