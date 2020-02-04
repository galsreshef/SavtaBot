package com.greystudio.savtabot;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import java.util.Locale;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends AppCompatActivity implements AIListener {

    private ConstraintLayout constraintlayout;
    private EditText etUserText;
    private ImageView ivSettings;
    private RelativeLayout addBtn;
    private Boolean flagFab = true;
    private AIService aiService;
    private LinearLayout linearLayoutChat;
    private TextToSpeech tts;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String textUserMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        constraintlayout = findViewById(R.id.ConstraintLayout);

        linearLayoutChat = findViewById(R.id.linearLayout1);
        etUserText = findViewById(R.id.etUserText);
        addBtn = findViewById(R.id.addBtn);
        ivSettings = findViewById(R.id.ivSettings);
        String MY_PREFS_FILENAME = "com.greystudio.savtabot.prefs";
        prefs = getSharedPreferences(MY_PREFS_FILENAME, MODE_PRIVATE);
        boolean runBefore = prefs.getBoolean("RunBefore", false);
        if (!runBefore) {
            editor = prefs.edit();
            editor.putBoolean("RunBefore", true);
            editor.apply();
            showTutorials();
        }

        final AIConfiguration config = new AIConfiguration("Put your Access Token Here",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        final AIDataService aiDataService = new AIDataService(config);
        final AIRequest aiRequest = new AIRequest();

        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageChangeDialog();
            }
        });


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "Language not supported", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Initialization failed", Toast.LENGTH_LONG).show();
                }
            }
        });


        etUserText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ImageView fab_img = findViewById(R.id.ivFab);
                Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.ic_send_white_24dp);
                Bitmap img1 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mic_white_24dp);
                if (s.toString().trim().length() != 0 && flagFab) {
                    ImageViewAnimatedChange(MainActivity.this, fab_img, img);
                    flagFab = false;

                } else if (s.toString().trim().length() == 0) {
                    ImageViewAnimatedChange(MainActivity.this, fab_img, img1);
                    flagFab = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textUserMessage = etUserText.getText().toString().trim();

                if (!textUserMessage.equals("")) {
                    TextView user = new TextView(MainActivity.this);
                    user.setText(textUserMessage);
                    user.setTextColor(getResources().getColor(R.color.colorPrimary));
                    user.setTextSize(30);
                    linearLayoutChat.addView(user);

                    aiRequest.setQuery(textUserMessage);
                    new AsyncTask<AIRequest, Void, AIResponse>() {

                        @Override
                        protected AIResponse doInBackground(AIRequest... aiRequests) {
                            try {
                                return aiDataService.request(aiRequest);
                            } catch (AIServiceException ignored) {
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(AIResponse response) {
                            if (response != null) {

                                Result result = response.getResult();
                                String reply = result.getFulfillment().getSpeech();
                                TextView bot = new TextView(MainActivity.this);
                                bot.setText(reply);
                                bot.setTextColor(getResources().getColor(R.color.colorAccent));
                                bot.setTextSize(20);
                                bot.setGravity(Gravity.END);
                                linearLayoutChat.addView(bot);
                                tts.setPitch(0.9f);
                                tts.setSpeechRate(0.8F);

                                // check for device version
                                if (Build.VERSION.SDK_INT >= 21)
                                    tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null, null);
                                else tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    }.execute(aiRequest);
                } else
                    aiService.startListening();
                etUserText.setText("");
            }
        });
    }

    @Override
    public void onResult(ai.api.model.AIResponse response) {

        Result result = response.getResult();
        String message = result.getResolvedQuery();
        TextView user = new TextView(MainActivity.this);
        user.setText(message);
        user.setTextColor(getResources().getColor(R.color.colorPrimary));
        user.setTextSize(30);
        String reply = result.getFulfillment().getSpeech();
        TextView bot = new TextView(MainActivity.this);

        bot.setText(reply);
        bot.setTextColor(getResources().getColor(R.color.colorAccent));
        bot.setTextSize(20);
        bot.setGravity(Gravity.END);
        linearLayoutChat.addView(user);
        linearLayoutChat.addView(bot);
        tts.setPitch(0.9f);
        tts.setSpeechRate(0.8F);

        // check for device version
        if (Build.VERSION.SDK_INT >= 21)
            tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null, null);
        else tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void showLanguageChangeDialog() {
        final String[] listItems = {"Abstract", "Blue", "Purple", "Yellow", "Show tutorials next run"};
        AlertDialog.Builder mb = new AlertDialog.Builder(MainActivity.this);
        mb.setTitle(getString(R.string.choose_background));
        mb.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0)
                    constraintlayout.setBackground(getResources().getDrawable(R.drawable.bgr_abstract));

                if (i == 1)
                    constraintlayout.setBackground(getResources().getDrawable(R.drawable.bgr_blue));

                if (i == 2)
                    constraintlayout.setBackground(getResources().getDrawable(R.drawable.bgr_purple));

                if (i == 3)
                    constraintlayout.setBackground(getResources().getDrawable(R.drawable.bgr_yellow));

                if (i == 4) {
                    editor = prefs.edit();
                    editor.putBoolean("RunBefore", false);
                    editor.apply();
                }

                dialogInterface.dismiss();
            }
        });

        AlertDialog md = mb.create();
        md.show();
    }

    private void showTutorials() {
        ShowcaseConfig config = new ShowcaseConfig();
        final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(MainActivity.this);
        config.setMaskColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
        config.setRenderOverNavigationBar(true);
        config.setShapePadding(50);
        config.setDelay(500);
        sequence.setConfig(config);

        ivSettings.post(new Runnable() {
            @Override
            public void run() {
                sequence.addSequenceItem(ivSettings, "Click Here for settings menu.", "Next");
                sequence.addSequenceItem(etUserText, "Type here instead of using your voice.", "Next");
                sequence.addSequenceItem(addBtn, "Click and Start talking after the beep.\nOr just click if you typed something.", "Next");
                sequence.start();
            }
        });
    }


/*

   private void showTutorial() {
              new MaterialShowcaseView.Builder(this)
                .setTarget(ivSettings)
                .setDismissText("GOT IT")
                .setDelay(500)
                .setShapePadding(0)
                .setMaskColour(ContextCompat.getColor(this, R.color.colorPrimary))
                .setDismissTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                .setContentText("Swipe up or tap the bottom bar to view more information.")
                .show();
       new MaterialShowcaseView.Builder(this)
               .setTarget(editText)
               .setDismissText("GOT IT")
               .setDelay(500)
               .setShapePadding(0)
               .setMaskColour(ContextCompat.getColor(this, R.color.colorPrimary))
               .setDismissTextColor(ContextCompat.getColor(this, R.color.colorWhite))
               .setContentText("Swipe up or tap the bottom bar to view more information.")
               .show();
       new MaterialShowcaseView.Builder(this)
               .setTarget(addBtn)
               .setDismissText("GOT IT")
               .setDelay(500)
               .setShapePadding(0)
               .setMaskColour(ContextCompat.getColor(this, R.color.colorPrimary))
               .setDismissTextColor(ContextCompat.getColor(this, R.color.colorWhite))
               .setContentText("Swipe up or tap the bottom bar to view more information.")
               .show();

    }
*/

    @Override
    public void onError(ai.api.model.AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    private void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }
}
