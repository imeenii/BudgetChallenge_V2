package com.example.budgetchallenge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

public class DonutChartView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final float[] values = {500, 350, 150, 200, 200};

    private final int[] colors = {
            0xFF244B3C,
            0xFF578F70,
            0xFFA9C5AD,
            0xFF829C89,
            0xFFC2D2C2
    };

    public DonutChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setData(double rent, double food, double transport, double bills, double other) {
        values[0] = (float) rent;
        values[1] = (float) food;
        values[2] = (float) transport;
        values[3] = (float) bills;
        values[4] = (float) other;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float density = getResources().getDisplayMetrics().density;
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        float radius = Math.min(centerX, centerY) - 12 * density;
        float stroke = 19 * density;

        RectF rect = new RectF(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
        );

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stroke);
        paint.setStrokeCap(Paint.Cap.ROUND);

        float startAngle = -90;
        float total = 0;
        for (float v : values) {
            total += v;
        }

        for (int i = 0; i < values.length; i++) {
            float sweep = total > 0 ? (values[i] / total * 360f) : 0f;

            paint.setColor(colors[i]);

            canvas.drawArc(
                    rect,
                    startAngle,
                    sweep - 2,
                    false,
                    paint
            );

            startAngle += sweep;
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(0xFF173F32);
        paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        paint.setTextSize(21 * density);

        String totalText = String.format(Locale.FRANCE, "%.0f DT", total);
        canvas.drawText(totalText, centerX, centerY, paint);

        paint.setTypeface(android.graphics.Typeface.DEFAULT);
        paint.setTextSize(11 * density);
        paint.setColor(0xFF829087);

        canvas.drawText(
                "Total dépenses",
                centerX,
                centerY + 20 * density,
                paint
        );
    }
}
