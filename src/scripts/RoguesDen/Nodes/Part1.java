package scripts.RoguesDen.Nodes;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import scripts.api.Node;

/**
 * Created by Bri on 8/16/2017.
 * Areas may need tweaking.
 */
public class Part1 extends Node {
    private final RSArea ARoom = new RSArea(new RSTile(3056, 4992, 1), new RSTile(3043, 4999, 1));
    private final RSArea BRoom = new RSArea(new RSTile(3039, 4995, 1), new RSTile(3024, 5003, 1));
    private final RSArea CRoom = new RSArea(new RSTile(3024, 5001, 1), new RSTile(3011, 5005, 1));
        //polygon of D-Room.
        private final RSTile[] other = new RSTile[]{new RSTile(2994, 5005, 1), new RSTile(2999, 4998, 1), new RSTile(3011, 5005, 1)};
    private final RSArea DRoom = new RSArea(other);

    State state;

    private enum State{CONTORTION_BARS_PENDULUM, AVOID_TRAP, OPEN_DOOR}
    public State getState(){
        RSTile myPos = Player.getPosition();
        if(ARoom.contains(myPos)){
            return State.CONTORTION_BARS_PENDULUM;
        }else if (BRoom.contains(myPos)){
            if(myPos.equals(new RSTile(3029, 5003, 1))){
                return State.OPEN_DOOR;
            }else
                return State.AVOID_TRAP;
        }

        return null;
    }

    @Override
    public void execute() {
        this.state = getState();
        switch (this.state) {
            case OPEN_DOOR:
                openDoor();
                break;
            case AVOID_TRAP:
                avoidTrap();
                break;
            case CONTORTION_BARS_PENDULUM:
                barAndPendulum();
                break;
            default: break;

        }
    }

    @Override
    public boolean validate() {
        RSTile myPos = Player.getPosition();
        if(ARoom.contains(myPos) || BRoom.contains(myPos) || CRoom.contains(myPos) || DRoom.contains(myPos)){
            getState();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return state != null ? state.toString() : "";
    }

    //STOPPED HERE.----------------------
    public void barAndPendulum(){
        if (Player.getPosition() != new RSTile(3048, 4997, 1)) {
            Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    General.sleep(100);
                    RSObject[] contortionBar = Objects.findNearest(10, 7251);
                    return DynamicClicking.clickRSObject(contortionBar[0], 1);
                }
            }, General.random(2000, 3000));
        }

        //Turns camera and clicks tile after pendulum.
        while(Camera.getCameraRotation() != General.random(20, 25)) { //while loops might weight down cpu...
            Camera.setCameraAngle(100);
            Camera.setCameraRotation(General.random(20, 25)); //CRAZY CAMERA???
        }
        Walking.clickTileMS(new RSTile(3039, 4997, 1), 1);
    }

    public void avoidTrap(){
        while(Camera.getCameraRotation() != General.random(112, 118)) {
            Camera.setCameraRotation(General.random(112, 118));
        }
        RSTile tile1 = new RSTile(3029, 5003, 1); //2nd tile after pendulum. Infront of Floor trap.
        Walking.clickTileMS(tile1, 1);
    }

    public void openDoor(){
        if (Player.getPosition() != new RSTile(3022, 5001, 1)) {
            Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    General.sleep(100);
                    RSObject[] nextDoor = Objects.findNearest(10, 7255);
                    return DynamicClicking.clickRSObject(nextDoor[0], 1);
                }
            }, General.random(2000, 3000));
        }
    }
}
