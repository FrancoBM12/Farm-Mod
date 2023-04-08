package com.francobm.testmod.scheduler;

import com.francobm.testmod.TestMod;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {

    private final TestMod testMod;
    //Actions that run once then delete from queue
    private final List<Action> queuedActions = new ArrayList<>();
    //Actions that repeat indefinitely
    private final List<Action> repeatingActions = new ArrayList<>();

    //For tracking world change events. This is used to reset repeating action timers when a world is joined
    private boolean doesWorldExist;

    public Scheduler(TestMod testMod) {
        this.testMod = testMod;
    }

    public void tick(MinecraftClient client) {

        //World change detection
        //This resets the timer on each repeating action on world change
        //Needed because Util.milliTime() can return a different value when the game is first initializing
        if ((client.world == null) == doesWorldExist) {
            doesWorldExist = (client.world != null);
            repeatingActions.forEach(Action::resetTimer);
        }

        //Clear out the action queue whenever **** is disabled or we are not ingame
        if (testMod.isDisabled()) queuedActions.clear();
        //Clear out the action queue whenever world or player goes null
        //Also returns method to prevent NullPointers on any scheduled actions
        if (client.world == null || client.player == null) {
            queuedActions.clear();
            return;
        }

        //Check if any actions are ready to execute, remove if so
        queuedActions.removeIf(Action::tick);
        //Tick all repeating actions
        repeatingActions.forEach(Action::tick);

    }

    public void scheduleAction(ActionType actionType, long delay, Runnable runnable) {
        queuedActions.add(new Action(actionType, delay, runnable));
    }

    public void scheduleAction(Action action) {
        queuedActions.add(action);
    }

    public void scheduleRepeatingAction(long interval, Runnable runnable) {
        repeatingActions.add(new Action(ActionType.REPEATING_ACTION, interval, runnable));
    }

    public boolean isRecastQueued() {
        //True if any scheduled actions are of the RECAST type
        return queuedActions.stream().anyMatch(action -> action.getActionType() == ActionType.RECAST);
    }
}