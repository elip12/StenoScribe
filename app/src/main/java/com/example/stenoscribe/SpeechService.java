package com.example.stenoscribe;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechService extends Service implements RecognitionListener {
    private SpeechRecognizer speech = null;
    private Intent intent;
    private final String TAG = "SPEECHSERVICE";
    public MyBinder mBinder = new MyBinder();
    public String returnedText = "";
    private final String CHANNEL_ID = "channel_recording";
    private final int NOTIFICATION_ID = 0;
    NotificationManagerCompat notificationManager;

    // creates notification showing currently recording
    private void createNotification(String text) {

        // Create notification with various properties
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        // Get compatibility NotificationManager
        notificationManager = NotificationManagerCompat.from(this);

        // Post notification using ID.  If same ID, this notification replaces previous one
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    // create notification channel for starting and stopping notification
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }

        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        // Register channel with system
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    // start listening for audio
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
        createNotificationChannel();
        createNotification("Recording Audio");

        return START_STICKY;
    }

    // used to share the recording transcription
    @Override
    public IBinder onBind(Intent i) {
        //Log.d(TAG, "Service binding");
        return mBinder;
    }

    // activity tells service to stop listening, kill everything so that it doesnt give a recognizer
    // busy error when restarting
    public void stopListening() {
        speech.stopListening();
        speech.destroy();
        speech = null;
        notificationManager.cancel(NOTIFICATION_ID);
    }

    // a bunch of methods that need to be overridden since this class implements RecognitionListener
    @Override
    public void onBeginningOfSpeech() {
        //Log.d(TAG, "onBeginningOfSpeech");
    }
    @Override
    public void onBufferReceived(byte[] buffer) {
        //Log.d(TAG, "onBufferReceived: " + buffer);
    }
    @Override
    public void onEndOfSpeech() {
        //Log.d(TAG, "onEndOfSpeech");
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
                message = "Server error";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        Log.w(TAG, message);
        speech.startListening(intent);
    }
    @Override
    public void onEvent(int arg0, Bundle arg1) {
        //Log.d(TAG, "onEvent");
    }
    @Override
    public void onPartialResults(Bundle arg0) {
        //Log.d(TAG, "onPartialResults");
    }
    @Override
    public void onReadyForSpeech(Bundle arg0) {
        //Log.d(TAG, "onReadyForSpeech");
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
        //Log.idTAG, "onRmsChanged: " + rmsdB);
    }

    public class MyBinder extends Binder {
        public SpeechService getService() {
            return SpeechService.this;
        }
    }
}
