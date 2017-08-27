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
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.common.capabilities.Capability

class ProxyEntity extends TileDataSlots with TileKeepData {
  val targetPosition = DataSlotOption[DimensionalPos]("targetPos", this).setUpdate(UpdateKind.SAVE, UpdateKind.WORLD)

  def getTarget: Option[TileEntity] = {
    for {
      DimensionalPos(pos, dimId) <- targetPosition.value
      dim <- Option(DimensionManager.getWorld(dimId)) if dim.isBlockLoaded(pos)
      tile <- Option(dim.getTileEntity(pos))
    } yield {
      if (pos == this.pos) {
        // Prevent infinite recursion
        if (!world.isRemote)
          world.createExplosion(null, pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, 5, true)
        return None
      }
      tile
    }
  }

  override def hasCapability(capability: Capability[_], facing: EnumFacing) =
    getTarget exists (_.hasCapability(capability, facing))
  override def getCapability[T](capability: Capability[T], facing: EnumFacing) =
    getTarget match {
      case Some(te) => te.getCapability(capability, facing)
      case None => null.asInstanceOf[T]
    }
}
