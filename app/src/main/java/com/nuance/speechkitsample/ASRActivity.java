package com.nuance.speechkitsample;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.nuance.speechkit.Audio;
import com.nuance.speechkit.DetectionType;
import com.nuance.speechkit.Language;
import com.nuance.speechkit.Recognition;
import com.nuance.speechkit.ResultDeliveryType;
import com.nuance.speechkit.RecognitionType;
import com.nuance.speechkit.Session;
import com.nuance.speechkit.Transaction;
import com.nuance.speechkit.TransactionException;

public class ASRActivity extends DetailActivity implements View.OnClickListener {

    private Audio startEarcon;
    private Audio stopEarcon;
    private Audio errorEarcon;

    private RadioGroup recoType;
    private RadioGroup detectionType;
    private ToggleButton progressiveResuts;
    private EditText language;

    private TextView logs;
    private Button clearLogs;

    private Button toggleReco;

    private ProgressBar volumeBar;

    private Session speechSession;
    private Transaction recoTransaction;
    private State state = State.IDLE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asr);

        recoType = (RadioGroup)findViewById(R.id.reco_type_picker);
        detectionType = (RadioGroup)findViewById(R.id.detection_type_picker);
        progressiveResuts = (ToggleButton)findViewById(R.id.progressive_results_toggle);
        language = (EditText)findViewById(R.id.language);
        language.setText(Configuration.LANGUAGE_CODE);

        logs = (TextView)findViewById(R.id.logs);
        clearLogs = (Button)findViewById(R.id.clear_logs);
        clearLogs.setOnClickListener(this);

        toggleReco = (Button)findViewById(R.id.toggle_reco);
        toggleReco.setOnClickListener(this);

        volumeBar = (ProgressBar)findViewById(R.id.volume_bar);

        //Create a session
        speechSession = Session.Factory.session(this, Configuration.SERVER_URI, Configuration.APP_KEY);

        loadEarcons();

        setState(State.IDLE);
    }

    @Override
    public void onClick(View v) {
        if(v == clearLogs) {
            logs.setText("");
        } else if(v == toggleReco) {
            toggleReco();
        }
    }

    /* Reco transactions */

    private void toggleReco() {
        switch (state) {
            case IDLE:
                recognize();
                break;
            case LISTENING:
                stopRecording();
                break;
            case PROCESSING:
                cancel();
                break;
        }
    }


    private void recognize() {

        Transaction.Options options = new Transaction.Options();
        options.setRecognitionType(resourceIDToRecoType(recoType.getCheckedRadioButtonId()));
        options.setDetection(resourceIDToDetectionType(detectionType.getCheckedRadioButtonId()));
        options.setLanguage(new Language(language.getText().toString()));
        options.setEarcons(startEarcon, stopEarcon, errorEarcon, null);

        if(progressiveResuts.isChecked()) {
            options.setResultDeliveryType(ResultDeliveryType.PROGRESSIVE);
        }

        recoTransaction = speechSession.recognize(options, recoListener);
    }

    private Transaction.Listener recoListener = new Transaction.Listener() {
        @Override
        public void onStartedRecording(Transaction transaction) {
            logs.append("\n onStartedRecording");
            setState(State.LISTENING);
            startAudioLevelPoll();
        }

        @Override
        public void onFinishedRecording(Transaction transaction) {
            logs.append("\n onFinishedRecording");
            setState(State.PROCESSING);
            stopAudioLevelPoll();
        }

        @Override
        public void onRecognition(Transaction transaction, Recognition recognition) {
            logs.append("\n onRecognition: " + recognition.getText());
        }

        @Override
        public void onSuccess(Transaction transaction, String s) {
            logs.append("\n onSuccess");
            setState(State.IDLE);
        }

        @Override
        public void onError(Transaction transaction, String s, TransactionException e) {
            logs.append("\n onError: " + e.getMessage() + ". " + s);
            setState(State.IDLE);
        }
    };

    private void stopRecording() {
        recoTransaction.stopRecording();
    }

    private void cancel() {
        recoTransaction.cancel();
        setState(State.IDLE);
    }

    private Handler handler = new Handler();

    private Runnable audioPoller = new Runnable() {
        @Override
        public void run() {
            float level = recoTransaction.getAudioLevel();
            volumeBar.setProgress((int)level);
            handler.postDelayed(audioPoller, 50);
        }
    };

    private void startAudioLevelPoll() {
        audioPoller.run();
    }

    private void stopAudioLevelPoll() {
        handler.removeCallbacks(audioPoller);
        volumeBar.setProgress(0);
    }


    private enum State {
        IDLE,
        LISTENING,
        PROCESSING
    }

    private void setState(State newState) {
        state = newState;
        switch (newState) {
            case IDLE:
                toggleReco.setText(getResources().getString(R.string.recognize));
                break;
            case LISTENING:
                toggleReco.setText(getResources().getString(R.string.listening));
                break;
            case PROCESSING:
                toggleReco.setText(getResources().getString(R.string.processing));
                break;
        }
    }

    private void loadEarcons() {

        startEarcon = new Audio(this, R.raw.sk_start, Configuration.PCM_FORMAT);
        stopEarcon = new Audio(this, R.raw.sk_stop, Configuration.PCM_FORMAT);
        errorEarcon = new Audio(this, R.raw.sk_error, Configuration.PCM_FORMAT);
    }

    private RecognitionType resourceIDToRecoType(int id) {
        if(id == R.id.dictation) {
            return RecognitionType.DICTATION;
        }
        if(id == R.id.search) {
            return RecognitionType.SEARCH;
        }
        if(id == R.id.tv) {
            return RecognitionType.TV;
        }
        return null;
    }

    private DetectionType resourceIDToDetectionType(int id) {
        if(id == R.id.long_endpoint) {
            return DetectionType.Long;
        }
        if(id == R.id.short_endpoint) {
            return DetectionType.Short;
        }
        if(id == R.id.none) {
            return DetectionType.None;
        }
        return null;
    }
}
