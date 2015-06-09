package com.apphunt.app.ui.widgets;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class AvatarImageView extends ImageView implements Target {

    private static final String TAG = AvatarImageView.class.getName();

    public AvatarImageView(Context context) {
        super(context);
    }

    public AvatarImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AvatarImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //constructors

    @Override
    public void onBitmapFailed(Drawable drawable) {
        //TODO
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadFrom) {
        new AsyncTask<Bitmap, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Bitmap... params) {
                return cropCircle(params[0].isMutable() ? params[0] : params[0].copy(Bitmap.Config.ARGB_8888, true));
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                setImageBitmap(bitmap);
            }
        }.execute(bitmap);
    }

    @Override
    public void onPrepareLoad(Drawable arg0) {
        //TODO
    }

    public Bitmap cropCircle(Bitmap bm) {
        Bitmap mCanvasBitmap = null;

        int width = bm.getWidth();
        int height = bm.getHeight();

        Bitmap cropped_bitmap;

        try {
            /* Crop the bitmap so it'll display well as a circle. */
            if (width > height) {
                cropped_bitmap = Bitmap.createBitmap(bm,
                        (width / 2) - (height / 2), 0, height, height);
            } else {
                cropped_bitmap = Bitmap.createBitmap(bm, 0, (height / 2)
                        - (width / 2), width, width);
            }

            BitmapShader shader = new BitmapShader(cropped_bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(shader);

            height = cropped_bitmap.getHeight();
            width = cropped_bitmap.getWidth();

            mCanvasBitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mCanvasBitmap);
            canvas.drawCircle(width / 2, height / 2, width / 2, paint);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Cannot create bitmap");
        }

        return mCanvasBitmap;
    }

}
