package com.blackbyte.skucise.components

import kotlinx.coroutines.CoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.IntSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.sign

@Composable
fun <T : Any> Pager(
    items: List<T>,
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.Horizontal,
    initialIndex: Int = 0,
    /*@FloatRange(from = 0.0, to = 1.0)*/
    itemFraction: Float = 1f,
    itemSpacing: Dp = 0.dp,
    /*@FloatRange(from = 0.0, to = 1.0)*/
    overshootFraction: Float = .5f,
    onItemSelect: (T) -> Unit = {},
    contentFactory: @Composable (T) -> Unit,
    regulateDrawer: (Boolean) -> Unit = {}
) {
    require(initialIndex in 0..items.lastIndex) { "Initial index out of bounds" }
    require(itemFraction > 0f && itemFraction <= 1f) { "Item fraction must be in the (0f, 1f] range" }
    require(overshootFraction > 0f && itemFraction <= 1f) { "Overshoot fraction must be in the (0f, 1f] range" }
    val scope = rememberCoroutineScope()
    val state = rememberPagerState(regulateDrawer)

    var indicatorIndex by remember {
        mutableStateOf(initialIndex)
    }
    val setIndicatorIndex = fun(value: Int) {
        indicatorIndex = value
    }

    state.currentIndex = initialIndex
    state.numberOfItems = items.size
    state.itemFraction = itemFraction
    state.overshootFraction = overshootFraction
    state.itemSpacing = with(LocalDensity.current) { itemSpacing.toPx() }
    state.orientation = orientation
    state.scope = scope
    state.listener = fun(index: Int) {
        setIndicatorIndex(index)
        onItemSelect(items[index])
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.wrapContentSize()
    ) {
        Layout(
            content = {
                items.map { item ->
                    Box(
                        modifier = when (orientation) {
                            Orientation.Horizontal -> Modifier.fillMaxWidth()
                            Orientation.Vertical -> Modifier.fillMaxHeight()
                        },
                        contentAlignment = Alignment.Center,
                    ) {
                        contentFactory(item)
                    }
                }
            },
            modifier = modifier
                .clipToBounds()
                .then(state.inputModifier),
        ) { measurables, constraints ->
            val dimension = constraints.dimension(orientation)
            val looseConstraints = constraints.toLooseConstraints(orientation, state.itemFraction)
            val placeables = measurables.map { measurable -> measurable.measure(looseConstraints) }
            val size = placeables.getSize(orientation, dimension)
            val itemDimension = (dimension * state.itemFraction).roundToInt()
            state.itemDimension = itemDimension
            val halfItemDimension = itemDimension / 2
            layout(size.width, size.height) {
                val centerOffset = dimension / 2 - halfItemDimension
                val dragOffset = state.dragOffset.value
                val roundedDragOffset = dragOffset.roundToInt()
                val spacing = state.itemSpacing.roundToInt()
                val itemDimensionWithSpace = itemDimension + state.itemSpacing
                val first = ceil(
                    (dragOffset - itemDimension - centerOffset) / itemDimensionWithSpace
                ).toInt().coerceAtLeast(0)
                val last =
                    ((dimension + dragOffset - centerOffset) / itemDimensionWithSpace).toInt()
                        .coerceAtMost(items.lastIndex)
                for (i in first..last) {
                    val offset = i * (itemDimension + spacing) - roundedDragOffset + centerOffset
                    placeables[i].place(
                        x = when (orientation) {
                            Orientation.Horizontal -> offset
                            Orientation.Vertical -> 0
                        },
                        y = when (orientation) {
                            Orientation.Horizontal -> 0
                            Orientation.Vertical -> offset
                        }
                    )
                }
            }
        }
        Column {
            Row(
                modifier = Modifier
                    .background(
                        color = Color(0x80000000),
                        shape = RoundedCornerShape(corner = CornerSize(size = 15.dp))
                    )
                    .padding(all = 8.dp)
            ) {
                for (i in 0..items.lastIndex) {
                    Spacer(modifier = Modifier.size(4.dp))
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(color = if (i == indicatorIndex) Color.White else Color.Transparent)
                            .border(width = 1.dp, color = Color.White, CircleShape)
                    ) {}
                    Spacer(modifier = Modifier.size(4.dp))
                }
            }
            Spacer(modifier = Modifier.size(2.dp))
        }
    }
    LaunchedEffect(key1 = items, key2 = initialIndex) {
        state.snapTo(initialIndex)
    }
}

@Composable
private fun rememberPagerState(regulateDrawer: (Boolean) -> Unit): PagerState =
    remember { PagerState(regulateDrawer = regulateDrawer) }

private fun Constraints.dimension(orientation: Orientation) = when (orientation) {
    Orientation.Horizontal -> maxWidth
    Orientation.Vertical -> maxHeight
}

private fun Constraints.toLooseConstraints(
    orientation: Orientation,
    itemFraction: Float,
): Constraints {
    val dimension = dimension(orientation)
    return when (orientation) {
        Orientation.Horizontal -> copy(
            minWidth = (dimension * itemFraction).roundToInt(),
            maxWidth = (dimension * itemFraction).roundToInt(),
            minHeight = 0,
        )
        Orientation.Vertical -> copy(
            minWidth = 0,
            minHeight = (dimension * itemFraction).roundToInt(),
            maxHeight = (dimension * itemFraction).roundToInt(),
        )
    }
}

private fun List<Placeable>.getSize(
    orientation: Orientation,
    dimension: Int,
): IntSize {
    return when (orientation) {
        Orientation.Horizontal -> IntSize(
            dimension,
            maxByOrNull { it.height }?.height ?: 0
        )
        Orientation.Vertical -> IntSize(
            maxByOrNull { it.width }?.width ?: 0,
            dimension
        )
    }
}

private class PagerState(
    val regulateDrawer: (Boolean) -> Unit
) {
    var currentIndex by mutableStateOf(0)
    var numberOfItems by mutableStateOf(0)
    var itemFraction by mutableStateOf(0f)
    var overshootFraction by mutableStateOf(0f)
    var itemSpacing by mutableStateOf(0f)
    var itemDimension by mutableStateOf(0)
    var orientation by mutableStateOf(Orientation.Horizontal)
    var scope: CoroutineScope? by mutableStateOf(null)
    var listener: (Int) -> Unit by mutableStateOf({})
    val dragOffset = androidx.compose.animation.core.Animatable(0f)

    private val animationSpec = SpringSpec<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow,
    )

    suspend fun snapTo(index: Int) {
        dragOffset.snapTo(index.toFloat() * (itemDimension + itemSpacing))
    }

    val inputModifier = Modifier.pointerInput(numberOfItems) {
        fun itemIndex(offset: Int): Int = (offset / (itemDimension + itemSpacing)).roundToInt()
            .coerceIn(0, numberOfItems - 1)

        fun updateIndex(offset: Float) {
            val index = itemIndex(offset.roundToInt())
            if (index != currentIndex) {
                currentIndex = index
                listener(index)
            }
        }

        fun calculateOffsetLimit(): OffsetLimit {
            val dimension = when (orientation) {
                Orientation.Horizontal -> size.width
                Orientation.Vertical -> size.height
            }
            val itemSideMargin = (dimension - itemDimension) / 2f
            return OffsetLimit(
                min = -dimension * overshootFraction + itemSideMargin,
                max = numberOfItems * (itemDimension + itemSpacing) - (1f - overshootFraction) * dimension + itemSideMargin,
            )
        }

        forEachGesture {
            this.awaitPointerEventScope {
                val tracker = VelocityTracker()
                val decay = splineBasedDecay<Float>(this)
                val down = awaitFirstDown()
                val offsetLimit = calculateOffsetLimit()
                val dragHandler = { change: PointerInputChange ->
                    scope?.launch {
                        regulateDrawer(false)
                        val dragChange = change.calculateDragChange(orientation)
                        dragOffset.snapTo(
                            (dragOffset.value - dragChange).coerceIn(
                                offsetLimit.min,
                                offsetLimit.max
                            )
                        )
                        updateIndex(dragOffset.value)
                    }
                    tracker.addPosition(change.uptimeMillis, change.position)
                }
                when (orientation) {
                    Orientation.Horizontal -> horizontalDrag(down.id, dragHandler)
                    Orientation.Vertical -> verticalDrag(down.id, dragHandler)
                }
                val velocity = tracker.calculateVelocity(orientation)
                scope?.launch {
                    regulateDrawer(false)
                    var targetOffset = decay.calculateTargetValue(dragOffset.value, -velocity)
                    val remainder = targetOffset.toInt().absoluteValue % itemDimension
                    val extra = if (remainder > itemDimension / 2f) 1 else 0
                    val lastVisibleIndex =
                        (targetOffset.absoluteValue / itemDimension.toFloat()).toInt() + extra
                    targetOffset =
                        (lastVisibleIndex * (itemDimension + itemSpacing) * targetOffset.sign)
                            .coerceIn(
                                0f,
                                (numberOfItems - 1).toFloat() * (itemDimension + itemSpacing)
                            )
                    dragOffset.animateTo(
                        animationSpec = animationSpec,
                        targetValue = targetOffset,
                        initialVelocity = -velocity
                    ) {
                        updateIndex(value)
                    }
                    regulateDrawer(true)
                }
            }
        }
    }

    data class OffsetLimit(
        val min: Float,
        val max: Float,
    )
}

private fun PointerInputChange.calculateDragChange(orientation: Orientation) =
    when (orientation) {
        Orientation.Horizontal -> positionChange().x
        Orientation.Vertical -> positionChange().y
    }