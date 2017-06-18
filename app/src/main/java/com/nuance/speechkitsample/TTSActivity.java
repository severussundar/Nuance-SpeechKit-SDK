package com.nuance.speechkitsample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nuance.speechkit.Audio;
import com.nuance.speechkit.AudioPlayer;
import com.nuance.speechkit.Language;
import com.nuance.speechkit.Session;
import com.nuance.speechkit.Transaction;
import com.nuance.speechkit.TransactionException;

public class TTSActivity extends DetailActivity implements View.OnClickListener, AudioPlayer.Listener {

    private EditText ttsText;
    private EditText language;

    private TextView logs;
    private Button clearLogs;

    private Button toggleTTS;

    private Session speechSession;
    private Transaction ttsTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);

        ttsText = (EditText)findViewById(R.id.tts_text);
        language = (EditText)findViewById(R.id.language);
        language.setText(Configuration.LANGUAGE_CODE);

        logs = (TextView)findViewById(R.id.logs);
        clearLogs = (Button)findViewById(R.id.clear_logs);
        clearLogs.setOnClickListener(this);

        toggleTTS = (Button)findViewById(R.id.toggle_tts);
        toggleTTS.setOnClickListener(this);

        speechSession = Session.Factory.session(this, Configuration.SERVER_URI, Configuration.APP_KEY);
        speechSession.getAudioPlayer().setListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == clearLogs) {
            logs.setText("");
        } else if(v == toggleTTS) {
            toggleTTS();
        }
    }


    private void toggleTTS() {
        //If we are not loading TTS from the server, then we should do so.
        if(ttsTransaction == null) {
            toggleTTS.setText(getResources().getString(R.string.cancel));
            synthesize();
        }
        //Otherwise lets attempt to cancel that transaction
        else {
            toggleTTS.setText(getResources().getString(R.string.speak_string));
            stop();
        }
    }

    private void synthesize() {

        Transaction.Options options = new Transaction.Options();
        options.setLanguage(new Language(language.getText().toString()));
        ttsTransaction = speechSession.speakString(ttsText.getText().toString(), options, new Transaction.Listener() {
            @Override
            public void onAudio(Transaction transaction, Audio audio) {
                logs.append("\n onAudio");
                ttsTransaction = null;
                toggleTTS.setText(getResources().getString(R.string.speak_string));
            }

            @Override
            public void onSuccess(Transaction transaction, String s) {
                logs.append("\n onSuccess");
            }

            @Override
            public void onError(Transaction transaction, String s, TransactionException e) {
                logs.append("\n onError: " + e.getMessage() + ". " + s);
                ttsTransaction = null;
            }
        });
    }

    private void stop() {
        ttsTransaction.cancel();
    }

    @Override
    public void onBeginPlaying(AudioPlayer audioPlayer, Audio audio) {
        logs.append("\n onBeginPlaying");

    }

    @Override
    public void onFinishedPlaying(AudioPlayer audioPlayer, Audio audio) {
        logs.append("\n onFinishedPlaying");
    }
}
