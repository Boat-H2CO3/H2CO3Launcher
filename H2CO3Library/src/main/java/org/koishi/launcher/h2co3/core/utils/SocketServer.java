package org.koishi.launcher.h2co3.core.utils;

import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3.core.utils.task.Schedulers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketServer {

    private static final Logger LOG = Logger.getLogger(SocketServer.class.getName());

    private final Listener listener;
    private final String ip;
    private final int port;
    private DatagramPacket packet;
    private DatagramSocket socket;
    private boolean isReceiving = false;
    private Object result;
    private final ExecutorService executorService;

    public SocketServer(String ip, int port, Listener listener) {
        this.listener = listener;
        this.ip = ip;
        this.port = port;
        executorService = Executors.newSingleThreadExecutor();
        Schedulers.androidUIThread().execute(() -> {
            byte[] bytes = new byte[1024];
            packet = new DatagramPacket(bytes, bytes.length);
            try {
                socket = new DatagramSocket(port, InetAddress.getByName(ip));
                LOG.log(Level.INFO, "Socket server init!");
            } catch (SocketException | UnknownHostException e) {
                LOG.log(Level.WARNING, "Failed to init socket server", e);
            }
        });
    }

    public DatagramPacket getPacket() {
        return packet;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public Listener getListener() {
        return listener;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void start() {
        Schedulers.androidUIThread().execute(() -> {
            if (packet == null || socket == null) {
                return;
            }
            LOG.log(Level.INFO, "Socket server " + ip + ":" + port + " start!");
            isReceiving = true;
            executorService.execute(() -> {
                while (isReceiving) {
                    try {
                        socket.receive(packet);
                        String receiveMsg = new String(packet.getData(), 0, packet.getLength());
                        listener.onReceive(this, receiveMsg);
                    } catch (IOException e) {
                        H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
                        LOG.log(Level.INFO, "Socket server " + ip + ":" + port + " start!");
                    }
                }
            });
        });
    }

    public void send(String msg) throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(new InetSocketAddress(ip, port));
            byte[] data = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket.send(packet);
        }
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void stop() {
        isReceiving = false;
        socket.close();
        LOG.log(Level.INFO, "Socket server " + ip + ":" + port + " stopped!");
        executorService.shutdown();
    }

    public interface Listener {
        void onReceive(SocketServer server, String msg);
    }

}