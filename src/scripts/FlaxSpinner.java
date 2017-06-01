package scripts;

import org.tribot.api.Clicking;
import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.*;
import org.tribot.api2007.types.*;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

import java.awt.*;

@ScriptManifest(authors = {"Brik94"}, category = "Money", name = "FlaxSpinner")
public class FlaxSpinner extends Script implements Painting{

    Timer time = new Timer(3000); //3 seconds. Time it rests until another condiiton is T/F. Dynamic Sleeping.
    private final RSTile BANK_TILE = new RSTile(3208, 3220, 2),
                         BANK_STAIRS = new RSTile(3205, 3209, 2),
                         WHEEL_STAIRS = new RSTile(3205, 3209, 1);
    private final int BANKER_ID = 3227,
                        BANK_BOOTH = 18491,
                        WOOL_ID = 1737,
                        CLOSED_DOOR_ID = 1543,
                        OPEN_DOOR_ID = 1543,
                        SPINNING_WHEEL_ID = 14889;


    @Override
    public void onPaint(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString("Flax Spinner", 10, 70);
    }

    /**
     * Method used to withdraw wool from the bank.
     * Want to make Dynamic so add a Timer? 10:00
     * @return False. No particular reason, it's a boolean method and needs a return.
     */
    public boolean withdrawWool(){
        RSItem[] wool = Banking.find(WOOL_ID);
        if(wool != null && wool.length > 0){
            wool[0].click("Withdraw All");
            println("Withdrawed wool"); //will return true.
        }
        return false;
    }

    //Timer of some kind.
    public void waitUntilIdle(){
        long t = System.currentTimeMillis();
        while(Timing.timeFromMark(t) < General.random(1500, 2000)){
            if(time.isRunning() || Player.getRSPlayer().getAnimation() != -1){
                t = System.currentTimeMillis();
            }else{
                break;
            }
            sleep(50,150);
        }
    }

    //
    public boolean walkToStairs(){
        int plane = Player.getPosition().getPlane();
        if(plane == 1){
            //At spinning wheel.
            if(Walking.walkTo(WHEEL_STAIRS)){
                waitUntilIdle();
                return true;
            }
        }else if (plane == 2){
            //At Bank
            if(Walking.walkTo(BANK_STAIRS)){
                waitUntilIdle();
                return true;
            }
        }
        return true;
    }

    private boolean openDoor(){
        RSObject[] door = Objects.find(20, CLOSED_DOOR_ID);
        if(door != null && door.length > 0){
            DynamicClicking.clickRSObject(door[0], "Open");
            waitUntilIdle();
            return true;
        }
        return false;
    }

    public boolean spinWheel(){
        RSObject[] wheel = Objects.findNearest(10, SPINNING_WHEEL_ID);
        if(wheel != null && wheel.length > 0){
            if (DynamicClicking.clickRSObject(wheel[0], "Spin")){
                waitUntilIdle();
                return true;
            }
        }
        return false;
    }

    private boolean makeWool(){

        //RSInterfaceChild wool = Interfaces.get()
        if(!ChooseOption.isOpen()) {
            if(spinWheel()){
                Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        println("Clicked the spot.");
                        General.sleep(2000);
                        Mouse.click(130, 126, 3);
                        return ChooseOption.isOpen();
                    }
                }, General.random(750, 1000));
            }
        }
        ChooseOption.select(new Filter<RSMenuNode>() {
            @Override
            public boolean accept(RSMenuNode rsMenuNode) {
                println(rsMenuNode.getData1());
                println(rsMenuNode.getData2());
                return rsMenuNode.containsAction("Make X");
            }
        });
        println("Waiting to type");
        General.sleep(1000);
        return false;
    }


    @Override
    public void run() {
        println("Script has been started.");
        //withdrawWool();
        println("Got the wool.");
        //walkToStairs();
        openDoor();
        makeWool();
        Keyboard.typeSend("28");
        sleep(1000);
    }


    public static RSInterface getInterfaceChild(RSInterface parent, String text){
        if (parent == null)
            return null;
        for (RSInterface child : parent.getChildren())
            if (child.getText().contains(text) || child.getComponentName().contains(text))
                return child;
        return null;
    }
}
