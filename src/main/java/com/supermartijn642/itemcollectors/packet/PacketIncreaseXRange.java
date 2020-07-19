package com.supermartijn642.itemcollectors.packet;

import com.supermartijn642.itemcollectors.CollectorTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class PacketIncreaseXRange extends CollectorPacket<PacketIncreaseXRange> {
    public PacketIncreaseXRange(BlockPos pos){
        super(pos);
    }

    public PacketIncreaseXRange(){
    }

    @Override
    protected void handle(PacketIncreaseXRange message, EntityPlayer player, World world, CollectorTile tile){
        tile.setRangeX(tile.rangeX + 1);
    }
}
