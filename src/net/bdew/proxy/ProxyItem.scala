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

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.block.ItemBlockKeepData
import net.bdew.lib.nbt.NBT
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World

object ProxyItem extends ItemBlockKeepData(ProxyBlock) {
  def getTargetPos(stack: ItemStack): Option[DimensionalPos] = {
    if (stack.hasTagCompound && stack.getTagCompound.hasKey("data") && stack.getTagCompound.getCompoundTag("data").hasKey("targetPos"))
      stack.getTagCompound.getCompoundTag("data").get[DimensionalPos]("targetPos")
    else
      None
  }

  def setTargetPos(stack: ItemStack, pos: DimensionalPos): Unit = {
    stack.setTagCompound(NBT("data" -> NBT("targetPos" -> pos)))
  }

  override def onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) = {
    if (!player.isSneaking) {
      if (getTargetPos(player.getHeldItem(hand)).isEmpty)
        EnumActionResult.FAIL
      else
        super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ)
    } else {
      setTargetPos(player.getHeldItem(hand), DimensionalPos(pos, player.dimension))
      import net.bdew.lib.helpers.ChatHelper._
      player.sendStatusMessage(L("proxy.bound", "%d, %d, %d".format(pos.getX, pos.getY, pos.getZ), player.dimension.toString), true)
      EnumActionResult.SUCCESS
    }
  }
}
