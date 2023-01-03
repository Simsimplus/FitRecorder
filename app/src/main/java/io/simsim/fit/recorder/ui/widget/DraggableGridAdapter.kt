package io.simsim.fit.recorder.ui.widget


/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder
import io.simsim.fit.recorder.R


internal class DraggableGridAdapter(dataProvider: AbstractDataProvider) :
    RecyclerView.Adapter<DraggableGridAdapter.MyViewHolder>(),
    DraggableItemAdapter<DraggableGridAdapter.MyViewHolder> {
    private var mItemMoveMode = RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT
    private val mProvider: AbstractDataProvider

    class MyViewHolder(v: View) : AbstractDraggableItemViewHolder(v) {
        var mContainer: FrameLayout

        //        var mDragHandle: View
        var mTextView: TextView

        init {
            mContainer = v.findViewById(R.id.container)
//            mDragHandle = v.findViewById(R.id.drag_handle)
            mTextView = v.findViewById(R.id.text1)
        }
    }

    init {
        mProvider = dataProvider

        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true)
    }

    fun setItemMoveMode(itemMoveMode: Int) {
        mItemMoveMode = itemMoveMode
    }

    override fun getItemId(position: Int): Long {
        return mProvider.getItem(position).id
    }

    override fun getItemViewType(position: Int): Int {
        return mProvider.getItem(position).viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v: View = inflater.inflate(R.layout.list_grid_item, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item: AbstractDataProvider.Data = mProvider.getItem(position)

        // set text
        holder.mTextView.text = item.text

        // set background resource (target view ID: container)
        val dragState = holder.dragState
        if (dragState.isUpdated) {
            val bgResId: Int
            if (dragState.isActive) {
                bgResId = R.drawable.bg_item_dragging_active_state

                // need to clear drawable state here to get correct appearance of the dragging item.
                holder.mContainer.foreground.state = intArrayOf()
            } else if (dragState.isDragging) {
                bgResId = R.drawable.bg_item_dragging_state
            } else {
                bgResId = R.drawable.bg_item_normal_state
            }
            holder.mContainer.setBackgroundResource(bgResId)
        }
    }

    override fun getItemCount(): Int {
        return mProvider.count
    }

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        Log.d(
            TAG,
            "onMoveItem(fromPosition = $fromPosition, toPosition = $toPosition)"
        )
        if (mItemMoveMode == RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT) {
            mProvider.moveItem(fromPosition, toPosition)
        } else {
            mProvider.swapItem(fromPosition, toPosition)
        }
    }

    override fun onCheckCanStartDrag(holder: MyViewHolder, position: Int, x: Int, y: Int): Boolean {
        return true
    }

    override fun onGetItemDraggableRange(holder: MyViewHolder, position: Int): ItemDraggableRange? {
        // no drag-sortable range specified
        return null
    }

    override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean {
        return true
    }

    override fun onItemDragStarted(position: Int) {
        notifyDataSetChanged()
    }

    override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {
        notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "MyDraggableItemAdapter"
    }
}

abstract class AbstractDataProvider {
    abstract class Data {
        abstract val id: Long
        abstract val isSectionHeader: Boolean
        abstract val viewType: Int
        abstract val text: String?
        abstract var isPinned: Boolean
    }

    abstract val count: Int

    abstract fun getItem(index: Int): Data
    abstract fun removeItem(position: Int)
    abstract fun moveItem(fromPosition: Int, toPosition: Int)
    abstract fun swapItem(fromPosition: Int, toPosition: Int)
    abstract fun undoLastRemoval(): Int
}