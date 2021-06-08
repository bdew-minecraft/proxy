package net.bdew.proxy

import net.bdew.lib.block.BlockPosDim
import net.bdew.lib.data.DataSlotOption
import net.bdew.lib.data.base.{TileDataSlots, UpdateKind}
import net.bdew.lib.keepdata.TileKeepData
import net.bdew.lib.tile.TileExtended
import net.minecraft.tileentity.{TileEntity, TileEntityType}
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional

class ProxyEntity(teType: TileEntityType[_]) extends TileExtended(teType) with TileDataSlots with TileKeepData {
  val targetPosition: DataSlotOption[BlockPosDim] = DataSlotOption[BlockPosDim]("targetPos", this)
    .setUpdate(UpdateKind.SAVE, UpdateKind.WORLD)

  def getTarget: Option[TileEntity] = {
    if (level.isClientSide) return None
    for {
      target <- targetPosition.value
      targetWorld <- target.world(level.getServer) if targetWorld.isLoaded(target.pos)
      tile <- Option(targetWorld.getBlockEntity(target.pos))
    } yield tile
  }

  override def getCapability[T](cap: Capability[T], side: Direction): LazyOptional[T] =
    RecursionGuard.withGuard(getLevel, getBlockPos, LazyOptional.empty[T]) {
      getTarget match {
        case Some(te) => te.getCapability(cap, side)
        case None => LazyOptional.empty[T]
      }
    }
}
