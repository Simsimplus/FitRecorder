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
package io.simsim.fit.recorder.data

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager
import io.simsim.fit.recorder.ui.widget.AbstractDataProvider
import java.util.*

class ExampleDataProvider : AbstractDataProvider() {
    private val mData: MutableList<ConcreteData>
    private var mLastRemovedData: ConcreteData? = null
    private var mLastRemovedPosition = -1

    init {
        val a2z = 'A'..'Z'
        mData = LinkedList()
        for (i in 0..1) {
            for (element in a2z) {
                val id = mData.size.toLong()
                val viewType = 0
                val text = element.toString()
                val swipeReaction =
                    RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP or RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN
                mData.add(ConcreteData(id, viewType, text, swipeReaction))
            }
        }
    }

    override val count: Int
        get() = mData.size

    override fun getItem(index: Int): Data {
        if (index < 0 || index >= count) {
            throw IndexOutOfBoundsException("index = $index")
        }
        return mData[index]
    }

    override fun undoLastRemoval(): Int {
        return if (mLastRemovedData != null) {
            val insertedPosition: Int =
                if (mLastRemovedPosition >= 0 && mLastRemovedPosition < mData.size) {
                    mLastRemovedPosition
                } else {
                    mData.size
                }
            mData.add(insertedPosition, mLastRemovedData!!)
            mLastRemovedData = null
            mLastRemovedPosition = -1
            insertedPosition
        } else {
            -1
        }
    }

    override fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) {
            return
        }
        val item = mData.removeAt(fromPosition)
        mData.add(toPosition, item)
        mLastRemovedPosition = -1
    }

    override fun swapItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) {
            return
        }
        Collections.swap(mData, toPosition, fromPosition)
        mLastRemovedPosition = -1
    }

    override fun removeItem(position: Int) {
        val removedItem = mData.removeAt(position)
        mLastRemovedData = removedItem
        mLastRemovedPosition = position
    }

    class ConcreteData internal constructor(
        override val id: Long,
        override val viewType: Int,
        text: String,
        swipeReaction: Int
    ) : Data() {
        override val text: String
        override var isPinned = false

        init {
            this.text = makeText(id, text, swipeReaction)
        }

        override val isSectionHeader: Boolean
            get() = false

        override fun toString(): String {
            return text
        }

        companion object {
            private fun makeText(id: Long, text: String, swipeReaction: Int): String {
                return "$id - $text"
            }
        }
    }
}