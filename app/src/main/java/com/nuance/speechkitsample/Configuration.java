package com.nuance.speechkitsample;

import android.net.Uri;

import com.nuance.speechkit.PcmFormat;


public class Configuration {


    public static final String APP_KEY = "3be1b57d7e874f5c8d8328aafd43c5fb60250961f00264b742ce5de16de671a8c7262645ad75a23af8cae5f50eaaf06ecff9cfbffb33763d01d31e6eb9901780";
    public static final String APP_ID = "NMDPPRODUCTION_Shyam_Sundar_J_nuance_test_20170611052625";
    public static final String SERVER_HOST = "lcj.nmdp.nuancemobility.net";
    public static final String SERVER_PORT = "443";

    public static final String LANGUAGE = "!LANGUAGE!";

    public static final Uri SERVER_URI = Uri.parse("nmsps://" + APP_ID + "@" + SERVER_HOST + ":" + SERVER_PORT);

    public static final PcmFormat PCM_FORMAT = new PcmFormat(PcmFormat.SampleFormat.SignedLinear16, 16000, 1);
    public static final String LANGUAGE_CODE = (Configuration.LANGUAGE.contains("!") ? "eng-USA" : Configuration.LANGUAGE);

}



