package android.danyk.Utilidades;

import android.content.Context;
import android.danyk.R;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ImagePreviewItem extends LinearLayout {
    private ImageView imageView;
    private TextView textView;

    public ImagePreviewItem(Context context) {
        super(context);
        initializeViews(context);
    }

    public ImagePreviewItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ImagePreviewItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.image_preview_item, this);
        imageView = findViewById(R.id.image_preview);
        textView = findViewById(R.id.image_name);
    }

    public void setImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    public void setImageName(String name) {
        textView.setText(name);
    }
}

