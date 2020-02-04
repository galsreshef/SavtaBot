package com.greystudio.savtabot;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        spanText();

        Thread splash_screen = new Thread() {
            public void run() {

                try
                {

                    sleep(2000);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    Intent mainMenu = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(mainMenu);
                }
            }
        };

        splash_screen.start();
    }

    private void spanText() {
        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.RED);
        ForegroundColorSpan fcs2 = new ForegroundColorSpan(Color.RED);
        ForegroundColorSpan fcs3 = new ForegroundColorSpan(Color.RED);
        ForegroundColorSpan fcs4 = new ForegroundColorSpan(Color.RED);
        TextView tvOwner, tvOwner2;
        SpannableString ss1 = new SpannableString(getString(R.string.grey_studio));
        SpannableString ss2 = new SpannableString(getString(R.string.gal_reshef_eliyahu_yakubov));
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        StyleSpan boldSpan2 = new StyleSpan(Typeface.BOLD);
        StyleSpan boldSpan3 = new StyleSpan(Typeface.BOLD);
        StyleSpan boldSpan4 = new StyleSpan(Typeface.BOLD);
        tvOwner = findViewById(R.id.tvOwner);
        tvOwner2 = findViewById(R.id.tvOwner2);
        ss1.setSpan(fcs,0,4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss1.setSpan(boldSpan,0,4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(fcs,0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(fcs2,4,5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(fcs3,13,14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(fcs4,21,22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(boldSpan,0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(boldSpan2,4,5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(boldSpan3,13,14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(boldSpan4,21,22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvOwner.setText(ss1);
        tvOwner2.setText(ss2);

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        finish();
    }
}
