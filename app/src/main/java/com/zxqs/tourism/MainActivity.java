package com.zxqs.tourism;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.speech.VoiceRecognitionService;
import com.baidu.voicerecognition.android.ui.BaiduASRDialog;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.zxqs.tourism.utils.BaiduConstant;
import com.zxqs.tourism.utils.Constant;
import com.zxqs.tourism.utils.DictationResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.baidu.voicerecognition.android.ui.BaiduASRDialog.STATUS_None;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private EditText et_xf;
    private EditText et_bd;
    private Button btn_xf;
    private Button btn_bd;

    private RecognizerDialog xfRecognizerDialog;
    private SpeechRecognizer speechRecognizer;

    private BaiduASRDigitalDialog baiduASRDigitalDialog;

    private static final int REQUEST_UI = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initXF();
        initBaidu();
    }


    private void initBaidu(){
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        speechRecognizer.setRecognitionListener(recognitionListener);

      /*  final Bundle params = new Bundle();
        //设置语音识别对话框为蓝色高亮主题
        params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, BaiduASRDigitalDialog.THEME_BLUE_LIGHTBG);
        baiduASRDigitalDialog = new BaiduASRDigitalDialog();
    */
        btn_bd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("com.baidu.action.RECOGNIZE_SPEECH");
                Bundle params = new Bundle();
                intent.putExtras(params);
                /*intent.putExtra(BaiduConstant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
                intent.putExtra(BaiduConstant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
                intent.putExtra(BaiduConstant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
                intent.putExtra(BaiduConstant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
                intent.putExtra(BaiduConstant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
                intent.putExtra(BaiduConstant.EXTRA_PROP, 10060);*/
                startActivityForResult(intent, REQUEST_UI);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            onResults(data.getExtras());
        }
    }

    private void onResults(Bundle results) {
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        print("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        String json_res = results.getString("origin_result");
        try {
            print("origin_result=\n" + new JSONObject(json_res).toString(4));
        } catch (Exception e) {
            print("origin_result=[warning: bad json]\n" + json_res);
        }
        et_bd.setText(nbest.get(0));
    }


    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {
            StringBuilder sb = new StringBuilder();
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    sb.append("音频问题");
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    sb.append("没有语音输入");
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    sb.append("其它客户端错误");
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    sb.append("权限不足");
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    sb.append("网络问题");
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    sb.append("没有匹配的识别结果");
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    sb.append("引擎忙");
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    sb.append("服务端错误");
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    sb.append("连接超时");
                    break;
            }
            sb.append(":" + error);
            print("识别失败：" + sb.toString());
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String json_res = results.getString("origin_result");
            print(json_res);
            et_bd.setText(nbest.get(0));
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (nbest.size() > 0) {
                et_bd.setText(nbest.get(0));
            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            switch (eventType) {
                case 11:
                    String reason = params.get("reason") + "";
                    print("EVENT_ERROR, " + reason);
                    break;
                case VoiceRecognitionService.EVENT_ENGINE_SWITCH:
                    int type = params.getInt("engine_type");
                    print("*引擎切换至" + (type == 0 ? "在线" : "离线"));
                    break;
            }
        }
    };


    private void initXF(){
        SpeechUtility.createUtility(MainActivity.this, Constant.KEY_XF);
        xfRecognizerDialog = new RecognizerDialog(MainActivity.this,xfInitListener);
        xfRecognizerDialog.setListener(xfRecognizerDialogListener);

        btn_xf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xfRecognizerDialog.show();
            }
        });

    }


    private InitListener xfInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                print("初始化失败，错误码：" + code);
            }
        }
    };


    private RecognizerDialogListener xfRecognizerDialogListener = new RecognizerDialogListener() {
        String resultJson = "[";
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {

            Log.d("XFTAG","resultJson----->"+resultJson);
           /* if (!isLast) {
                resultJson += recognizerResult.getResultString() + ",";
            } else {
                resultJson += recognizerResult.getResultString() + "]";
            }*/
            resultJson += recognizerResult.getResultString();

            if (!isLast) {
                resultJson += ",";
            } else {
                resultJson += "]";
                Log.d("XFTAG",resultJson);
                //解析语音识别后返回的json格式的结果
                Gson gson = new Gson();
                List<DictationResult> resultList = gson.fromJson(resultJson,
                        new TypeToken<List<DictationResult>>() {
                        }.getType());
                String result = "";
                for (int i = 0; i < resultList.size() - 1; i++) {
                    result += resultList.get(i).toString();
                }
                et_xf.setText(result);
              /*  //获取焦点
                et_xf.requestFocus();
                //将光标定位到文字最后，以便修改
                et_xf.setSelection(result.length());*/
                resultJson = "[";
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            //自动生成的方法存根
            speechError.getPlainDescription(true);
        }


    };


    private void initUI(){
        et_xf = (EditText) findViewById(R.id.et_xf);
        et_bd = (EditText) findViewById(R.id.et_bd);
        btn_xf = (Button) findViewById(R.id.btn_xf);
        btn_bd = (Button) findViewById(R.id.btn_bd);
    }

    private void print(String msg){
        Log.d(TAG,msg);
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG);
    }

}
