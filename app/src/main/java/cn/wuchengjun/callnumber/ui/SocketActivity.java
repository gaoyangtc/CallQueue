package cn.wuchengjun.callnumber.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import cn.wuchengjun.callnumber.R;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketActivity extends AppCompatActivity {

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);

        try {
            mSocket = IO.socket("http://47.93.249.4:2120");

//            mSocket.on(Socket.EVENT_CONNECT, onConnect);
//            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
//            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
//            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on("connect", onConnect);
            mSocket.on("new_msg", onNewMessage);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

//    private Emitter.Listener onConnect = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (!isConnected) {
//                        if (null != mUsername)
//                            mSocket.emit("add user", mUsername);
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                R.string.connect, Toast.LENGTH_LONG).show();
//                        isConnected = true;
//                    }
//                }
//            });
//        }
//    };
//
//    private Emitter.Listener onDisconnect = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.i(TAG, "diconnected");
//                    isConnected = false;
//                    Toast.makeText(getActivity().getApplicationContext(),
//                            R.string.disconnect, Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//    };
//
//    private Emitter.Listener onConnectError = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e(TAG, "Error connecting");
//                    Toast.makeText(getActivity().getApplicationContext(),
//                            R.string.error_connect, Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    JSONObject data = (JSONObject) args;
                    Toast.makeText(SocketActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
//                    String username;
//                    String message;
//                    try {
//                        username = data.getString("username");
//                        message = data.getString("message");
//                    } catch (JSONException e) {
//                        Log.e(TAG, e.getMessage());
//                        return;
//                    }
//
//                    removeTyping(username);
//                    addMessage(username, message);
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = (String) args[0];
//                    Toast.makeText(SocketActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
//                    String username;
//                    String message;
//                    try {
//                        username = data.getString("username");
//                        message = data.getString("message");
//                    } catch (JSONException e) {
//                        Log.e(TAG, e.getMessage());
//                        return;
//                    }
//
//                    removeTyping(username);
//                    addMessage(username, message);
                }
            });
        }
    };
}
