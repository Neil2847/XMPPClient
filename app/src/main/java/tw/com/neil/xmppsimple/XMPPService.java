package tw.com.neil.xmppsimple;

import android.os.Message;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class XMPPService {

    // login account & password
    private final String USER_NAME = "";
    private final String PASSWORD = "";
    private static final String TAG = "XMPPService";
    // you want to send message user: id@host
    private final String USER_JID = "";

    public final static String HOST = "";
    private static final String SERVICE_NAME = "";
    // default port
    public final static int PORT = 5222;
    public static AbstractXMPPConnection mXMPPTCPConnection;

    private static SSLContext mSSLContext;
    private MainActivity.XMPPHandler mXMPPHandler;

    // -------------------------------------------------------
    public XMPPService(MainActivity.XMPPHandler mXMPPHandler) {
        this.mXMPPHandler = mXMPPHandler;
    }

    // -------------------------------------------------------
    public AbstractXMPPConnection initXMPPTCPConnection() {
        SmackConfiguration.DEBUG = true;
        try {
            mSSLContext = SSLContext.getInstance("TLS");
            mSSLContext.init(null, new TrustManager[]{new MyTrustManager()}, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("connecting failed", e);
        }
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();

        builder.setHost(HOST);
        builder.setServiceName(SERVICE_NAME);
        builder.setPort(PORT);
        builder.setCompressionEnabled(false);

        // 安全模式
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        // 不驗證憑證
        builder.setCustomSSLContext(mSSLContext);
        SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
        SASLAuthentication.unBlacklistSASLMechanism("DIGEST-MD5");
        mXMPPTCPConnection = new XMPPTCPConnection(builder.build());
        return mXMPPTCPConnection;
    }

    // -------------------------------------------------------
    public void connect() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message message = new Message();
                    message.what = AppConstants.CONNECT;
                    if (mXMPPTCPConnection.isConnected()) {
                        message.obj = "connect successful";
                    } else {
                        mXMPPTCPConnection.connect();
                        if (mXMPPTCPConnection.isConnected()) {
                            message.obj = "connect successful";
                        } else {
                            message.obj = "connect failed";
                        }
                    }
                    mXMPPHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "connect e=" + e.getMessage());
                }
            }
        }
        );
        thread.start();
    }

    // -------------------------------------------------------
    public void login() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String loginMsg;
                Message message = new Message();
                message.what = AppConstants.LOGIN;
                try {
                    if (!mXMPPTCPConnection.isConnected())
                        mXMPPTCPConnection.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                    mXMPPTCPConnection.disconnect();
                }

                if (mXMPPTCPConnection.isConnected()) {
                    try {
                        mXMPPTCPConnection.login(USER_NAME, PASSWORD);
                        if (mXMPPTCPConnection.isAuthenticated()) {
                            loginMsg = "sign in successful";
                            receiveMessage();
                        } else {
                            loginMsg = "sign in failed";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        loginMsg = "sign in exception = " + e.getMessage();
                    }
                } else {
                    loginMsg = "connect failed";
                }
                message.obj = loginMsg;
                mXMPPHandler.sendMessage(message);
            }
        }).start();
    }

    // -------------------------------------------------------
    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = AppConstants.DISCONNECT;
                mXMPPTCPConnection.disconnect();
                mXMPPHandler.sendMessage(msg);
            }
        }).start();
    }

    // -------------------------------------------------------
    public void receiveMessage() {

        mXMPPTCPConnection.addAsyncStanzaListener(new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) {

                Message msg = new Message();
                msg.what = AppConstants.MESSAGE;
                if (packet instanceof org.jivesoftware.smack.packet.Message) {
                    msg.obj = ((org.jivesoftware.smack.packet.Message) packet).getBody();
                    mXMPPHandler.sendMessage(msg);
                }
            }
        }, new StanzaFilter() {
            @Override
            public boolean accept(Stanza stanza) {
                if (stanza instanceof org.jivesoftware.smack.packet.Message) {
                    if (((org.jivesoftware.smack.packet.Message) stanza).getBody() != null) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    // -------------------------------------------------------
    public void sendMessage(String message) throws SmackException.NotConnectedException {
        org.jivesoftware.smack.packet.Message msg = new org.jivesoftware.smack.packet.Message();
        msg.setBody(message);
        msg.setType(org.jivesoftware.smack.packet.Message.Type.chat);
        ChatManager.getInstanceFor(mXMPPTCPConnection).createChat(USER_JID).sendMessage(msg);
    }

    // -------------------------------------------------------
}
