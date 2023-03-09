package com.cyberesSCM.karatechjudge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity  {

    //private Button butOne;
    public TextView score;
    int REDScore = 0;
    int WHITEScore = 0;
    int port = 0;
    Button REDBut;
    Button WHITEBut;
    Button reset;
    String IP_SERVER = "192.168.1.100";
    String IP_MAX_DEBUG = "192.168.1.8";
    String ip = IP_MAX_DEBUG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        port = intent.getIntExtra("port", 0);
        ip = intent.getStringExtra("ip");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        score = findViewById(R.id.textScore);
        REDBut = findViewById(R.id.REDBut);
        WHITEBut = findViewById(R.id.WHITEBut);
        reset = findViewById(R.id.reset);
    }




    @SuppressLint("SetTextI18n")
    public void REDBut(View view){
        Thread thRED = new Thread(()->{
            try {
                Socket s = new Socket();
                s.connect(new InetSocketAddress(ip, port));
                PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                pw.println("red:" + REDScore + "," + WHITEScore);
                pw.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thRED.start();
        REDScore++;
        score.setText(WHITEScore + ":" + REDScore);
        vibrate();
    }

    @SuppressLint("SetTextI18n")
    public void WHITEBut(View view){
        Thread thWHITE = new Thread(()->{
            try {
                Socket s = new Socket();
                s.connect(new InetSocketAddress(ip, port));
                PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                pw.println("white:" + REDScore + "," + WHITEScore);
                pw.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thWHITE.start();
        WHITEScore++;
        score.setText(WHITEScore + ":" + REDScore);
        vibrate();
    }



    public void vibrate(){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(50);
        }
    }


    @Override
    public void onBackPressed() {
        Thread th = new Thread(() -> {
            try {
                Socket s = new Socket(ip, 25800);
                PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                pw.println("123456NoPass:" + port);
                s.close();
                pw.close();

            } catch (IOException ignored) {
            }
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        super.onBackPressed();
    }

    public void reset(View view) {
        @SuppressLint("SetTextI18n") Thread reset = new Thread(()->{
            try {
                Socket s = new Socket();
                s.connect(new InetSocketAddress(ip, 25800));
                PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                Scanner scan = new Scanner(s.getInputStream());
                pw.println("isEnd:" + port);
                if(scan.hasNextLine()) {
                    if (scan.nextLine().equals("yep")) {
                        REDScore = 0;
                        WHITEScore = 0;
                        score.setText(0 + ":" + 0);
                        Socket ss = new Socket();
                        ss.connect(new InetSocketAddress(ip, port));
                        PrintWriter pww = new PrintWriter(ss.getOutputStream(), true);
                        pww.println("white:" + REDScore + "," + WHITEScore);
                        pww.close();
                        ss.close();
                    }
                }
                pw.close();
                s.close();
            } catch (IOException e) {
                REDScore = 0;
                WHITEScore = 0;
            }
        });
        reset.start();
        vibrate();
    }
}