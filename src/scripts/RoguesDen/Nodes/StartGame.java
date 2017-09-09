package scripts.RoguesDen.Nodes;


import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import scripts.api.Node;

/**
 * Created by Bri on 8/16/2017.
 * Node used to begin the mini game.
 *
 * Idea: If in an unfamilar Area, restart game by teleporting out with jewel.
 */
public class StartGame extends Node {
    //fix start room to contain corridor of 1st door.
    private final RSArea ROGUES_DEN_START = new RSArea(new RSTile(3039, 4961, 1), new RSTile(3061, 4991, 1));
    private final int RICHARD_ID = 3189, JEWEL_ID = 5561;
    private State state;

    private enum State{ TALKING_TO_RICHARD, ENTERING_DOORWAY }

    private State getState() {
        RSTile myPos = Player.getPosition();
        if((ROGUES_DEN_START.contains(myPos)))
            return State.ENTERING_DOORWAY;
        else
            return null;
    }


    @Override
    public void execute() {
        this.state = getState();
        switch (this.state){
            case ENTERING_DOORWAY:
                enterDoorway();
                break;
            default: break;
        }

    }

    @Override
    //I'm in the starter area but I don't have a jewel =  mini game hasn't begun.
    public boolean validate() {
        RSTile myPos = Player.getPosition();
        if((ROGUES_DEN_START.contains(myPos))){
            getState();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return state != null ? state.toString() : "";
    }

    /*
        Deprecated. Due to a recent update, talking to Richard is no longer necessary.
     */
    private boolean talkToRichard(){
        RSNPC[] richard = NPCs.findNearest(50, RICHARD_ID);
        Camera.turnToTile(richard[0]);

        if (NPCChat.getSelectOptionInterface() == null){
            if(!richard[0].isOnScreen())
                Walking.walkTo(richard[0]);
            richard[0].click("Talk-to");
            Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    General.sleep(100);
                    return NPCChat.clickContinue(true);
                }
            }, General.random(750, 1000));
        }

        if(Inventory.getCount(JEWEL_ID) != 1) {
            Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    clickThroughChat();
                    General.sleep(100);
                    return Inventory.getCount(JEWEL_ID) == 1;
                }
            }, General.random(3000, 4000));
        }else{
            clickThroughChat();
        }

        //fail-safe. 100% Mini game has begun if we have the jewel. Otherwise, try again.
        return (Inventory.getCount(JEWEL_ID) == 1);
    }

    private boolean enterDoorway(){
        RSTile afterDoorTile = new RSTile(3056, 4992, 1);
        RSArea firsDoorWalkway = new RSArea(new RSTile(3056, 4987, 1), new RSTile(3056, 4991, 1));
        //Walking.blindWalkTo(firstDoorTile, null, 0);

        //Clicks door.
        if (afterDoorTile.isOnScreen() && Player.getPosition()!= afterDoorTile) {
            General.sleep(400, 800);
            RSObject[] firstDoor = Objects.findNearest(10, 7256);
            if(firstDoor.length > 0)
                DynamicClicking.clickRSObject(firstDoor[0], 1);
            waitUntilIdle();
        }else{
            Walking.walkTo(firsDoorWalkway.getRandomTile());
            waitUntilIdle();
        }
        return (Inventory.getCount(JEWEL_ID) == 1);
    }

    private void clickThroughChat(){
        //Continues until chat dialogue is over. Mini game should be started.
        while (NPCChat.getMessage() != null || NPCChat.getSelectOptionInterface() != null || NPCChat.getClickContinueInterface() != null) { //continue loop until interface is done.
            if (NPCChat.getSelectOptionInterface() != null)
                NPCChat.selectOption("I want to try the maze again!", true);
            else
                NPCChat.clickContinue(true);
            General.sleep(100);
        }

    }

    private void waitUntilIdle(){
        long t = System.currentTimeMillis();

        while(Timing.timeFromMark(t) < General.random(400, 800)){ //400, 800
            General.sleep(400, 800);

            if(Player.isMoving() || Player.getAnimation() != -1){
                t = System.currentTimeMillis();
                continue;
            }

            General.sleep(40, 80);

            if(Player.getAnimation() == -1)
                break;

        }
    }

}
