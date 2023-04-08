package com.francobm.testmod.mixin;

import com.francobm.testmod.TestMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class MixinSB {

    @Inject(at = @At("HEAD"), method = "onBreak")
    private void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci){
        if(!TestMod.getInstance().isCrops()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.player == null) return;
        if(!client.player.getUuid().equals(player.getUuid())) return;
        HitResult hitResult = client.crosshairTarget;
        if(hitResult == null) return;
        if (hitResult.getType() != HitResult.Type.BLOCK) return;
        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        PlayerInteractBlockC2SPacket blockC2SPacket = new PlayerInteractBlockC2SPacket(Hand.OFF_HAND, blockHitResult);
        client.getNetworkHandler().sendPacket(blockC2SPacket);
    }


}
