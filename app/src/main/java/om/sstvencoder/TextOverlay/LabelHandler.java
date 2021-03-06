/*
Copyright 2015 Olga Miller <olga.rgb@googlemail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package om.sstvencoder.TextOverlay;

import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.LinkedList;
import java.util.List;

public class LabelHandler {
    private List<Label> mLabels;
    private Label mEditLabel;
    private float mPreviousX, mPreviousY;

    public LabelHandler() {
        mLabels = new LinkedList<>();
        mPreviousX = 0;
        mPreviousY = 0;
    }

    public LabelSettings editLabelBegin(float x, float y, float w, float h) {
        mEditLabel = findLabel(x, y);
        if (mEditLabel == null)
            mEditLabel = new Label(x, y, w, h);
        return mEditLabel.getSettings();
    }

    public boolean editLabelEnd(LabelSettings settings) {
        boolean edited = false;
        if (settings != null) {
            if (settings.getText().length() != 0) {
                if (!mLabels.contains(mEditLabel))
                    add(mEditLabel);
                mEditLabel.loadSettings(settings);
            } else if (mLabels.contains(mEditLabel)) {
                mLabels.remove(mEditLabel);
            }
            edited = true;
        }
        mEditLabel = null;
        return edited;
    }

    public void drawLabels(Canvas canvas) {
        for (Label label : mLabels)
            label.draw(canvas);
    }

    public void drawLabels(Canvas canvas, Rect src, Rect dst) {
        for (Label label : mLabels)
            label.draw(canvas, src, dst);
    }

    public void update(float w, float h) {
        for (Label label : mLabels)
            label.update(w, h);
    }

    public boolean dragLabel(float x, float y) {
        if (mEditLabel == null) {
            Label label = findLabel(x, y);
            if (label != null) {
                mEditLabel = label;
                moveToFront(mEditLabel);
                mPreviousX = x;
                mPreviousY = y;
                mEditLabel.drag();
                return true;
            }
        }
        return false;
    }

    public void dropLabel() {
        mEditLabel.drop();
        mEditLabel = null;
    }

    public void moveLabel(float x, float y) {
        mEditLabel.move(x - mPreviousX, y - mPreviousY);
        mPreviousX = x;
        mPreviousY = y;
    }

    private Label findLabel(float x, float y) {
        for (Label label : mLabels) {
            if (label.contains(x, y))
                return label;
        }
        return null;
    }

    private void add(Label label) {
        if (mLabels.size() == 0)
            mLabels.add(label);
        else
            mLabels.add(0, label);
    }

    private void moveToFront(Label label) {
        mLabels.remove(label);
        add(label);
    }
}
