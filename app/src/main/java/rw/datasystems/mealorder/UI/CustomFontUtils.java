package rw.datasystems.mealorder.UI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import rw.datasystems.mealorder.R;

/**
 * Created by norman on 3/8/15.
 */
public class CustomFontUtils extends AppCompatActivity {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public static void applyCustomFont(TextView customFontTextView, Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.CustomFontTextView);

//        String fontName = attributeArray.getInt(R.styleable.CustomFontTextView_font);

        // check if a special textStyle was used (e.g. extra bold)
        int textStyle = attributeArray.getInt(R.styleable.CustomFontTextView_textStyle, 0);

        // if nothing extra was used, fall back to regular android:textStyle parameter
        if (textStyle == 0) {
            textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
        }

//        Typeface customFont = selectTypeface(context, fontName, textStyle);
//        customFontTextView.setTypeface(customFont);

        attributeArray.recycle();
    }

    private static Typeface selectTypeface(Context context, String fontName, int textStyle) {

            switch (textStyle) {
                case Typeface.BOLD: // bold
                    return FontCache.getTypeface("GOTHICB.TTF", context);

                case Typeface.NORMAL: // italic
                    return FontCache.getTypeface("GOTHIC.TTF", context);

                default:
                    return FontCache.getTypeface("GOTHIC.TTF", context);
            }

    }
}