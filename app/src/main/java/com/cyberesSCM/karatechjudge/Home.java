package com.cyberesSCM.karatechjudge;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Home extends AppCompatActivity {

    EditText ip_input;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ip_input = findViewById(R.id.ip_text_input);
        relativeLayout = findViewById(R.id.loadingPanel);
        relativeLayout.setVisibility(View.INVISIBLE);
    }
    public void Join(View view) throws InterruptedException {
        relativeLayout.setVisibility(View.VISIBLE);
        Thread th = new Thread(() -> {
            try {
                int port;
                Socket s = new Socket(ip_input.getText().toString(), 25800);
                System.out.println(ip_input.getText().toString());
                Scanner scan = new Scanner(s.getInputStream());
                PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                pw.println("123456pass");
                String answer = scan.nextLine();
                if (answer.equals("X")){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        getMainExecutor().execute(()->Toast.makeText(this, "Все места судей заняты!", Toast.LENGTH_SHORT).show());
                        getMainExecutor().execute(() -> relativeLayout.setVisibility(View.INVISIBLE));
                    }
                    s.close();
                    scan.close();
                    pw.close();
                    return;
                }
                port = Integer.parseInt(answer);
                Intent myIntent = new Intent(this, MainActivity.class);
                myIntent.putExtra("port", port);
                myIntent.putExtra("ip", ip_input.getText().toString());
                this.startActivity(myIntent);
            } catch (IOException e) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                    getMainExecutor().execute(()->Toast.makeText(this, "Не удалось найти сервер", Toast.LENGTH_SHORT).show());
                }
            } finally {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    getMainExecutor().execute(() -> relativeLayout.setVisibility(View.INVISIBLE));
                }
            }
        });
        th.start();

    }

}