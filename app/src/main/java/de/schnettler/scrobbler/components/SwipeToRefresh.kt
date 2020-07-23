package de.schnettler.scrobbler.components

import androidx.animation.*
import androidx.annotation.FloatRange
import androidx.compose.Composable
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.animation.asDisposableClock
import androidx.ui.core.*
import androidx.ui.core.gesture.scrollorientationlocking.Orientation
import androidx.ui.foundation.Box
import androidx.ui.foundation.InteractionState
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.foundation.animation.fling
import androidx.ui.foundation.gestures.draggable
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.layout.Stack
import androidx.ui.layout.offset
import androidx.ui.layout.padding
import androidx.ui.layout.preferredSize
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.Surface
import androidx.ui.unit.dp
import androidx.ui.util.fastFirstOrNull
import kotlin.math.sign

private val SWIPE_DISTANCE_SIZE = 100.dp
private const val SWIPE_DOWN_OFFSET = 1.2f

@Composable
fun SwipeToRefreshLayout(
    refreshingState: Boolean,
    onRefresh: () -> Unit,
    refreshIndicator: @Composable() () -> Unit,
    content: @Composable() () -> Unit
) {
    val size = with(DensityAmbient.current) { SWIPE_DISTANCE_SIZE.toPx() }
    // min is below negative to hide
    val min = -size
    val max = size * SWIPE_DOWN_OFFSET
    val dragPosition = state { 0f }
    Box(Modifier.stateDraggable(
        state = refreshingState,
        onStateChange = { shouldRefresh -> if (shouldRefresh) onRefresh() },
        anchorsToState = listOf(min to false, max to true),
        minValue = min,
        maxValue = max,
        animationSpec = TweenSpec(),
        orientation = Orientation.Vertical,
        onNewValue = { newValue ->
            dragPosition.value = newValue
        }
    )) {
        val dpOffset = with(DensityAmbient.current) {
            (dragPosition.value * 0.5f).toDp()
        }
        Stack {
            content()
            Box(Modifier.gravity(Alignment.TopCenter).offset(0.dp, dpOffset)) {
                if (dragPosition.value != min) {
                    refreshIndicator()
                }
            }
        }
    }
}

@Composable
fun SwipeRefreshPrograssIndicator() {
    Surface(elevation = 10.dp, shape = CircleShape) {
        CircularProgressIndicator(modifier = Modifier.preferredSize(40.dp).padding(6.dp), strokeWidth = 2.5.dp)
    }
}

// Copied from ui/ui-material/src/main/java/androidx/ui/material/internal/StateDraggable.kt

/**
 * Higher-level component that allows dragging around anchored positions binded to different states
 *
 * Example might be a Switch which you can drag between two states (true or false).
 *
 * Additional features compared to regular [draggable] modifier:
 * 1. The AnimatedFloat hosted inside and its value will be in sync with call site state
 * 2. When the anchor is reached, [onStateChange] will be called with state mapped to this anchor
 * 3. When the anchor is reached and [onStateChange] with corresponding state is called, but
 * call site didn't update state to the reached one for some reason,
 * this component performs rollback to the previous (correct) state.
 * 4. When new [state] is provided, component will be animated to state's anchor
 *
 * children of this composable will receive [AnimatedFloat] class from which
 * they can read current value when they need or manually animate.
 *
 * @param T type with which state is represented
 * @param state current state to represent Float value with
 * @param onStateChange callback to update call site's state
 * @param anchorsToState pairs of anchors to states to map anchors to state and vise versa
 * @param animationBuilder animation which will be used for animations
 * @param dragDirection direction in which drag should be happening.
 * Either [DragDirection.Vertical] or [DragDirection.Horizontal]
 * @param minValue lower bound for draggable value in this component
 * @param maxValue upper bound for draggable value in this component
 * @param enabled whether or not this Draggable is enabled and should consume events
 */
// TODO(malkov/tianliu) (figure our how to make it better and make public)
internal fun <T> Modifier.stateDraggable(
    state: T,
    onStateChange: (T) -> Unit,
    anchorsToState: List<Pair<Float, T>>,
    animationSpec: AnimationSpec<Float>,
    orientation: Orientation,
    thresholds: (Float, Float) -> Float = fractionalThresholds(0.5f),
    enabled: Boolean = true,
    reverseDirection: Boolean = false,
    minValue: Float = Float.MIN_VALUE,
    maxValue: Float = Float.MAX_VALUE,
    interactionState: InteractionState? = null,
    onNewValue: (Float) -> Unit
) = composed {
    val forceAnimationCheck = state { true }
    val anchors = remember(anchorsToState) { anchorsToState.map { it.first } }
    val currentValue = anchorsToState.fastFirstOrNull { it.second == state }!!.first
    val onAnimationEnd: OnAnimationEnd = { reason, finalValue, _ ->
        if (reason != AnimationEndReason.Interrupted) {
            val newState = anchorsToState.firstOrNull { it.first == finalValue }?.second
            if (newState != null && newState != state) {
                onStateChange(newState)
                forceAnimationCheck.value = !forceAnimationCheck.value
            }
        }
    }
    val flingConfig = FlingConfig(
        decayAnimation = ExponentialDecay(),
        adjustTarget = { target ->
            // Find the two anchors the target lies between.
            val a = anchors.filter { it <= target }.maxOrNull()
            val b = anchors.filter { it >= target }.minOrNull()
            // Compute which anchor to fling to.
            val adjusted: Float =
                if (a == null && b == null) {
                    // There are no anchors, so return the target unchanged.
                    target
                } else if (a == null) {
                    // The target lies below the anchors, so return the first anchor (b).
                    b!!
                } else if (b == null) {
                    // The target lies above the anchors, so return the last anchor (b).
                    a
                } else if (a == b) {
                    // The target is equal to one of the anchors, so return the target unchanged.
                    target
                } else {
                    // The target lies strictly between the two anchors a and b.
                    // Compute the threshold between a and b based on the drag direction.
                    val threshold = if (currentValue <= a) {
                        thresholds(a, b)
                    } else {
                        thresholds(b, a)
                    }
                    require(threshold >= a && threshold <= b) {
                        "Invalid threshold $threshold between anchors $a and $b."
                    }
                    if (target < threshold) a else b
                }
            TargetAnimation(adjusted, animationSpec)
        }
    )
    val clock = AnimationClockAmbient.current.asDisposableClock()
    val position = remember(clock) {
        onNewValue(currentValue)
        NotificationBasedAnimatedFloat(currentValue, clock, onNewValue)
    }
    position.onNewValue = onNewValue
    position.setBounds(minValue, maxValue)
    // This state is to force this component to be recomposed and trigger onCommit below
    // This is needed to stay in sync with drag state that caller side holds
    onCommit(currentValue, forceAnimationCheck.value) {
        position.animateTo(currentValue, animationSpec)
    }
    Modifier.draggable(
        orientation = orientation,
        enabled = enabled,
        reverseDirection = reverseDirection,
        startDragImmediately = position.isRunning,
        interactionState = interactionState,
        onDragStopped = { position.fling(it, flingConfig, onAnimationEnd) }
    ) { delta ->
        position.snapTo(position.value + delta)
    }
}

/**
 * Fixed anchors thresholds. Each threshold will be at an [offset] away from the first anchor.
 */
internal fun fixedThresholds(offset: Float): (Float, Float) -> Float =
    { fromAnchor, toAnchor -> fromAnchor + offset * sign(toAnchor - fromAnchor) }
/**
 * Fractional thresholds. Each threshold will be at a [fraction] of the way between the two anchors.
 */
internal fun fractionalThresholds(
    @FloatRange(from = 0.0, to = 1.0) fraction: Float
): (Float, Float) -> Float = { fromAnchor, toAnchor -> lerp(fromAnchor, toAnchor, fraction) }
private class NotificationBasedAnimatedFloat(
    initial: Float,
    clock: AnimationClockObservable,
    internal var onNewValue: (Float) -> Unit
) : AnimatedFloat(clock, Spring.DefaultDisplacementThreshold) {
    override var value = initial
        set(value) {
            onNewValue(value)
            field = value
        }
}

internal fun lerp(start: Float, stop: Float, fraction: Float) =
    (start * (1 - fraction) + stop * fraction)