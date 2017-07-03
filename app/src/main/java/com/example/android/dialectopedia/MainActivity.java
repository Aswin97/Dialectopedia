package com.example.android.dialectopedia;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    //Set context
    Context c = this;
    EditText et;
    ImageButton ib;
    // Activity a=this;
    private TextToSpeech convert;
    TextToSpeech t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b = (Button) findViewById(R.id.button);
        et=(EditText)findViewById(R.id.editText);
        ib=(ImageButton)findViewById(R.id.imageButton4);
        TextView tv = (TextView) findViewById(R.id.textView3);
        //Default variables for translation
        final String languagePair = "en-fr"; //English to French ("<source_language>-<target_language>")

        //Executing the translation function
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TranslatorBackgroundTask tbt = new TranslatorBackgroundTask(c);
                String textToBeTranslated=et.getText().toString();
                tbt.execute(textToBeTranslated, languagePair);// Returns the translated text as a String
                // Logs the result in Android Monitor
            }
        });
    }
    //Text to speech
    public void speak(View view) {
        if(view.getId()==R.id.imageButton)
        {

            promptSpeechInput();
        }
    }

    public void promptSpeechInput(){
        Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
        try {
            startActivityForResult(i, 100);
        }
        catch(ActivityNotFoundException a)
        {
            Toast.makeText(MainActivity.this,"Your device doesnt support",Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int request_code, int result_code, Intent i) {
        super.onActivityResult(request_code, result_code, i);
        convert = new TextToSpeech(this, this);
        switch (request_code) {
            case 100:
                if (result_code == RESULT_OK && i != null) {
                    ArrayList<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    et.setText(result.get(0));
                }
                break;
        }
        //if (result_code == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
        // success, create the TTS instance

        //}
    }

    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //Toast.makeText(MainActivity.this, "Engine is initialized", Toast.LENGTH_LONG).show();
            convert.setLanguage(Locale.ENGLISH);

            /*if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.i("TTS", "This Language is not supported");
            } else {
                //speakOut("Ich");
                Log.i("TTS", "This Language is supported");
            }*/
        }
    }
    //stt gets over

    //text to speech

    //translator
    class TranslatorBackgroundTask extends AsyncTask<String, Void, String> {
        //Declare Context
        Context ctx;
        //TextView tv;
        ;

        //Set Context
        //MainActivity a=new MainActivity();
        public TranslatorBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        //TextView tv
        @Override
        protected String doInBackground(String... params) {
            //String variables

            String textToBeTranslated = params[0];
            String languagePair = params[1];

            String jsonString;

            try {
                //Set up the translation call URL
                String yandexKey = "YOUR_KEY";
                String yandexUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey
                        + "&text=" + textToBeTranslated + "&lang=" + languagePair;
                URL yandexTranslateURL = new URL(yandexUrl);

                //Set Http Connection, Input Stream, and Buffered Reader
                HttpURLConnection httpJsonConnection = (HttpURLConnection) yandexTranslateURL.openConnection();
                InputStream inputStream = httpJsonConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                //Set string builder and insert retrieved JSON result into it
                StringBuilder jsonStringBuilder = new StringBuilder();
                while ((jsonString = bufferedReader.readLine()) != null) {
                    jsonStringBuilder.append(jsonString + "\n");
                }

                //Close and disconnect
                bufferedReader.close();
                inputStream.close();
                httpJsonConnection.disconnect();

                //Making result human readable
                String resultString = jsonStringBuilder.toString().trim();

                //Getting the characters between [ and ]
                resultString = resultString.substring(resultString.indexOf('[') + 1);
                resultString = resultString.substring(0, resultString.indexOf("]"));

                //Getting the characters between " and "
                resultString = resultString.substring(resultString.indexOf("\"") + 1);
                resultString = resultString.substring(0, resultString.indexOf("\""));

                Log.d("Translation Result:", resultString);
                //return jsonStringBuilder.toString().trim();
                return resultString;
                //return resultString;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //setContentView(R.layout.activity_main);
        }

        @Override
        protected void onPostExecute(String result) {
            TextView tv = (TextView)findViewById(R.id.textView3);
            tv.setText(result);

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}




