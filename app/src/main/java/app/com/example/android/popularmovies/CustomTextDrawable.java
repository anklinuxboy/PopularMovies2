package app.com.example.android.popularmovies;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Created by ankit on 10/15/16.
 */

public class CustomTextDrawable extends Drawable {
    private Paint paint;
    private String title;
    public CustomTextDrawable(String mtitle) {
        this.title = mtitle;
        this.paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(14f);
//        paint.setAntiAlias(true);
//        paint.setFakeBoldText(true);
//        paint.setShadowLayer(6f, 0, 0, Color.BLACK);
//        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(title, 100, 100, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
