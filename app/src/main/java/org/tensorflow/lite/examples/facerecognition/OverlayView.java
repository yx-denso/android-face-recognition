package org.tensorflow.lite.examples.facerecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class OverlayView extends View {
    private static final String TAG = "OverlayView";
    private static final int BOUNDING_RECT_TEXT_PADDING = 8;
    private List<RecognizedFace> results = new ArrayList<>();
    private Paint boxPaint = new Paint();
    private Paint textBackgroundPaint = new Paint();
    private Paint textPaint = new Paint();
    private Float scaleFactor = 1.0f;
    private Rect textBounds = new Rect();

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    public void clear() {
        textPaint.reset();
        textBackgroundPaint.reset();
        boxPaint.reset();
        invalidate();
        initPaints();
    }

    private void initPaints() {
        textBackgroundPaint.setColor(Color.BLACK);
        textBackgroundPaint.setStyle(Paint.Style.FILL);
        textBackgroundPaint.setTextSize(50f);

        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(50f);

        boxPaint.setColor(ContextCompat.getColor(getContext(), R.color.bounding_box_color));
        boxPaint.setStrokeWidth(8f);
        boxPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        for (RecognizedFace result : results) {
            RectF boundingBox = new RectF(result.face.getBoundingBox());
            //            Log.i(TAG, boundingBox.toString());
            //            Log.i(TAG, "top: " + boundingBox.top + ", left: " + boundingBox.left + ", bottom: " + boundingBox.bottom + ", right: " + boundingBox.right);

            float left = boundingBox.left * scaleFactor;;
            float top = boundingBox.top * scaleFactor;;
            float width = boundingBox.width() * scaleFactor;
            float height = boundingBox.height() * scaleFactor;
            // Draw bounding box around detected objects
            RectF drawableRect = new RectF(left, top, left + width, top + height);

            canvas.drawRect(drawableRect, boxPaint);

            // Create text to display alongside detected objects
            // TODO: display name
            String drawableText = result.name;

            // Draw rect behind display text
            textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length(), textBounds);
            int textWidth = textBounds.width();
            int textHeight = textBounds.height();
            canvas.drawRect(
                    left,
                    top,
                    left + textWidth + BOUNDING_RECT_TEXT_PADDING,
                    top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                    textBackgroundPaint
            );

            // Draw text for detected object
            canvas.drawText(drawableText, left, top + textBounds.height(), textPaint);
        }
    }

    public void setResults(List<RecognizedFace> detectionResults, int rotationDegree) {
        results = detectionResults;
//        Log.i(TAG, "Rotation degree: " + rotationDegree);
        //                Log.i(TAG, "View: (" + getWidth() + "," + getHeight() + ")");
        //        Log.i(TAG, "Image: (" + imageWidth + "," + imageHeight + ")");

        // PreviewView is in FILL_START mode. So we need to scale up the bounding box to match with
        // the size that the captured images will be displayed.
        int imageWidth = MyAppConfig.CAMERA_IMAGE_WIDTH;
        int imageHeight = MyAppConfig.CAMERA_IMAGE_HEIGHT;
        if (rotationDegree == 0 || rotationDegree == 180) {
            scaleFactor = Math.max(getWidth() * 1f / imageWidth, getHeight() * 1f / imageHeight);
        } else {
            scaleFactor = Math.max(getWidth() * 1f / imageHeight, getHeight() * 1f / imageWidth);
        }
    }
}


