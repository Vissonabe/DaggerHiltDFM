package com.viswa.app.movie

import androidx.compose.animation.DpPropKey
import androidx.compose.animation.animatedFloat
import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.AnimatedFloat
import androidx.compose.animation.core.AnimationClockObservable
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.BaseAnimatedValue
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TargetAnimation
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.transition
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.Text
import androidx.compose.foundation.animation.AndroidFlingDecaySpec
import androidx.compose.foundation.animation.FlingConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableController
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.launchInComposition
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ContentDrawScope
import androidx.compose.ui.DrawLayerModifier
import androidx.compose.ui.DrawModifier
import androidx.compose.ui.LayoutModifier
import androidx.compose.ui.Measurable
import androidx.compose.ui.MeasureScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.drawLayer
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorStop
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.VerticalGradient
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AnimationClockAmbient
import androidx.compose.ui.platform.ConfigurationAmbient
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.platform.InspectableParameter
import androidx.compose.ui.platform.ParameterElement
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.viswa.app.movie.api.AmbientMovieApi
import com.viswa.app.movie.api.CastMember
import com.viswa.app.movie.api.Movie
import com.viswa.app.movie.api.MoviesResponse
import com.viswa.app.movie.api.PartialMovie
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.reflect.KProperty

val posterAspectRatio = .674f

@Composable
fun Screen() {
    val configuration = ConfigurationAmbient.current
    val screenWidth = configuration.screenWidthDp.dp
    val posterWidthDp = screenWidth * 0.6f
    val posterSpacingDp = posterWidthDp + 20.dp
    val carouselState = rememberCarouselState()

    val api = AmbientMovieApi.current
    var movies by remember { mutableStateOf<MoviesResponse?>(null) }
    launchInComposition {
        movies = api.topRated(1)
    }

    Stack {
        Carousel(
            items = movies?.results ?: emptyList<PartialMovie>(),
            state = carouselState,
            spacing = posterSpacingDp,
            getBackgroundImage = {
                it.posterUrl
            },
            getForegroundImage = {
                it.posterUrl
            },
        ) { movie, expanded ->
            MoviePoster(
                movie = movie,
                expanded = expanded,
                expandedWidth = screenWidth,
                normalWidth = posterWidthDp,
            )
        }
        BuyTicketButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(posterWidthDp)
                .padding(20.dp),
            onClick = {
                carouselState.expandSelectedItem()
            }
        )
    }
}

class ValueHolder<T>(var value: T)

operator fun <T> ValueHolder<T>.getValue(thisRef: Any?, property: KProperty<*>) = value
operator fun <T> ValueHolder<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this.value = value
}

private fun indexToOffset(index: Int, spacingPx: Float): Float = -1 * index * spacingPx
private fun offsetToIndex(offset: Float, spacingPx: Float): Int =
    floor(-1 * offset / spacingPx).toInt()

@Composable fun rememberCarouselState(): CarouselState {
    val density = DensityAmbient.current
    val clock = AnimationClockAmbient.current.asDisposableClock()
    val animatedOffset = animatedFloat(0f)
    val expanded = animatedFloat(0f)
    return remember(clock, density) {
        CarouselState(
            density,
            animatedOffset,
            expanded,
            clock,
        )
    }
}

class CarouselState(
    private val density: Density,
    internal val animatedOffset: AnimatedFloat,
    internal val expanded: AnimatedFloat,
    clock: AnimationClockObservable
) {
    private val flingConfig = FlingConfig(AndroidFlingDecaySpec(density)) { adjustTarget(it) }
    internal val scrollableController = ScrollableController({ consumeScrollDelta(it) }, flingConfig, clock)
    var expandedIndex by mutableStateOf<Int?>(null)
        private set
    private var itemCount: Int = 0
    internal var spacingPx: Float = 0f
    internal fun update(count: Int, spacing: Dp) {
        itemCount = count
        spacingPx = with(density) { spacing.toPx() }
    }
    private val upperBound: Float = 0f
    private val lowerBound: Float get() = -1 * (itemCount - 1) * spacingPx

    private fun adjustTarget(target: Float): TargetAnimation? {
        return TargetAnimation((target / spacingPx).roundToInt() * spacingPx)
    }
    private fun consumeScrollDelta(delta: Float): Float {
        if (expandedIndex != null) {
            return 0f
        }
        var target = animatedOffset.value + delta
        var consumed = delta
        when {
            target > upperBound -> {
                consumed = upperBound - animatedOffset.value
                target = upperBound
            }
            target < lowerBound -> {
                consumed = lowerBound - animatedOffset.value
                target = lowerBound
            }
        }
        animatedOffset.snapTo(target)
        return consumed
    }
    fun expandSelectedItem() {
        if (expandedIndex != null) {
            expandedIndex = null
            expanded.animateTo(0f, SpringSpec(stiffness = Spring.StiffnessLow))
        } else {
            expandedIndex = selectedIndex
            expanded.animateTo(
                1f,
                SpringSpec(
                    stiffness = Spring.StiffnessLow,
                    dampingRatio = Spring.DampingRatioLowBouncy
                )
            )
        }
    }
    val selectedIndex: Int get() = offsetToIndex(animatedOffset.value, spacingPx)
}

@Composable fun <T> Carousel(
    items: List<T>,
    spacing: Dp,
    state: CarouselState,
    getBackgroundImage: (T) -> Any,
    getForegroundImage: (T) -> Any,
    foregroundContent: @Composable (item: T, expanded: Boolean) -> Unit
) {
    state.update(items.size, spacing)
    val spacingPx = state.spacingPx
    val animatedOffset = state.animatedOffset
    val expanded = state.expanded
    val expandedIndex = state.expandedIndex
    Stack(
        Modifier
            .background(Color.Black)
            .fillMaxSize()
            .scrollable(
                Orientation.Horizontal,
                state.scrollableController,
            )
    ) {
        items.forEachIndexed { index, item ->
            CoilImage(
                data = getBackgroundImage(item),
                modifier = Modifier
                    .carouselBackground(
                        index = index,
                        getIndexFraction = { -1 * animatedOffset.value / spacingPx },
                        getExpandedFraction = { expanded.value }
                    )
                    .fillMaxWidth()
                    .aspectRatio(posterAspectRatio)
            )
            if (expandedIndex != null && abs(index - expandedIndex) == 1) {
                CoilImage(
                    data = getForegroundImage(item),
                    modifier = Modifier
                        .carouselExpandedBackground(
                            index = index,
                            expandedIndex = expandedIndex,
                            getExpandedFraction = { expanded.value }
                        )
                        .align(Alignment.BottomCenter)
                        .width(spacing)
                        .aspectRatio(posterAspectRatio)
                )
            }
        }
        if (expandedIndex != null) {
            CoilImage(
                data = getForegroundImage(items[expandedIndex]),
                modifier = Modifier
                    .carouselExpandedBackground(
                        index = expandedIndex,
                        expandedIndex = expandedIndex,
                        getExpandedFraction = { expanded.value }
                    )
                    .align(Alignment.BottomCenter)
                    .width(spacing)
                    .aspectRatio(posterAspectRatio)
            )
        }
        Spacer(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .verticalGradient(0f to Color.Transparent, 0.3f to Color.White, 1f to Color.White)
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
        )
        items.forEachIndexed { index, item ->
            val center = spacingPx * index
            Column(
                Modifier
                    .zIndex(if (expandedIndex == index) 1f else 0f)
                    .offset(
                        getX = {
                            center + animatedOffset.value
                        },
                        getY = {
                            val distFromCenter = abs(animatedOffset.value + center) / spacingPx
                            lerp(0f, 50f, distFromCenter)
                        }
                    )
                    .align(Alignment.TopCenter)
            ) {
                foregroundContent(item, expandedIndex == index)
            }
        }
    }
}

@Composable fun Scope(content: @Composable () -> Unit) = content()

fun Modifier.carouselExpandedBackground(
    index: Int,
    expandedIndex: Int,
    getExpandedFraction: () -> Float,
) = this then object : DrawLayerModifier {
    override val alpha: Float
        get() {
            return getExpandedFraction()
        }

    override val translationY: Float
        get() {
            val indexOffset = abs(index - expandedIndex)
            return lerp(0f, -600f + 200 * indexOffset, getExpandedFraction())
        }

    override val translationX: Float
        get() {
            return (index - expandedIndex) * 170f
        }
}

fun Modifier.carouselBackground(
    index: Int,
    getIndexFraction: () -> Float,
    getExpandedFraction: () -> Float,
) = this then object : DrawLayerModifier {
    override val alpha: Float
        get() {
            val indexFraction = getIndexFraction()
            val leftIndex = floor(indexFraction).toInt()
            val rightIndex = ceil(indexFraction).toInt()
            return if (index == leftIndex || index == rightIndex)
                1f - getExpandedFraction()
            else
                0f
        }
    override val shape: Shape
        get() {
            val indexFraction = getIndexFraction()
            val leftIndex = floor(indexFraction).toInt()
            val rightIndex = ceil(indexFraction).toInt()
            return when (index) {
                rightIndex -> {
                    // 1, 0.25 -> 0f, 0.25
                    val fraction = indexFraction - index + 1
                    FractionalRectangleShape(0f, fraction)
                }
                leftIndex -> {
                    // 0, 0.25 -> 0.25, 1f
                    val fraction = indexFraction - index
                    FractionalRectangleShape(fraction, 1f)
                }
                else -> RectangleShape
            }
        }
    override val clip: Boolean
        get() = true
}

fun FractionalRectangleShape(startFraction: Float, endFraction: Float) = object : Shape {
    override fun createOutline(size: Size, density: Density) =
        Outline.Rectangle(
            Rect(
                top = 0f,
                left = (startFraction * size.width).coerceAtMost(size.width - 1f),
                bottom = size.height,
                right = (endFraction * size.width).coerceAtLeast(1f)
            )
        )
}

fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
}

fun Modifier.verticalGradient(vararg colors: ColorStop) = this then object :
    DrawModifier,
    InspectableParameter {

    // naive cache outline calculation if size is the same
    private var lastSize: Size? = null
    private var lastBrush: Brush? = null

    override fun ContentDrawScope.draw() {
        drawRect()
        drawContent()
    }

    private fun ContentDrawScope.drawRect() {
        var brush = lastBrush
        if (size != lastSize || brush == null) {
            brush = VerticalGradient(
                *colors,
                startY = 0f,
                endY = size.height
            )
            lastSize = size
            lastBrush = brush
        }
        drawRect(brush = brush, alpha = 1f)
    }

    override val nameFallback = "verticalGradient"

    override val valueOverride: Any?
        get() = colors

    override val inspectableElements: Sequence<ParameterElement>
        get() = sequenceOf(
            ParameterElement("colors", colors)
        )
}

fun Modifier.offset(
    getX: () -> Float,
    getY: () -> Float,
    rtlAware: Boolean = true
) = this then object : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureScope.MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            if (rtlAware) {
                placeable.placeRelative(getX().roundToInt(), getY().roundToInt())
            } else {
                placeable.place(getX().roundToInt(), getY().roundToInt())
            }
        }
    }
}

@OptIn(ExperimentalComposeApi::class)
fun <T, V : AnimationVector> BaseAnimatedValue<T, V>.animateTo(target: () -> T, scope: CoroutineScope) {
    snapshotFlow(target)
        .onEach { animateTo(it) }
        .launchIn(scope)
}

val imageWidthKey = DpPropKey()
val widthKey = DpPropKey()
val imageScaleKey = FloatPropKey()
val imageAlphaKey = FloatPropKey()
val bodyTranslateKey = FloatPropKey()
val bodyAlphaKey = FloatPropKey()
val posterPadding = 20.dp
fun makePosterTransition(expandedWidth: Dp, normalWidth: Dp) = transitionDefinition<Boolean> {
    state(true) { // expanded
        this[widthKey] = expandedWidth
        this[imageWidthKey] = 1.dp
        this[imageScaleKey] = 0f
        this[imageAlphaKey] = 0f
        this[bodyTranslateKey] = 0f
        this[bodyAlphaKey] = 1f
    }
    state(false) { // unexpanded
        this[widthKey] = normalWidth
        this[imageWidthKey] = normalWidth - posterPadding
        this[imageScaleKey] = 1f
        this[imageAlphaKey] = 1f
        this[bodyTranslateKey] = 100f
        this[bodyAlphaKey] = 0f
    }
    transition(false to true) {
        bodyTranslateKey using keyframes {
            100f at 200
            0f at 500 with FastOutLinearInEasing
        }
        bodyAlphaKey using keyframes {
//            delayMillis = 200
            0f at 200
            1f at 500
        }
//        imageWidthKey using spring(
//            dampingRatio = Spring.DampingRatioHighBouncy,
//            stiffness = Spring.StiffnessVeryLow
//        )
    }
}

@Composable fun MoviePoster(
    movie: PartialMovie,
    expanded: Boolean,
    expandedWidth: Dp,
    normalWidth: Dp,
    modifier: Modifier = Modifier
) {
    val tDef = remember(expandedWidth, normalWidth) { makePosterTransition(expandedWidth, normalWidth) }
    var fullMovie by remember { mutableStateOf<Movie?>(null) }
    val t = transition(tDef, expanded && fullMovie != null)
    ScrollableColumn(
        modifier = modifier
            .width(t[widthKey])
            .padding(top = 200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White),
        contentPadding = PaddingValues(posterPadding),
        isScrollEnabled = expanded,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CoilImage(
            movie.posterUrl,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .drawLayer(
                    scaleX = t[imageScaleKey],
                    scaleY = t[imageScaleKey],
                    alpha = t[imageAlphaKey],
                )
                .width(t[imageWidthKey])
                .aspectRatio(posterAspectRatio)
                .clip(RoundedCornerShape(10.dp))
        )
        Text(
            movie.title,
            fontSize = 24.sp,
            color = Color.Black
        )
        Row {
            for (chip in listOf("Action", "Drama", "History")) {
                Chip(chip)
            }
        }
        if (expanded) {
            val api = AmbientMovieApi.current
            launchInComposition {
                fullMovie = api.movie(movie.id)
            }
        }
        val fullMovie = fullMovie
        if (expanded && fullMovie != null) {
            Column(
                modifier = Modifier.drawLayer(
                    alpha = t[bodyAlphaKey],
                    translationY = t[bodyTranslateKey],
                )
            ) {
                Text(
                    "Actors",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                ScrollableRow {
                    fullMovie.credits.cast.filter { it.profile_path != null }.take(10).forEach {
                        Actor(it)
                    }
                }
                if (fullMovie.overview != null) {
                    Text(
                        "Overview",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                    )
                    Text(fullMovie.overview, style = MaterialTheme.typography.body2)
                }
            }
        }
    }
}

@Composable fun Actor(actor: CastMember) {
    Column {
        CoilImage(
            data = actor.profileUrl,
            modifier = Modifier
                .padding(end = 20.dp)
                .size(138.dp, 175.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Text(actor.name)
    }
}

@Composable fun BuyTicketButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        backgroundColor = Color.DarkGray,
        elevation = 0.dp,
        modifier = modifier
            .padding(vertical = 10.dp)
    ) {
        Text("Buy Ticket", color = Color.White)
    }
}

@Composable fun Chip(label: String, modifier: Modifier = Modifier) {
    Text(
        label,
        fontSize = 9.sp,
        color = Color.Gray,
        modifier = modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 2.dp)
    )
}
