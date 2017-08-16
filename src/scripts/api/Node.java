package scripts.api;

/**
 * Created by Bri on 8/16/2017.
 */
public abstract class Node {

    //Execute: where the action happens
    public abstract void execute();

    //Validate: if true, will run the execute method.
    public abstract boolean validate();

    //Will give the current state.
    public abstract String toString();

}
