package android.danyk.Animaciones;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.danyk.Actividades.actividad_login;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.danyk.R;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Animacion_inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.animacion_inicio);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorBarra));
        }

        final ImageView logoImageView = findViewById(R.id.logoImageView);
        final TextView appNameTextView = new TextView(this);
        appNameTextView.setText("T-Manager");
        appNameTextView.setTextColor(getResources().getColor(android.R.color.black));
        appNameTextView.setTextSize(40);
        appNameTextView.setTextColor(Color.parseColor("#ffffff"));
        appNameTextView.setVisibility(View.INVISIBLE);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Nexa_Heavy.ttf");
        appNameTextView.setTypeface(typeface);

        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.BELOW, R.id.logoImageView);
        appNameTextView.setLayoutParams(params);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        final RelativeLayout parentLayout = findViewById(R.id.parentLayout);
        parentLayout.addView(appNameTextView);

        final TranslateAnimation logoAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -0.7f,
                Animation.RELATIVE_TO_SELF, 0
        );
        logoAnimation.setDuration(2000);
        logoAnimation.setFillAfter(true);
        logoImageView.startAnimation(logoAnimation);

        final AlphaAnimation textAnimation = new AlphaAnimation(0.0f, 1.0f);
        textAnimation.setDuration(2000);
        textAnimation.setStartOffset(1000);

        TranslateAnimation textSlideAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f
        );
        textSlideAnimation.setDuration(2000);
        textSlideAnimation.setFillAfter(true);
        params.addRule(RelativeLayout.BELOW, R.id.logoImageView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                appNameTextView.setVisibility(View.VISIBLE);
                appNameTextView.startAnimation(textSlideAnimation);
                appNameTextView.startAnimation(textAnimation);
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Animacion_inicio.this, actividad_login.class);
                startActivity(intent);
                overridePendingTransition(R.anim.aparecer_transicion, R.anim.desaparecer_transicion);
                finish();
            }
        }, 4000);
    }
}