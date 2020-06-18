package com.example.a0602;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //180秒=3分で設定
    private static final long START_TIME = 180000;
    private  TextView mTextViewCountDown;
    private  Button mButtonStartPause;
    private  Button getmButtonReset;

    private  CountDownTimer mCountDownTimer;
    private  boolean mTimerRunning;

    private long mTimeLeftInMillis = START_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        mButtonStartPause = findViewById(R.id.buttonstartpause);
        getmButtonReset = findViewById(R.id.buttonreset);

        mButtonStartPause.setOnClickListener(new View.OnClickListener(){
            //スタートボタン押下処理
            @Override
            public void onClick(View v) {
                //作動中はpauseTimerへ、停止または初期状態はstartTimerへ
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        getmButtonReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                resetTimer();
            }
        });

        updateCountDownText();
    }

    //タイマー作動処理
    private void startTimer(){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis,1000) {
            //タイマーカウントダウン処理
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            //タイマーが00:00になったら
            @Override
            public void onFinish() {
                normalNotification();
                mTimerRunning = false;
                mTimeLeftInMillis = START_TIME;
                updateCountDownText();
                mButtonStartPause.setText("スタート");
                getmButtonReset.setVisibility(View.INVISIBLE);
            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setText("一時停止");
        getmButtonReset.setVisibility(View.INVISIBLE);
    }

    //一時停止ボタン押下処理
    private void pauseTimer(){
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("スタート");
        getmButtonReset.setVisibility(View.VISIBLE);
    }

    //リセットボタン押下処理
    private void resetTimer(){
        mTimeLeftInMillis = START_TIME;
        updateCountDownText();
        mButtonStartPause.setVisibility(View.VISIBLE);
        getmButtonReset.setVisibility(View.INVISIBLE);
    }

    private void updateCountDownText(){
        //画面表示用の時間計算
        int minutes = (int)(mTimeLeftInMillis/1000)/60;
        int seconds = (int)(mTimeLeftInMillis/1000)%60;
        String timerLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timerLeftFormatted);
    }


    // 以下通知発行用処理を分離
    private void normalNotification(){
        Notification notification = null;
        // システムから通知マネージャー取得
        final NotificationManager notificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        // アプリ名をチャンネルIDとして利用
        String chID = getString(R.string.app_name);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {     //APIが「26」以上の場合
            //通知チャンネルIDを生成してインスタンス化
            NotificationChannel notificationChannel = new NotificationChannel(chID, chID, NotificationManager.IMPORTANCE_DEFAULT);
            //通知の説明のセット
            notificationChannel.setDescription(chID);
            //通知チャンネルの作成
            notificationManager.createNotificationChannel(notificationChannel);
            //通知の生成と設定とビルド
            notification = new Notification.Builder(this, chID)
                    .setContentTitle("タイマーからのお知らせ")  //通知タイトル
                    .setContentText("時間が経過しました")        //通知内容
                    .setSmallIcon(R.drawable.icon)                  //通知用アイコン
                    .build();                                       //通知のビルド

        }
        notificationManager.notify(1, notification);
    }
}
