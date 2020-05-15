package com.example.keyboardapp;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {

    private String HOST = "192.168.0.11";
    private int PORT = 8000;
    private Connection mConnect = null;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    private void configure(String[] data) {
        float x=-1;
        float y=-1;
        try {
            Path clickPath = new Path();
            float startX = Float.parseFloat(data[0].trim());
            float startY = Float.parseFloat(data[1].trim());
            clickPath.moveTo(startX, startY);
            for (int i = 2; i < data.length; i++) {
                x = Float.parseFloat(data[i].trim());
                Log.d("AAA",x+" ");
                y = Float.parseFloat(data[i+1].trim());
                Log.d("AAA",y+" ");
                clickPath.lineTo(x, y);
                i++;
                Log.d("OK",data.length+" ");
            }
            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(clickPath, 0, data.length*3));
            dispatchGesture(gestureBuilder.build(), null, null);

            Log.d("NY", "NARISOVALI");
        }catch (Exception ex)
        {
            Log.d("NOO","NET");
            Log.d("NOO",ex.getMessage());
            Log.d("NOO",x+" "+y+" ");
        }
    }

    @Override
    protected void onServiceConnected() {

        mConnect = new Connection(HOST, PORT);
        // Открытие сокета в отдельном потоке
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mConnect.openConnection();


                    Log.d(Connection.LOG_TAG, "Соединение установлено");
                    Log.d(Connection.LOG_TAG, "(mConnect != null) = " + (mConnect != null));
                } catch (Exception e) {
                    Log.e(Connection.LOG_TAG, "Соединение НЕ установлено");
                    mConnect = null;
                }

                while (true)
                {
                    Log.d(Connection.LOG_TAG, "Попытка1");
                    assert mConnect != null;
                    Log.d(Connection.LOG_TAG, "Попытка2");
                    mConnect.receiveData();
                    configure(Connection.data);
                    Log.d(Connection.LOG_TAG, "Попытка3");
                    Log.d(Connection.LOG_TAG, "Попытка4");
                }
            }
        }).start();

    }
}
