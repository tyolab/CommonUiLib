package au.com.tyo.common.ui;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 22/3/17.
 */

public class CardBox extends PreviewBox {

    private int index;

    public CardBox(Context context) {
        super(context);

        init(context);
    }

    public CardBox(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public CardBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

}
