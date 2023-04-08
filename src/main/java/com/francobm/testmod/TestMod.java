package com.francobm.testmod;

import com.francobm.testmod.scheduler.Scheduler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class TestMod implements ClientModInitializer {
    private final Logger logger = LogManager.getLogManager().getLogger("");
    private int canUse = -1;
    private static TestMod instance;
    private boolean crops;
    private boolean mobs;
    private int delay = 0;
    private KeyBinding cropsKeyBinding;
    private KeyBinding mobsKeyBinding;
    private Scheduler scheduler;
    private Update update;
    private MinecraftClient client;

    @Override
    public void onInitializeClient() {
        if(instance == null) {
            instance = this;
        }
        client = MinecraftClient.getInstance();
        cropsKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.testmod.crops", InputUtil.GLFW_KEY_R, "key.categories.crops"));
        mobsKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.testmod.mobs", InputUtil.GLFW_KEY_Y, "key.categories.mobs"));
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        scheduler = new Scheduler(this);
        update = new Update(this);
        scheduler.scheduleRepeatingAction(1, () -> {
            update.cropsTick();
            update.mobsTick();
        });
    }

    private void tick(MinecraftClient client) {
        if(cropsKeyBinding.wasPressed()){
            toggleCrops();
            if(isCrops()){
                client.player.sendMessage(new LiteralText("§aEl recolector de bayas está activado."), true);
            }else{
                client.player.sendMessage(new LiteralText("§cEl recolector de bayas está desactivado."), true);
            }
        }
        if(mobsKeyBinding.wasPressed()) {
            toggleMobs();
            if(isMobs()){
                client.player.sendMessage(new LiteralText("§aEl matador de mobs está activado."), true);
            }else{
                client.player.sendMessage(new LiteralText("§cEl matador de mobs está desactivado."), true);
            }
        }
        scheduler.tick(client);
    }

    public void mobs(){
        if(client.world == null) return;
        if(client.interactionManager == null) return;
        List<Entity> entities = client.world.getOtherEntities(client.player, client.player.getBoundingBox().expand(3, 3, 3));
        for(Entity entity : entities){
            if(!entity.isAlive()) continue;
            if(!entity.isLiving()) continue;
            if(entity.getType() == EntityType.ARMOR_STAND) continue;
            if(entity.getType() == EntityType.PLAYER) continue;
            //client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, entity.getEyePos());
            client.interactionManager.attackEntity(client.player, entity);
            client.player.swingHand(Hand.MAIN_HAND);
            break;
        }
        //HitResult hitResult = client.crosshairTarget;
        //if(hitResult == null) return;
        //if (hitResult.getType() != HitResult.Type.ENTITY) return;
        //EntityHitResult entityHitResult = (EntityHitResult) hitResult;
        /*if(item.equals(Items.OBSIDIAN) || item.equals(Items.DIAMOND_BLOCK)) {
            client.player.swingHand(Hand.MAIN_HAND);
            client.interactionManager.updateBlockBreakingProgress(blockPos, client.player.getMovementDirection());
        }*/
    }

    public void isMaxGrow() {
        if(client.world == null) return;
        if(client.interactionManager == null) return;
        HitResult hitResult = client.crosshairTarget;
        if(hitResult == null) return;
        if (hitResult.getType() != HitResult.Type.BLOCK) return;
        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = client.world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if(!(block instanceof SweetBerryBushBlock)) return;
        int age = blockState.get(SweetBerryBushBlock.AGE);
        if(age != 3) return;
        client.player.swingHand(Hand.MAIN_HAND);
        client.interactionManager.attackBlock(blockPos, client.player.getMovementDirection());
    }

    public Logger getLogger() {
        return logger;
    }

    public static TestMod getInstance() {
        return instance;
    }

    public boolean isCanUse(){
        return canUse == -1;
    }

    public int getCanUse() {
        return canUse;
    }

    public void setCanUse(int canUse) {
        this.canUse = canUse;
    }

    public void increaseCanUse() {
        canUse++;
    }

    public boolean isCrops() {
        return crops;
    }

    public void toggleCrops(){
        this.crops = !crops;
    }

    public boolean isMobs() {
        return mobs;
    }

    public void toggleMobs() {
        this.mobs = !mobs;
    }

    public boolean isDisabled() {
        return !crops && !mobs;
    }
}
