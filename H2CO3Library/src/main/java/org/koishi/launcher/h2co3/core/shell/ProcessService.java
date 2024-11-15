package org.koishi.launcher.h2co3.core.shell;

import android.app.Service;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3.core.H2CO3Settings;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.launch.H2CO3BaseLaunch;
import org.koishi.launcher.h2co3.core.launch.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.core.launch.H2CO3LauncherBridgeCallback;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class ProcessService extends Service {

    public static final int PROCESS_SERVICE_PORT = 29118;
    private boolean firstLog = true;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String[] command = intent.getExtras().getStringArray("command");
        int java = intent.getExtras().getInt("java");
        String jre = "jre" + java;
        startProcess(command, new H2CO3Settings(), jre);
        return super.onStartCommand(intent, flags, startId);
    }

    public void startProcess(String[] command, H2CO3Settings gameHelper, String jre) {
        H2CO3LauncherBridge bridge = H2CO3BaseLaunch.launchAPIInstaller(H2CO3Tools.CONTEXT, gameHelper, command, jre);
        File logFile = new File(bridge.getLogPath());
        H2CO3LauncherBridgeCallback callback = new H2CO3LauncherBridgeCallback() {
            /**
             */
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

            }

            /**
             */
            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public void onCursorModeChange(int mode) {
                // Ignore
            }

            @Override
            public void onHitResultTypeChange(int type) {
                // Ignore
            }

            @Override
            public void onLog(String log) {
                try {
                    if (firstLog) {
                        FileTools.writeText(logFile, log + "\n");
                        firstLog = false;
                    } else {
                        FileTools.writeTextWithAppendMode(logFile, log + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /**
             *
             */
            @Override
            public void onStart() {

            }

            /**
             *
             */
            @Override
            public void onPicOutput() {

            }

            /**
             */
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onExit(int code) {
                sendCode(code);
            }
        };
        Handler handler = new Handler();
        handler.post(() -> {
            try {
                bridge.execute(null, callback);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void sendCode(int code) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", PROCESS_SERVICE_PORT));
            byte[] data = (code + "").getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}