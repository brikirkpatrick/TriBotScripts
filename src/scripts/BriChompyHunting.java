package scripts;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.rs3.Player;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;

import java.awt.*;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;



@ScriptManifest(authors = {"Brik94"}, category = "Quests", name = "BriChompyHunting",
        description = "Chompy hunting using an efficient method.")
public class BriChompyHunting extends Script implements Painting {

    //Note: need to add all Bow ID's.
    private final int EMPTY_BELLOWS_ID = 2871, FULL_BELLOWS_ID = 2872, COMP_OGREBOW_ID = 4827,
            OGRE_ARROW_ID = 2866, TOAD_ID = 1473, BLOATED_TOAD_ID= 1474, INV_TOAD_ID = 2875, ALIVE_CHOMY_ID = 1475,
            DEAD_CHOMPY_ID = 1476, SWAMP_ID = 684;
    private final int[] USUSABLE_BELLOW_ID = {2872, 2873, 2874};
    private int killedChompies;
    private State SCRIPT_STATE = getState();
    //Timer time = new Timer(3000);

    @Override
    public void onPaint(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString("Bri's Chompy Killer", 10, 70);
        g.drawString("Total chompies killed: " + killedChompies, 10, 90);
        g.drawString("State: " + SCRIPT_STATE, 10, 110);
    }

    private enum State {
        KILLING_CHOMPY, FILLING_BELLOWS, CATCHING_TOAD, PLACING_TOAD, LOOK_FOR_CHOMPY, PLUCKING_CHOMPY
    }

    private State getState(){
        if (checkForChompy()){ //chompy in sight
            if(howManyBellows() < 1){
                return State.FILLING_BELLOWS;
            }else {
                return State.KILLING_CHOMPY;
            }
        }else if(howManyBellows() > 0) {//we have atleast 1 useable bellow
            if (Inventory.getCount(INV_TOAD_ID) < 1) { //we have no toads
                return State.CATCHING_TOAD;
            } else { //we do have toads
                return State.PLACING_TOAD;
            }
        }else{ //no chompy + no full bellows
            return State.FILLING_BELLOWS;
        }
    }

    @Override
    public void run() {
        killedChompies = 0;

        //noinspection InfiniteLoopStatement
        while(true) {
            SCRIPT_STATE = getState();

            switch(SCRIPT_STATE){

                case KILLING_CHOMPY:
                    killChompy();
                    killedChompies++;
                    break;
                case FILLING_BELLOWS:
                    fillBellows();
                    break;
                case CATCHING_TOAD:
                    catchToad();
                    break;
                case PLACING_TOAD:
                    placeToad();
                    break;
            }
        }

    }

    public boolean haveMinBellows(){
        RSItem[] ebellows = Inventory.find(EMPTY_BELLOWS_ID);
        RSItem[] fbellows = Inventory.find(FULL_BELLOWS_ID);

        if ((ebellows != null && ebellows.length > 0) || (fbellows != null && fbellows.length > 0)){
            println("Bellows in inventory");
            return true;
        }
        return false;
    } //might revisit

    private int howManyBellows(){
        return Inventory.getCount(EMPTY_BELLOWS_ID);
    }

    //Have atleast 1 usable bellow.
    private boolean minFullBellow() {
        RSItem[] fbellows = Inventory.find(FULL_BELLOWS_ID);
        if (fbellows.length > 0) {
            println("FULL Bellows in inventory");
            return true;
        }
        return false;
        //return fbellows != null && fbellows.length > 0;
    }

    private void fillBellows(){

        //if all bellows are empty, fill them up.
        while (howManyBellows() != 0){ // returns false
            if (Player.getAnimation() == -1) {
                println("Player standing");
                RSItem[] ebellows = Inventory.find(EMPTY_BELLOWS_ID);
                RSObject[] bubbles = Objects.findNearest(10, SWAMP_ID);
                ebellows[0].click("Use");
                DynamicClicking.clickRSObject(bubbles[0], 1);
                waitUntilIdle();
            }

        }
    }

    private void catchToad(){
        if (minFullBellow()){
            RSItem[] goodBellows = Inventory.find(USUSABLE_BELLOW_ID);
            RSNPC[] toad = NPCs.findNearest(10, TOAD_ID);

            goodBellows[0].click("Use");
            DynamicClicking.clickRSNPC(toad[0], 1);
            waitUntilIdle();
        }
    }

    private void placeToad(){
        RSItem[] bloatedToad = Inventory.find(INV_TOAD_ID);
        bloatedToad[0].click("Drop");
    }

    private boolean checkForChompy(){
        RSNPC[] chompy = NPCs.findNearest(ALIVE_CHOMY_ID);
        if(chompy.length > 0 && chompy[0].getID()!= 1476){
            //chompy[0].getPosition();
            println("Chompy in sight yes");
            return true;
        }

        println("not clickable");
        return false;
    }

    private void waitUntilIdle(){
        long t = System.currentTimeMillis();

        while(Timing.timeFromMark(t) < General.random(400, 800)){ //400, 800
            sleep(400, 800);

            if(org.tribot.api2007.Player.isMoving() || org.tribot.api2007.Player.getRSPlayer().getAnimation() != -1){
                t = System.currentTimeMillis();
                continue;
            }

            sleep(40, 80);

            if(Player.getAnimation() == -1)
                break;
        }
    }

    private void killChompy(){
        RSNPC[] first_chompy = NPCs.findNearest(50, ALIVE_CHOMY_ID);
        if(checkForChompy()){ //if chompy is here
            //Attack it once. Then check for new chompy. If no new chompy, proceed to attack current chompy.
            println("going for kill");
            first_chompy[0].click("Attack");
            waitUntilIdle();
            killChompy();
        }
    }
}