package com.afzal.jeecountdown;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Simple LinearLayout subclass used as card containers in XML.
 * Acts as a named hook so layout XML can reference this class directly.
 */
public class GlowCardView extends LinearLayout {

    public GlowCardView(Context context) {
        super(context);
    }

    public GlowCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GlowCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
