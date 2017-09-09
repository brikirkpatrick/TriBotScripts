package scripts.RoguesDen;

import org.tribot.api.Timing;
import org.tribot.script.interfaces.Painting;
import scripts.RoguesDen.Nodes.*;
import scripts.api.Node;
import org.tribot.api.General;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;


/**
 * Created by Bri on 8/16/2017.
 * Main Class.
 * Note: So far script seems to use about 25% CPU Power.
 */
@ScriptManifest(authors = {"PureImagination"}, category = "Mini games", name = "PureRogueDenRunner",
        description = "Rogues Den Mini Game Runner.")

public class PureRogueDenRunner extends Script implements Painting{
    private final List<Node> nodes = new ArrayList<>();
    private final long START_TIME = System.currentTimeMillis();
    private Node currentNode = null;

    @Override
    public void onPaint(Graphics g) {
        long runtime = Timing.timeFromMark(START_TIME);
        g.drawString("PureRogueDenRunner", 5, 50);
        g.drawString("Time running: " + Timing.msToString(runtime), 5, 70);
        g.drawString("State: " + currentNode != null ? currentNode.toString() : "null",5,90);
    }

    @Override
    public void run() {
        Collections.addAll(nodes, new StartGame(), new Part1(), new Part2());
        loop(20, 40);

    }

    private void loop(int min, int max){
        while(true){
            for(final Node node:nodes){
                if(node.validate()){
                    currentNode = node;
                    node.execute();
                    sleep(General.random(min, max));
                }
            }
        }
    }
}
