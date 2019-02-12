package tw.com.neil.xmppsimple;

public interface XMPPClickListener {
    void connect(String msg);

    void login(String msg);

    void message(String msg);
}
