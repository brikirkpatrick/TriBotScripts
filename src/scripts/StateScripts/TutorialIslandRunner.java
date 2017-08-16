package scripts.StateScripts;

import org.tribot.api2007.NPCs;
import org.tribot.api2007.types.RSNPC;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

import static org.tribot.api2007.NPCChat.*;


/**
 * Created by BriannaK on 6/15/2017.
 */
@ScriptManifest(authors = {"Brik94"}, category = "Misc", name = "Bri's Tutorial Island Runner")
public class TutorialIslandRunner extends Script {

    public final int RSGUIDE_ID = 3308;

    // Will randomize character creation then click Accept.
    public void charCreation(){

    }

    public boolean talkToRSGuide(){
        RSNPC[] RSGuide = NPCs.find(10, RSGUIDE_ID);

        RSGuide[0].click("Talk-to");
        while(getSelectOptionInterface() == null)
            clickContinue(true);
        println("continue...");
        return true;
    }

    @Override
    public void run() {
        talkToRSGuide();
    }
}
