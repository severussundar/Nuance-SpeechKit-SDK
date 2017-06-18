package com.nuance.speechkitsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends Activity implements View.OnClickListener {

    View asrButton = null;
    View nluButton = null;
    View textNluButton = null;
    View ttsButton = null;

    View audioButton = null;

    View configButton = null;
    View aboutButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout mainContent = (LinearLayout) findViewById(R.id.main_content);
        asrButton = inflateRowView("Speech to text","", mainContent);
        ttsButton = inflateRowView("Text to Speech", "", mainContent);

    }

    @Override
    public void onClick(View v) {

        Intent intent = null;
        if(v == asrButton) {
            intent = new Intent(this, ASRActivity.class);
        } else if(v == ttsButton) {
            intent = new Intent(this, TTSActivity.class);
        }

        if(intent != null) {
            startActivity(intent);
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        }
    }

    private LinearLayout inflateCategoryView(String title, LinearLayout parent) {
        View v = (View) getLayoutInflater().inflate(R.layout.activity_main_category, null);
        ((TextView)v.findViewById(R.id.title)).setText(title);
        parent.addView(v);
        return ((LinearLayout)v.findViewById(R.id.list));
    }

    private View inflateRowView(String mainText, String subText, LinearLayout parent) {
        View v = (View) getLayoutInflater().inflate(R.layout.activity_main_row, null);
        ((TextView)v.findViewById(R.id.mainText)).setText(mainText);
        ((TextView)v.findViewById(R.id.subText)).setText(subText);
        parent.addView(v);
        v.setOnClickListener(this);
        return v;
    }
}
