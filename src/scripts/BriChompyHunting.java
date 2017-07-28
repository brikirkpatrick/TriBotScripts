package scripts;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;

import java.awt.*;
import java.util.Random;

import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import static org.tribot.api2007.Walking.clickTileMS;

/**
 * Fix 1: Need to add total number of bellows in players inventory at the beginning.
 * Fix getState 2nd else if statement to include this.
 * Fix 2: Check for correct chompy hunting gear at beginning
 * Fix 3: Add run time + Chompy kills p/hr.
 * DONE 3: Placing new toad where bloated toad already exists
 * https://tribot.org/forums/topic/30185-trilezs-scripting-tutorial/
 * https://pastebin.com/v0Y6vAdy     //ABC2 implement
 */


@ScriptManifest(authors = {"Brik94"}, category = "MiniGames", name = "PureChompyHunting",
        description = "Chompy hunting using an efficient method.")
public class BriChompyHunting extends Script implements Painting, MessageListening07{

    private final int EMPTY_BELLOWS_ID = 2871, TOAD_ID = 1473, BLOATED_TOAD_ID= 1474, INV_TOAD_ID = 2875,
                      ALIVE_CHOMY_ID = 1475, SWAMP_ID = 684;
                        //OGRE_ARROW_ID = 2866, FULL_BELLOWS_ID = 2872,COMP_OGREBOW_ID = 4827, DEAD_CHOMPY_ID = 1476
    private final int[] USUSABLE_BELLOW_ID = {2872, 2873, 2874};
    private int killedChompies;
    private State SCRIPT_STATE = getState();
    private ABCUtil abc_util = new ABCUtil(); //AntiBanCompliance
    private final long START_TIME = System.currentTimeMillis();

    @Override
    public void onPaint(Graphics g) {
        //CALCULATIONS
        long timeRan = System.currentTimeMillis()-START_TIME;
        double multiplier = timeRan / 3600000D;
        int killsPerHr = (int) (killedChompies / multiplier);
        //https://tribot.org/forums/topic/7010-tutorialbrians-rookie-pro-scripting-tutorialtutorial/
        g.setColor(Color.WHITE);
        g.drawString("Pure's Chompy Killer", 10, 70);
        g.drawString("Running for: " + Timing.msToString(timeRan), 10, 90);
        g.drawString("Total chompies killed: " + killedChompies, 10, 110);
        g.drawString("Kills Per Hour: " + killsPerHr, 10, 130);
        g.drawString("State: " + SCRIPT_STATE, 10, 150);
    }

    private enum State {
        KILLING_CHOMPY, FILLING_BELLOWS, CATCHING_TOAD, PLACING_TOAD, LOOK_FOR_CHOMPY, PLUCKING_CHOMPY
    }

    private State getState(){
        if (checkForChompy()){ //chompy in sight
            if(numEmptyBellows() == 24){
                println("Less than 1 bellows");
                return State.FILLING_BELLOWS;
            }else {
                //Camera.setCameraAngle(General.random(40, 70)); Prolly unnecessary.
                return State.KILLING_CHOMPY;
            }
        }else if(numEmptyBellows() != 24) {//there is atleast 1 useable bellow
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
        Camera.setRotationMethod(Camera.ROTATION_METHOD.ONLY_KEYS);
        killedChompies = 0;

        //noinspection InfiniteLoopStatement
        while(true) {
            sleep(50);
            SCRIPT_STATE = getState();

            switch(SCRIPT_STATE){

                case KILLING_CHOMPY:
                    killChompy();
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
            //sleep(40, 80);
        }
    }

    private void abc() {

        if (this.abc_util.shouldCheckTabs())
            this.abc_util.checkTabs();

        if (this.abc_util.shouldCheckXP())
            this.abc_util.checkXP();

        if (this.abc_util.shouldExamineEntity())
            this.abc_util.examineEntity();

        if (this.abc_util.shouldMoveMouse())
            this.abc_util.moveMouse();

        if (this.abc_util.shouldPickupMouse())
            this.abc_util.pickupMouse();

        if (this.abc_util.shouldRightClick())
            this.abc_util.rightClick();

        if (this.abc_util.shouldRotateCamera())
            this.abc_util.rotateCamera();

        if (this.abc_util.shouldLeaveGame())
            this.abc_util.leaveGame();

    }

    private int numEmptyBellows(){
        return Inventory.getCount(EMPTY_BELLOWS_ID);
    }

    private void fillBellows(){

        //if all bellows are empty, fill them up.
        while (numEmptyBellows() != 0){ // returns false
            if (Player.getAnimation() == -1) {
                RSItem[] ebellows = Inventory.find(EMPTY_BELLOWS_ID);
                RSObject[] bubbles = Objects.findNearest(10, SWAMP_ID);
                ebellows[0].click("Use");
                DynamicClicking.clickRSObject(bubbles[0], 1);
                waitUntilIdle();
            }

//            Timing.waitCondition(new Condition() {
//                @Override
//                public boolean active() {
//                    General.sleep(100);
//                    return //boolean all bellows full?
//                }
//            })
        }
    }

    private void catchToad(){
        RSItem[] goodBellows = Inventory.find(USUSABLE_BELLOW_ID);
        RSNPC[] toad = NPCs.findNearest(10, TOAD_ID);
        Camera.setCameraAngle(General.random(40, 70));
        goodBellows[0].click("Use");
        DynamicClicking.clickRSNPC(toad[0], 1);
        waitUntilIdle();

        //https://tribot.org/forums/topic/60128-tutorial-dynamic-sleepingconditional-sleeping/
    }

    private void placeToad(){
        RSItem[] bloatedToad = Inventory.find(INV_TOAD_ID);
        bloatedToad[0].click("Drop");
        waitUntilIdle();
    }

    private boolean checkForChompy(){
        RSNPC[] chompy = NPCs.findNearest(ALIVE_CHOMY_ID);
        if(chompy.length > 0 && chompy[0].getID()!= 1476){
            Camera.turnToTile(chompy[0].getPosition());
            return true;
        }
        return false;
    }

    //Maybe add conditional Sleeping
    //https://tribot.org/forums/topic/60128-tutorial-dynamic-sleepingconditional-sleeping/
    private void waitUntilIdle(){
        long t = System.currentTimeMillis();

        while(Timing.timeFromMark(t) < General.random(400, 800)){ //400, 800
            sleep(400, 800);

            if(Player.isMoving() || Player.getAnimation() != -1){
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
            first_chompy[0].click("Attack");
            waitUntilIdle();
            killChompy(); //Recursion. Base case occurs from checkForChompy == false
        }
    }

    private RSTile getRandomTileNear(RSTile t, int offset) {
        int random = General.random(0, 4);
        RSTile rTile;

        switch (random) {

            case 1:

                rTile = new RSTile(t.getX() + General.random(0, offset), t.getY() + General.random(0, offset));
                break;

            case 2:

                rTile = new RSTile(t.getX() - General.random(0, offset), t.getY() - General.random(0, offset));
                break;

            case 3:

                rTile = new RSTile(t.getX() - General.random(0, offset), t.getY() + General.random(0, offset));
                break;

            default:

                rTile = new RSTile(t.getX() + General.random(0, offset), t.getY() - General.random(0, offset));
                break;
        }
        return rTile;
    }


    //Methods that have to be generated from MessageListener07------------------------------------------------------
    @Override
    public void serverMessageReceived(String s) {
        sleep(300, 1000);

        //Increments killed chompies based on server message received.
        if (s.contains("You scratch a notch on your bow for the chompy bird kill.")) {
            println("Server message worked. Chompy killed.");
            killedChompies++;
        }

        //Standing on an object, so move to random tile close by.
        if(s.contains("There is a bloated toad already placed at this location.")){
            //Move to another tile.
            RSTile current = Player.getPosition();
            RSTile randomTile =  getRandomTileNear(current, 4);
            clickTileMS(randomTile, 1);
            Camera.turnToTile(randomTile);
            Camera.setCameraAngle(General.random(40, 70));
            println("Server message worked. Moved to random tile.");
        }

        //Something is wrong, so we move to another tile close by.
        if(s.contains("I can't reach that!")){
            RSTile current = Player.getPosition();
            RSTile randomTile =  getRandomTileNear(current, 4);
            clickTileMS(randomTile, 1);
            Camera.turnToTile(randomTile);
            Camera.setCameraAngle(General.random(40, 70));
            println("Server message worked. Moved to random tile.");
        }
    }

    @Override
    public void clanMessageReceived(String s, String s1) {

    }

    @Override
    public void playerMessageReceived(String s, String s1) {

    }

    @Override
    public void tradeRequestReceived(String s) {

    }

    @Override
    public void duelRequestReceived(String s, String s1) {

    }

    @Override
    public void personalMessageReceived(String s, String s1) {

    }
    //--------------------------------------------------------------------------------------------------------------

}