package cn.rlstech.callnumber.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;



public class TextToSpeechUtils {
    private Context context;

    private static final String TAG = "SpeechUtils";
    private static TextToSpeechUtils singleton;

    private TextToSpeech textToSpeech; // TTS对象

    public static TextToSpeechUtils getInstance(Context context) {
        if (singleton == null) {
            synchronized (TextToSpeechUtils.class) {
                if (singleton == null) {
                    singleton = new TextToSpeechUtils(context);
                }
            }
        }
        return singleton;
    }

    public TextToSpeechUtils(Context context) {
        this.context = context;
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setPitch(1.0f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                    textToSpeech.setSpeechRate(1.0f);
                }
            }
        });
    }

    public void speakText(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text,
                    TextToSpeech.QUEUE_FLUSH, null);
        }

    }
}
