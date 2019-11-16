package rw.datasystems.mealorder.UI;

import android.content.Context;
import android.util.AttributeSet;


public class CustomFontText extends androidx.appcompat.widget.AppCompatTextView{

    public CustomFontText(Context context) {
        super(context);

        CustomFontUtils.applyCustomFont(this, context, null);
    }

    public CustomFontText(Context context, AttributeSet attrs) {
        super(context, attrs);

        CustomFontUtils.applyCustomFont(this, context, attrs);
    }

    public CustomFontText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CustomFontUtils.applyCustomFont(this, context, attrs);
    }
}