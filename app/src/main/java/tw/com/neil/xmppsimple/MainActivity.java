package tw.com.neil.xmppsimple;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jivesoftware.smack.SmackException;

public class MainActivity extends AppCompatActivity implements XMPPClickListener, View.OnClickListener {

    private XMPPService mXMPPService;
    private XMPPHandler mXMPPHandler = new XMPPHandler();
    private ProgressDialog mProgressDialog;
    private TextView msg;
    private Button btn;
    private EditText msgBox;
    private LinearLayout msgLayout;
    private static boolean isConnect = false;

    // -------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    // -------------------------------------------------------
    private void init() {
        mXMPPService = new XMPPService(mXMPPHandler);
        mXMPPHandler.setXMPPClickListener(this);
        mXMPPService.initXMPPTCPConnection();
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("loading..");
        findViewById(R.id.connect).setOnClickListener(this);
        findViewById(R.id.send).setOnClickListener(this);
        msg = findViewById(R.id.msg);
        btn = findViewById(R.id.connect);
        msgBox = findViewById(R.id.message_box);
        msgLayout = findViewById(R.id.message_layout);
    }

    // -------------------------------------------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect:
                progressDialogShow();
                if (isConnect) {
                    mXMPPService.disconnect();
                } else {
                    mXMPPService.login();
                }
                break;
            case R.id.send:
                try {
                    mXMPPService.sendMessage(msgBox.getText().toString());
                    msgBox.setText("");
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    // -------------------------------------------------------
    @Override
    public void connect(String msg) {
        toastShow(msg);
    }

    @Override
    public void login(String msg) {
        toastShow(msg);
        msgLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void message(String msg) {
        toastShow(msg);
    }

    // -------------------------------------------------------
    private void toastShow(CharSequence text) {
        progressDialogDismiss();
        msg.setText(text);
    }

    // -------------------------------------------------------
    private void progressDialogShow() {
        mProgressDialog.show();
    }

    private void progressDialogDismiss() {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    // -------------------------------------------------------
    class XMPPHandler extends Handler {
        private XMPPClickListener mXMPPClickListener;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String text = (String) msg.obj;
            switch (msg.what) {
                case AppConstants.CONNECT:
                    mXMPPClickListener.connect(text);
                    break;
                case AppConstants.LOGIN:
                    mXMPPClickListener.login(text);
                    isConnect = true;
                    btn.setText("DISCONNECT");
                    break;
                case AppConstants.REGISTER:
                    break;
                case AppConstants.MESSAGE:
                    mXMPPClickListener.message(text);
                    break;
                case AppConstants.DISCONNECT:
                    isConnect = false;
                    progressDialogDismiss();
                    btn.setText("CONNECT");
                    msgLayout.setVisibility(View.INVISIBLE);
                    break;
            }
        }

        void setXMPPClickListener(XMPPClickListener xmppClickListener) {
            this.mXMPPClickListener = xmppClickListener;
        }
    }

    // -------------------------------------------------------
}
