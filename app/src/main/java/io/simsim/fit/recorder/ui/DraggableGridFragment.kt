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
package io.simsim.fit.recorder.ui

import android.graphics.drawable.NinePatchDrawable
import android.os.Bundle
import android.view.*
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils
import io.simsim.fit.recorder.R
import io.simsim.fit.recorder.data.ExampleDataProvider
import io.simsim.fit.recorder.databinding.FragmentRecyclerListViewBinding
import io.simsim.fit.recorder.ui.widget.AbstractDataProvider
import io.simsim.fit.recorder.ui.widget.DraggableGridAdapter

class DraggableGridFragment : Fragment() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var mAdapter: DraggableGridAdapter
    private lateinit var mWrappedAdapter: RecyclerView.Adapter<*>
    private lateinit var mRecyclerViewDragDropManager: RecyclerViewDragDropManager
    private lateinit var binding: FragmentRecyclerListViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecyclerListViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = binding.recyclerView
        mLayoutManager = GridLayoutManager(requireContext(), 5, RecyclerView.VERTICAL, false)

        // drag & drop manager
        mRecyclerViewDragDropManager = RecyclerViewDragDropManager()
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.material_shadow_z3
            ) as NinePatchDrawable?
        )
        // Start dragging after long press
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true)
        mRecyclerViewDragDropManager.setInitiateOnMove(false)
        mRecyclerViewDragDropManager.setLongPressTimeout(750)

        // setup dragging item effects (NOTE: DraggableItemAnimator is required)
        mRecyclerViewDragDropManager.dragStartItemAnimationDuration = 250
        mRecyclerViewDragDropManager.draggingItemAlpha = 0.8f
        mRecyclerViewDragDropManager.draggingItemScale = 1.0f
        mRecyclerViewDragDropManager.draggingItemRotation = 0.0f

        //adapter
        val myItemAdapter = DraggableGridAdapter(dataProvider)
        mAdapter = myItemAdapter
        mWrappedAdapter =
            mRecyclerViewDragDropManager.createWrappedAdapter(myItemAdapter) // wrap for dragging
        val animator: GeneralItemAnimator =
            DraggableItemAnimator() // DraggableItemAnimator is required to make item animations properly.
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = mWrappedAdapter // requires *wrapped* adapter
        mRecyclerView.itemAnimator = animator

        // additional decorations
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            mRecyclerView.addItemDecoration(
                ItemShadowDecorator(
                    (ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.material_shadow_z1
                    ) as NinePatchDrawable?)!!
                )
            )
        }
        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView)

        // for debugging
//        animator.setDebug(true);
//        animator.setMoveDuration(2000);
    }

    override fun onPause() {
        mRecyclerViewDragDropManager.cancelDrag()
        super.onPause()
    }

    override fun onDestroyView() {
        mRecyclerViewDragDropManager.release()
        WrapperAdapterUtils.releaseAll(mWrappedAdapter)
        super.onDestroyView()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_drag_grid, menu)

        // setting up the item move mode selection switch
        val menuSwitchItem = menu.findItem(R.id.menu_switch_swap_mode)
        menuSwitchItem.actionView?.findViewById<CompoundButton>(R.id.switch_view)
            ?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                updateItemMoveMode(
                    isChecked
                )
            }
    }

    private fun updateItemMoveMode(swapMode: Boolean) {
        val mode =
            if (swapMode) RecyclerViewDragDropManager.ITEM_MOVE_MODE_SWAP else RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT
        mRecyclerViewDragDropManager.itemMoveMode = mode
        mAdapter.setItemMoveMode(mode)
        Snackbar.make(
            requireView(),
            "Item move mode: " + if (swapMode) "SWAP" else "DEFAULT",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun supportsViewElevation(): Boolean {
        return true
    }

    private val dataProvider: AbstractDataProvider
        get() = ExampleDataProvider()
}