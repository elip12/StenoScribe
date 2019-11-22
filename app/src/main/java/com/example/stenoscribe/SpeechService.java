package com.example.stenoscribe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;

import com.example.stenoscribe.ui.recordings.RecordingsFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechService extends Service implements RecognitionListener {
    private static final int REQUEST_RECORD_PERMISSION = 100;
    private SpeechRecognizer speech = null;
    private Intent intent;
    private final String TAG = "SPEECHSERVICE";
    public MyBinder mBinder = new MyBinder();
    public String returnedText = "";

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        Log.d(TAG, "service starting");
        speech = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speech.setRecognitionListener(this);
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));


        speech.startListening(intent);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent i) {
        Log.d(TAG, "Service binding");
        return mBinder;
    }

    public void stopListening() {
        speech.stopListening();
        speech.destroy();
        speech = null;
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(TAG, "onBeginningOfSpeech");

    }
    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(TAG, "onBufferReceived: " + buffer);
    }
    @Override
    public void onEndOfSpeech() {
        Log.i(TAG, "onEndOfSpeech");

    }
    @Override
    public void onError(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                speech.cancel();
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        Log.e(TAG, message);
        speech.startListening(intent);
    }
    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(TAG, "onEvent");
    }
    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(TAG, "onPartialResults");
    }
    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(TAG, "onReadyForSpeech");
    }
    @Override
    public void onResults(Bundle results) {
        Log.i(TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        returnedText += matches.get(0) + "\n";
        speech.startListening(intent); // this restarts the thing so it records continuously
    }
    @Override
    public void onRmsChanged(float rmsdB) {
        //Log.i(TAG, "onRmsChanged: " + rmsdB);

    }

    public class MyBinder extends Binder {
        public SpeechService getService() {
            return SpeechService.this;
        }
    }
}
