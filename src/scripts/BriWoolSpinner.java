package scripts;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.*;
import org.tribot.api2007.types.*;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

import java.awt.*;

@ScriptManifest(authors = {"Brik94"}, category = "Money", name = "BriWoolSpinner",
                description = "Wool Spinner. Is Functional but not optimized.")

public class BriWoolSpinner extends Script implements Painting{

    private final long START_TIME = System.currentTimeMillis();
    //Timer time = new Timer(3000); //3 seconds. Time it rests until another condiiton is T/F. Dynamic Sleeping.
    private final RSTile BANK_TILE = new RSTile(3208, 3220, 2), SPIN_TILE =  new RSTile(3209, 3213, 1),
            BANK_STAIRS_TILE = new RSTile(3205, 3209, 2),
            WHEEL_STAIRS_TILE = new RSTile(3205, 3209, 1),
            DOOR_TILE = new RSTile(3207, 3214, 1);
    private final int  WOOL_ID = 1737, BALL_ID = 1759, CLOSED_DOOR_ID = 1543,
            SPINNING_WHEEL_ID = 14889, STAIRS_WHEEL_ID = 16672, STAIRS_BANK_ID = 16673,
            CRAFTING_ANIMATION_ID = 894, START_XP = Skills.getXP(Skills.SKILLS.CRAFTING);
    private State SCRIPT_STATE = getState();
    private int CURRENTXP = START_XP;


    @Override
    public void onPaint(Graphics g) {
        long timeRan = System.currentTimeMillis() - START_TIME;
        int xpGained = CURRENTXP - START_XP;
        double multiplier = timeRan / 3600000D;
        int xpPerHour = (int) (xpGained / multiplier);
        int woolMade =  xpGained/(int) 2.5;
        int woolPerHour = (int) (woolMade / multiplier);

        g.setColor(new Color(60, 60, 60));
        g.fillRect(5, 50, 170, 165);

        g.setColor(Color.WHITE);
        g.drawString("Wool Spinner", 10, 70);
        g.drawString("Running for: " + Timing.msToString(timeRan), 10, 90);
        g.drawString("State: " + SCRIPT_STATE, 10, 110);

        g.drawString("XP Gained " + xpGained + " (" + xpPerHour + "/h)", 10, 140);
        g.drawString("Flax made: " + woolMade + " (" + woolPerHour + "/h)", 10, 160);

        g.drawString("Flax interface: " + haveWoolInterface(), 10, 190);
        g.drawString("Type interface: " + haveAmountInterface(), 10, 210);
    }

    // Video pt.2 23mins
    public enum State{
        SELECT_WOOL, TYPE_AMOUNT, CRAFTING,
        WHEEL_TO_STAIRS, CLIMB_STAIRS, STAIRS_TO_BANK, OPEN_BANK,
        DEPOSIT_BALL, WITHDRAW_WOOL, BANK_TO_STAIRS, STAIRS_TO_WHEEL,
        OPEN_DOOR, USE_SPIN_WHEEL, WALK_TO_DOOR
    }

    private State getState(){
        if (haveWool()) {
            if (atBank()) {
                return State.BANK_TO_STAIRS;
            } else {
                if (atSpinWheel()) {
                    if (haveWoolInterface()) {
                        return State.SELECT_WOOL;
                    } else {
                        if (haveAmountInterface()) {
                            return State.TYPE_AMOUNT;
                        } else {
                            if(areCrafting()){
                                return State.CRAFTING;
                            }else{
                                return State.USE_SPIN_WHEEL;
                            }
                        }
                    }
                } else {
                    if (Player.getPosition().getPlane() != 1) {
                        if (atStairs()) {
                            return State.CLIMB_STAIRS;
                        } else {
                            return State.BANK_TO_STAIRS;
                        }
                    } else {
                        if (isDoorClosed()) {
                            if (atDoor()) {
                                return State.OPEN_DOOR;
                            } else {
                                return State.WALK_TO_DOOR;
                            }
                        } else {
                            return State.STAIRS_TO_WHEEL;
                        }
                    }
                }
            }
        } else {
            if (Player.getPosition().getPlane() != 2) {
                if (atStairs()) {
                    return State.CLIMB_STAIRS;
                } else { //At wheel. Is door open?
                    if(isDoorClosed()){
                        return State.OPEN_DOOR;
                    }else{
                        return State.WHEEL_TO_STAIRS;
                    }
                }
            } else {
                if (Banking.isBankScreenOpen()) {
                    if (haveWoolBall()) {
                        return State.DEPOSIT_BALL;
                    } else {
                        return State.WITHDRAW_WOOL;
                    }
                } else {
                    if (atBank()) {
                        return State.OPEN_BANK;
                    } else {
                        return State.STAIRS_TO_BANK;
                    }
                }
            }
        }
    }

    /**
     * Method used to withdraw wool from the bank.
     * Want to make Dynamic so add a Timer? 10:00
     * @return False. No particular reason, it's a boolean method and needs a return.
     */
    private boolean withdrawWool(){
        RSItem[] wool = Banking.find(WOOL_ID);
        if(wool != null && wool.length > 0){
            wool[0].click("Withdraw-All");
            println("Withdrawed wool"); //will return true.
        }
        return false;
    }

    private boolean depositBalls(){
        RSItem[] ball = Inventory.find(BALL_ID);
        if(ball != null &&ball.length > 0){
            ball[0].click("Deposit-All");
            println("Deposited"); //will return true.
        }
        return false;
    }

    //Timer of some kind.
    private void waitUntilIdle(RSTile tile){
        long t = System.currentTimeMillis();

        while(Timing.timeFromMark(t) < General.random(400, 800)){ //400, 800
            sleep(400, 800);

            if(Player.isMoving() || Player.getRSPlayer().getAnimation() != -1){
                t = System.currentTimeMillis();
                continue ;
            }

            sleep(40, 80);

            if(Player.getPosition().distanceTo(tile) == 0);
            break;
        }
    }

    private boolean atStairs(){
        RSTile myPos = Player.getPosition();
        int plane = Player.getPosition().getPlane();
        if(plane == 1) {
            return myPos.distanceTo(BANK_STAIRS_TILE) < 3;
        }else if (plane == 2){
            return myPos.distanceTo(WHEEL_STAIRS_TILE) < 3;
        }
        return false;
    }

    private boolean walkToTile(RSTile tile){
        if(Walking.walkTo(tile)){
            waitUntilIdle(tile);
            return true;
        }
        return false;
    }

    //Walks to stairs.
    private boolean walkToStairs(){
        int plane = Player.getPosition().getPlane();
        if(plane == 1){
            //At spinning wheel.
            return walkToTile(WHEEL_STAIRS_TILE);
        }else if (plane == 2){
            //At Bank
            return walkToTile(BANK_STAIRS_TILE);
        }
        return false;
    }

    private boolean walkToBank(){
        return walkToTile(BANK_TILE);
    }

    private boolean walkToDoor(){
        return walkToTile(DOOR_TILE);
    }

    private boolean  walkToWheel(){
        return walkToTile(SPIN_TILE);
    }

    //Climbs the stairs. Maybe combine this with walkToStairs.
    private boolean climbStairs(){
        int plane = Player.getPosition().getPlane();
        if(plane == 1) {
            //At spinning wheel.
            RSObject[] stairs = Objects.findNearest(10, STAIRS_WHEEL_ID);
            if (stairs != null && stairs.length > 0 && DynamicClicking.clickRSObject(stairs[0], "up")) {
                waitUntilIdle(stairs[0].getPosition());
                return true;
            }
        }else if (plane == 2){
            //At Bank
            RSObject[] stairs = Objects.findNearest(10, STAIRS_BANK_ID);
            if (stairs != null && stairs.length > 0 && DynamicClicking.clickRSObject(stairs[0], "down")) {
                waitUntilIdle(stairs[0].getPosition());
                return true;
            }
        }
        return false;
    }

    private boolean atDoor(){
        RSTile myPos = Player.getPosition();
        return myPos.distanceTo(DOOR_TILE) < 5;
    }

    private boolean openDoor(){
        RSObject[] door = Objects.find(20, CLOSED_DOOR_ID);
        if(door != null && door.length > 0){
            DynamicClicking.clickRSObject(door[0], "Open");
            waitUntilIdle(door[0].getPosition());
            return true;
        }
        return false;
    }

    private boolean atSpinWheel(){
        RSTile myPos = Player.getPosition();
        return myPos.distanceTo(SPIN_TILE) < 3;
    }

    private boolean spinWheel(){
        RSObject[] wheel = Objects.findNearest(10, SPINNING_WHEEL_ID);
        if(wheel != null && wheel.length > 0){
            if (DynamicClicking.clickRSObject(wheel[0], "Spin")){
                while(isDoorClosed()){
                    openDoor();
                }
                waitUntilIdle(wheel[0].getPosition());
                return true;
            }
        }
        return false;
    }

    private boolean spinWool(){
        RSInterfaceChild woolBall = Interfaces.get(459, 100); //Ball of Wool
        //println("Woolball null?: " + woolBall.isHidden());
        while(woolBall == null){
            println("waiting for interface to open...");
            sleep(500, 1500);
            woolBall = Interfaces.get(459, 100);
        }

        if(!woolBall.isHidden() && woolBall.click("Make X")){
           println("Selected X");

            return true;
        }
        return false;
    }

    private boolean haveWoolInterface(){
        RSInterfaceChild woolBall = Interfaces.get(459, 100); //Ball of Wool
        return woolBall != null && !woolBall.isHidden();
    }

    private boolean haveAmountInterface(){
        RSInterfaceChild asterisk = Interfaces.get(162, 33);

        if (!asterisk.isHidden()){
            String text = asterisk.getText();
            if(text != null && text.equals("*")){
                return true;
            }
        }
        return false;
    }

    private boolean typeAmount(){
        if(haveAmountInterface()){
            Keyboard.typeSend("28");
            return true;
        }
        return false;
    }

    private boolean areCrafting(){
        long t = System.currentTimeMillis();
        while(Timing.timeFromMark(t) < General.random(1000, 1400)){
            if(Player.getAnimation() == CRAFTING_ANIMATION_ID){
                return true;
            }
            sleep(50, 100);
        }
        return false;
    }

    private boolean isDoorClosed(){
        RSObject[] doors = Objects.findNearest(15, CLOSED_DOOR_ID);
        if (doors != null && doors.length > 0)
            for(RSObject door : doors)
                if(door.getPosition().getY() < 3220)
                    return true;
        return false;
    }

    private boolean haveWool(){
        RSItem[] wool = Inventory.find(WOOL_ID);
        return wool != null && wool.length > 0;
    }

    private boolean haveWoolBall(){
        RSItem[] ball = Inventory.find(BALL_ID);
        return ball != null && ball.length > 0;
    }

    private boolean atBank(){
        RSTile myPos = Player.getPosition();
        return myPos.distanceTo(BANK_TILE) < 5;
    }

    @Override
    public void run() {
        Mouse.setSpeed(General.random(130,150 ));

        //Change loop to stop when out of Wool/Item.n
        while(true){
            SCRIPT_STATE = getState();
            CURRENTXP = Skills.getXP(Skills.SKILLS.CRAFTING);

            switch(SCRIPT_STATE){
                case BANK_TO_STAIRS:
                    walkToStairs();
                    break;

                case CLIMB_STAIRS:
                    climbStairs();
                    break;

                case CRAFTING:
                    sleep(200, 400);
                    break;

                case DEPOSIT_BALL:
                    depositBalls();
                    break;

                case OPEN_BANK:
                    Banking.openBank();
                    break;

                case OPEN_DOOR:
                    openDoor();
                    break;

                case SELECT_WOOL:
                    spinWool();
                    break;

                case STAIRS_TO_BANK:
                    walkToBank();
                    break;

                case STAIRS_TO_WHEEL:
                    walkToWheel();
                    //waitUntilIdle();
                    break;

                case TYPE_AMOUNT:
                    typeAmount();
                    break;

                case USE_SPIN_WHEEL:
                    spinWheel();
                    break;

                case WHEEL_TO_STAIRS:
                    walkToStairs();
                    break;

                case WITHDRAW_WOOL:
                    withdrawWool();
                    break;

                case WALK_TO_DOOR:
                    walkToDoor();
                    break;
            }
            sleep(40, 80);
        }
    }
}
