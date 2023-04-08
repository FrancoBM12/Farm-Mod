package com.francobm.testmod;

public class Update {

    private final TestMod testMod;

    public Update(TestMod testMod) {
        this.testMod = testMod;
    }

    public void cropsTick() {
        if(!testMod.isCrops()) return;
        if(testMod.getCanUse() == 20){
            testMod.setCanUse(-1);
            return;
        }
        if(!testMod.isCanUse()) {
            testMod.increaseCanUse();
            return;
        }
        testMod.isMaxGrow();
    }

    public void mobsTick() {
        if(!testMod.isMobs()) return;
        if(testMod.getCanUse() == 20){
            testMod.mobs();
            testMod.setCanUse(-1);
            return;
        }
        testMod.increaseCanUse();
    }
}
