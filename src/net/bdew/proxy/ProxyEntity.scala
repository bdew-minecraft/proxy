/*
 * Copyright (c) bdew, 2017
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.bdew.proxy

import net.bdew.lib.block.TileKeepData
import net.bdew.lib.data.DataSlotOption
import net.bdew.lib.data.base.{TileDataSlots, UpdateKind}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraftforge.common.ForgeChunkManager.Ticket
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.{DimensionManager, ForgeChunkManager}

class ProxyEntity extends TileDataSlots with TileKeepData {
  val targetPosition = DataSlotOption[DimensionalPos]("targetPos", this).setUpdate(UpdateKind.SAVE, UpdateKind.WORLD)
  var ticket: Ticket = _

  def getWorldFromId(id: Int, force: Boolean): Option[World] = {
    require(!(world.isRemote & force), "Force can't be used in client context")
    if (id == world.provider.getDimension) return Some(world)
    if (force) {
      val w = Option(world.getMinecraftServer.getWorld(id))
      if (w.isEmpty)
        Proxy.logWarn(s"Force load of dimension $id failed")
      w
    } else Option(DimensionManager.getWorld(id))
  }

  def startChunkLoading(): Unit = {
    if (ticket != null) stopChunkLoading()
    for {
      DimensionalPos(pos, dimId) <- targetPosition.value
      targetWorld <- getWorldFromId(dimId, true)
    } {
      ticket = ForgeChunkManager.requestTicket(Proxy, targetWorld, ForgeChunkManager.Type.NORMAL)
      Proxy.logInfo(s"Got ticket for ${this.pos} to load ${targetPosition.value}")
      if (ticket != null)
        ForgeChunkManager.forceChunk(ticket, new ChunkPos(pos.getX >> 4, pos.getZ >> 4))
    }
  }

  def stopChunkLoading(): Unit = {
    Proxy.logInfo("Releasing ticket")
    ForgeChunkManager.releaseTicket(ticket)
    ticket = null
  }

  override def doLoad(kind: UpdateKind.Value, t: NBTTagCompound): Unit = {
    super.doLoad(kind, t)
    if (kind == UpdateKind.SAVE && !world.isRemote) {
      Proxy.logInfo(s"Load $pos")
      startChunkLoading()
    }
  }

  override def onChunkUnload(): Unit = {
    Proxy.logInfo(s"Unload $pos")
    if (ticket != null) stopChunkLoading()
  }

  override def invalidate(): Unit = {
    Proxy.logInfo(s"Invalidate $pos")
    if (ticket != null) stopChunkLoading()
    super.invalidate()
  }


  override def setWorldCreate(world: World) = {
    this.world = world
  }

  def getTarget: Option[TileEntity] = {
    for {
      DimensionalPos(pos, dimId) <- targetPosition.value
      dim <- getWorldFromId(dimId, false)
      tile <- Option(dim.getTileEntity(pos))
    } yield tile
  }

  override def hasCapability(capability: Capability[_], facing: EnumFacing) =
    RecursionGuard.withGuard(world, pos, false) {
      getTarget exists (_.hasCapability(capability, facing))
    }

  override def getCapability[T](capability: Capability[T], facing: EnumFacing) =
    RecursionGuard.withGuard(world, pos, null.asInstanceOf[T]) {
      getTarget match {
        case Some(te) => te.getCapability(capability, facing)
        case None => null.asInstanceOf[T]
      }
    }
}
