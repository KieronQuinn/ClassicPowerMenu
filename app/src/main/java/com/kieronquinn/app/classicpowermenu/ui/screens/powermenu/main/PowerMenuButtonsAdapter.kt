package com.kieronquinn.app.classicpowermenu.ui.screens.powermenu.main

import android.animation.AnimatorInflater
import android.animation.LayoutTransition
import android.animation.ValueAnimator
import android.content.ClipData
import android.content.Context
import android.content.res.ColorStateList
import android.database.DataSetObserver
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.databinding.ItemPowerMenuButtonBinding
import com.kieronquinn.app.classicpowermenu.databinding.ItemPowerMenuButtonEmergencyBinding
import com.kieronquinn.app.classicpowermenu.databinding.ItemPowerMenuButtonEmptyBinding
import com.kieronquinn.app.classicpowermenu.model.power.PowerMenuButton
import com.kieronquinn.app.classicpowermenu.model.power.PowerMenuButtonId
import com.kieronquinn.app.classicpowermenu.model.power.PowerMenuButtonType
import com.kieronquinn.monetcompat.core.MonetCompat
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.coroutines.resume

class PowerMenuButtonsAdapter(context: Context, var items: MutableList<PowerMenuButton>, private val monetEnabled: Boolean, private val showSnackbar: ((text: CharSequence) -> Unit)? = null, private val setEmptyStateVisible: ((visible: Boolean) -> Unit)? = null, private val isWorkspace: Boolean = false): RecyclerView.Adapter<PowerMenuButtonsAdapter.ViewHolder>() {

    private lateinit var lifecycle: LifecycleCoroutineScope
    private val isDraggable
        get() = this::lifecycle.isInitialized

    private val dragEventCallback = MutableSharedFlow<DragEvent>()
    //Drag location is very noisy so gets its own flow
    private val dragLocationEventCallback = MutableSharedFlow<DragEvent.Location>()
    //Drop is priority
    private val dragDropCallback = MutableSharedFlow<DragEvent.Drop>()

    private val _changed = MutableSharedFlow<List<PowerMenuButtonId>>()
    val changed = _changed.asSharedFlow()

    private val layoutInflater by lazy {
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    private val monet by lazy {
        MonetCompat.getInstance()
    }

    private val googleSansMedium by lazy {
        ResourcesCompat.getFont(context, R.font.google_sans_text_medium)
    }

    private val monetPrimaryColor by lazy {
        if(monetEnabled){
            monet.getPrimaryColor(context)
        }else{
            ContextCompat.getColor(context, R.color.background_secondary)
        }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return items[position].type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(PowerMenuButtonType.values()[viewType]){
            PowerMenuButtonType.EMERGENCY -> ViewHolder.Emergency(
                ItemPowerMenuButtonEmergencyBinding.inflate(layoutInflater, parent, false)
            )
            PowerMenuButtonType.BUTTON -> ViewHolder.Button(
                ItemPowerMenuButtonBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
            PowerMenuButtonType.EMPTY -> ViewHolder.Empty(
                ItemPowerMenuButtonEmptyBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        when(holder){
            is ViewHolder.Emergency -> holder.setupEmergencyButton(item as PowerMenuButton.Emergency)
            is ViewHolder.Button -> holder.setupButton(item as PowerMenuButton.Button)
            is ViewHolder.Empty -> holder.setupEmpty(item as PowerMenuButton.Empty)
        }
    }

    private fun ViewHolder.Emergency.setupEmergencyButton(button: PowerMenuButton.Emergency){
        with(binding){
            if(isDraggable){
                itemPowerMenuButtonEmergencyDragLeft.setOnDragListener(DragCallback(DragEvent.DragSide.LEFT) { bindingAdapterPosition })
                itemPowerMenuButtonEmergencyDragRight.setOnDragListener(DragCallback(DragEvent.DragSide.RIGHT) { bindingAdapterPosition })
                itemPowerMenuButtonEmergencyRoot.stateListAnimator = AnimatorInflater.loadStateListAnimator(root.context, R.animator.animator_zoom_out)
                itemPowerMenuButtonEmergencyRoot.setOnLongClickListener {
                    if(canDragItem()) {
                        onItemLongTouch(button, binding.root)
                    }else{
                        showSnackbar?.invoke(it.resources.getString(R.string.settings_power_options_cannot_remove))
                    }
                    true
                }
            }
            itemPowerMenuButtonEmergencyText.typeface = googleSansMedium
            itemPowerMenuButtonEmergencyRoot.setOnClickListener {
                button.onClick()
            }
        }
    }

    private fun ViewHolder.Button.setupButton(button: PowerMenuButton.Button){
        with(binding){
            val backgroundColor = if(button.backgroundColor != null) {
                ContextCompat.getColor(root.context, button.backgroundColor)
            }else{
                monetPrimaryColor
            }
            itemPowerMenuButtonRoot.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            itemPowerMenuButtonIcon.setImageResource(button.icon)
            itemPowerMenuButtonText.text = button.text
            itemPowerMenuButtonText.typeface = googleSansMedium
            itemPowerMenuButtonRoot.setOnClickListener {
                button.onClick()
            }
            if(button.onLongClick != null) {
                itemPowerMenuButtonRoot.stateListAnimator = AnimatorInflater.loadStateListAnimator(root.context, R.animator.animator_zoom_out)
                itemPowerMenuButtonRoot.setOnLongClickListener {
                    button.onLongClick.invoke()
                    true
                }
            }else if(isDraggable) {
                itemPowerMenuButtonRoot.setOnLongClickListener {
                    if(canDragItem()) {
                        onItemLongTouch(button, binding.root)
                    }else{
                        showSnackbar?.invoke(it.resources.getString(R.string.settings_power_options_cannot_remove))
                    }
                    true
                }
            }else{
                itemPowerMenuButtonRoot.stateListAnimator = null
            }
            if(isDraggable){
                itemPowerMenuDragLeft.setOnDragListener(DragCallback(DragEvent.DragSide.LEFT) { bindingAdapterPosition })
                itemPowerMenuDragRight.setOnDragListener(DragCallback(DragEvent.DragSide.RIGHT) { bindingAdapterPosition })
            }
        }
    }

    private fun ViewHolder.Empty.setupEmpty(item: PowerMenuButton.Empty){
        with(binding.itemPowerMenuButtonEmptyDropper){
            setOnDragListener(
                DragCallback(DragEvent.DragSide.CENTER) { bindingAdapterPosition }
            )
            backgroundTintList = if(item.isHighlighted){
                ColorStateList.valueOf(monetPrimaryColor)
            }else{
                ColorStateList.valueOf(Color.TRANSPARENT)
            }
        }
    }

    fun setupDrag(recyclerView: RecyclerView, lifecycle: LifecycleCoroutineScope){
        this.lifecycle = lifecycle
        registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                changed()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                changed()
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                changed()
            }

            private fun changed(){
                setEmptyStateVisible?.invoke(items.filterNot { it is PowerMenuButton.Empty }.isEmpty())
                lifecycle.launch {
                    _changed.emit(items.mapNotNull { it.id })
                }
            }

            init {
                changed()
            }

        })
        recyclerView.setOnDragListener { _, dragEvent ->
            if(dragEvent.action == android.view.DragEvent.ACTION_DRAG_EXITED){
                removeEmptyItem()
                return@setOnDragListener true
            }
            if(dragEvent.action == android.view.DragEvent.ACTION_DRAG_ENTERED || dragEvent.action == android.view.DragEvent.ACTION_DRAG_STARTED){
                (recyclerView.adapter as PowerMenuButtonsAdapter).let {
                    if(it.itemCount == 0){
                        it.addEmptyItemAt(0)
                    }
                }
            }
            if(dragEvent.action == android.view.DragEvent.ACTION_DROP){
                (recyclerView.adapter as PowerMenuButtonsAdapter).removeEmptyItem()
            }
            dragEvent.action == android.view.DragEvent.ACTION_DRAG_STARTED
        }
        lifecycle.launchWhenResumed {
            launch {
                dragEventCallback.debounce(100).collect { dragEvent ->
                    onDragEvent(dragEvent)
                }
            }
            launch {
                dragLocationEventCallback.debounce(100).collect {
                    onDragLocationEvent(it)
                }
            }
            launch {
                dragDropCallback.debounce(100).collect {
                    onDragDropEvent(it)
                }
            }
        }
    }

    private fun onDragEvent(dragEvent: DragEvent){
        val itemIndex = dragEvent.itemIndex()
        if(itemIndex < 0 || itemIndex > items.size) return
        when(dragEvent){
            is DragEvent.Enter -> {
                val position = when (dragEvent.dragSide) {
                    DragEvent.DragSide.LEFT -> {
                        itemIndex
                    }
                    DragEvent.DragSide.RIGHT -> {
                        itemIndex + 1
                    }
                    DragEvent.DragSide.CENTER -> {
                        (items[itemIndex] as? PowerMenuButton.Empty)?.let {
                            it.isHighlighted = true
                            notifyItemChanged(itemIndex)
                        }
                        return
                    }
                }
                addEmptyItemAt(position)
            }
            is DragEvent.Ended -> {
                if(!dragEvent.dragItem.rejectionHandled) {
                    dragEvent.dragItem.rejectionHandled = true
                    dragEvent.dragItem.onRejected()
                }
                removeEmptyItem()
            }
            is DragEvent.Exit -> {
                (items[itemIndex] as? PowerMenuButton.Empty)?.let {
                    it.isHighlighted = false
                    notifyItemChanged(itemIndex)
                }
            }
        }
    }

    private fun onDragLocationEvent(locationEvent: DragEvent.Location){
        val itemIndex = locationEvent.itemIndex()
        if(itemIndex < 0 || itemIndex > items.size) return
        if(locationEvent.dragSide == DragEvent.DragSide.CENTER){
            (items[itemIndex] as? PowerMenuButton.Empty)?.let {
                if(!it.isHighlighted) {
                    it.isHighlighted = true
                    notifyItemChanged(itemIndex)
                }
            }
        }
    }

    private fun onDragDropEvent(dropEvent: DragEvent.Drop){
        val itemIndex = dropEvent.itemIndex()
        if(itemIndex == -1) return
        dropEvent.dragItem.rejectionHandled = true
        removeEmptyItem()
        items.add(itemIndex, dropEvent.dragItem.item)
        notifyItemInserted(itemIndex)
    }

    private fun addEmptyItemAt(position: Int) = with(items) {
        val adjustedPosition = max(min(position, size - 1), 0)
        indexOfFirst { it is PowerMenuButton.Empty }.let {
            if(it == -1) return@let
            if(adjustedPosition == it) return@with
            removeAt(it)
            notifyItemRemoved(it)
        }
        add(adjustedPosition, PowerMenuButton.Empty(false))
        notifyItemInserted(adjustedPosition)
    }

    private fun removeEmptyItem() = with(items) {
        indexOfFirst { it is PowerMenuButton.Empty }.let {
            if(it == -1) return@let
            removeAt(it)
            notifyItemRemoved(it)
        }
        if(items.size == 0){
            addEmptyItemAt(0)
        }
    }

    private fun onItemLongTouch(item: PowerMenuButton, view: View){
        val index = items.indexOfFirst { it == item }
        items.removeAt(index)
        notifyItemRemoved(index)
        val dragItem = DragItem(item){
            items.add(index, item)
            notifyItemInserted(index)
        }
        view.startDragAndDrop(ClipData.newPlainText("", ""), View.DragShadowBuilder(view), dragItem, View.DRAG_FLAG_GLOBAL)
    }

    private fun canDragItem(): Boolean {
        if(!isWorkspace) return true
        return items.size > 1
    }

    sealed class ViewHolder(open val binding: ViewBinding): RecyclerView.ViewHolder(binding.root) {
        data class Emergency(override val binding: ItemPowerMenuButtonEmergencyBinding): ViewHolder(binding)
        data class Button(override val binding: ItemPowerMenuButtonBinding): ViewHolder(binding)
        data class Empty(override val binding: ItemPowerMenuButtonEmptyBinding): ViewHolder(binding)
    }

    inner class DragCallback(private val dragSide: DragEvent.DragSide, private val itemIndex: () -> Int): View.OnDragListener {
        override fun onDrag(view: View, event: android.view.DragEvent): Boolean {
            when(event.action){
                android.view.DragEvent.ACTION_DRAG_ENTERED -> {
                    dragEventCallback(DragEvent.Enter(dragSide, itemIndex))
                }
                android.view.DragEvent.ACTION_DRAG_EXITED -> {
                    dragEventCallback(DragEvent.Exit(dragSide, itemIndex))
                }
                android.view.DragEvent.ACTION_DRAG_LOCATION -> {
                    dragLocationEventCallback(DragEvent.Location(dragSide, itemIndex))
                }
                android.view.DragEvent.ACTION_DROP -> {
                    dragDropEventCallback(DragEvent.Drop(event.localState as DragItem, dragSide, itemIndex))
                }
                android.view.DragEvent.ACTION_DRAG_ENDED -> {
                    dragEventCallback(DragEvent.Ended(event.localState as DragItem, dragSide, itemIndex))
                }
            }
            return true
        }

        private fun dragEventCallback(dragEvent: DragEvent) = lifecycle.launch {
            dragEventCallback.emit(dragEvent)
        }

        private fun dragLocationEventCallback(dragEvent: DragEvent.Location) = lifecycle.launch {
            dragLocationEventCallback.emit(dragEvent)
        }

        private fun dragDropEventCallback(dragEvent: DragEvent.Drop) = lifecycle.launch {
            dragDropCallback.emit(dragEvent)
        }
    }

    sealed class DragEvent(open val dragSide: DragSide, open val itemIndex: () -> Int) {

        data class Enter(override val dragSide: DragSide, override val itemIndex: () -> Int): DragEvent(dragSide, itemIndex)
        data class Exit(override val dragSide: DragSide, override val itemIndex: () -> Int): DragEvent(dragSide, itemIndex)
        data class Location(override val dragSide: DragSide, override val itemIndex: () -> Int): DragEvent(dragSide, itemIndex)
        data class Drop(val dragItem: DragItem, override val dragSide: DragSide, override val itemIndex: () -> Int): DragEvent(dragSide, itemIndex)
        data class Ended(val dragItem: DragItem, override val dragSide: DragSide, override val itemIndex: () -> Int): DragEvent(dragSide, itemIndex)

        enum class DragSide {
            LEFT, RIGHT, CENTER
        }
    }

    data class DragItem(val item: PowerMenuButton, var rejectionHandled: Boolean = false, val onRejected: () -> Unit)

}