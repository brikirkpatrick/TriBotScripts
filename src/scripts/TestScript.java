package scripts;

import org.tribot.api.input.Keyboard;
import org.tribot.script.Script;
import org.tribot.script.interfaces.MessageListening07;

public class TestScript extends Script implements MessageListening07 {
    public void run() {
        while (true) {
            sleep(50, 200);
        }
    }

    @Override
    public void tradeRequestReceived(String s) {
    }

    @Override
    public void playerMessageReceived(String s, String s1) {
        sleep(300, 1000);
        if (s.contains("You scratch a notch on your bow for the chompy bird kill.")) {
            Keyboard.typeSend("/loldudeplswork");
            println("player message worked");
        }
    }

    @Override
    public void clanMessageReceived(String s, String s1) {
        sleep(3000, 10000);
        if (s.contains("Hi")) {
            Keyboard.typeSend("/loldudeplswork");
            System.out.println("lolitworked");
        }
    }

    @Override
    public void duelRequestReceived(String s, String s1) {
    }

    @Override
    public void personalMessageReceived(String s, String s1) {
        sleep(300, 1000);
        if (s.contains("You scratch a notch on your bow for the chompy bird kill.")) {
            Keyboard.typeSend("/loldudeplswork");
           println("Personal message worked");
        }
    }

    @Override
    public void serverMessageReceived(String s) {
        sleep(300, 1000);
        if (s.contains("You scratch a notch on your bow for the chompy bird kill.")) {
            Keyboard.typeSend("/loldudeplswork");
            println("Server emssage worked");
        }
    }
}