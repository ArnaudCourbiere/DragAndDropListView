/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.courbiere.android.draganddroplistview;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import java.util.ArrayList;

/**
 * Sortable ResourceCursorAdapter. Used for the drag and drop list view.
 */
public abstract class SortableCursorAdapter
        extends CursorAdapter implements DragAndDropListView.SortableAdapter {

    private static final String TAG = "SortableCursorAdapter";

    /**
     * Items positions mappings, key is list view position, value is cursor position.
     */
    private SparseIntArray mListMapping = new SparseIntArray();

    public SortableCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public SortableCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public SortableCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cursor swapCursor(Cursor c) {
        Cursor old = super.swapCursor(c);
        resetMappings();
        return old;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        resetMappings();
    }

    private void resetMappings() {
        mListMapping.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getItem(int position) {
        return super.getItem(mListMapping.get(position, position));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(int position) {
        return super.getItemId(mListMapping.get(position, position));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(mListMapping.get(position, position), convertView, parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(mListMapping.get(position, position), convertView, parent);
    }

    /**
     * Swaps two items positions.
     *
     * @param from First item's position.
     * @param to Second item's position.
     */
    @Override
    public void swap(int from, int to) {
        if (from != to) {
            int cursorFrom = mListMapping.get(from, from);

            if (from > to) {
                for (int i = from; i > to; i--) {
                    mListMapping.put(i, mListMapping.get(i - 1, i - 1));
                }
            } else {
                for (int i = from; i < to; i++) {
                    mListMapping.put(i, mListMapping.get(i + 1, i + 1));
                }
            }

            mListMapping.put(to, cursorFrom);

            cleanMapping();
            notifyDataSetChanged();
        }
    }

    /**
     * Removes unnecessary mappings from the sparse array.
     */
    private void cleanMapping() {
        ArrayList<Integer> toRemove = new ArrayList<Integer>();

        int size = mListMapping.size();
        for (int i = 0; i < size; ++i) {
            if (mListMapping.keyAt(i) == mListMapping.valueAt(i)) {
                toRemove.add(mListMapping.keyAt(i));
            }
        }

        size = toRemove.size();
        for (int i = 0; i < size; ++i) {
            mListMapping.delete(toRemove.get(i));
        }
    }
}
