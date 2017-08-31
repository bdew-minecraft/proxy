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

import com.mojang.realmsclient.gui.ChatFormatting
import net.bdew.lib.Misc
import net.bdew.lib.block._
import net.minecraft.block.material.{MapColor, Material}
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.world.World

object ProxyMaterial extends Material(MapColor.OBSIDIAN)

object ProxyBlock extends BaseBlock("proxy", ProxyMaterial) with HasTE[ProxyEntity] with BlockKeepData with HasItemBlock with BlockTooltip {
  setHardness(0.5f)

  override val TEClass = classOf[ProxyEntity]
  override val itemBlockInstance = ProxyItem

  override def getTooltip(stack: ItemStack, world: World, flags: ITooltipFlag) = {
    (ProxyItem.getTargetPos(stack) match {
      case Some(DimensionalPos(pos, dim)) => List(ChatFormatting.YELLOW + Misc.toLocalF("proxy.bound", "%d, %d, %d".format(pos.getX, pos.getY, pos.getZ), dim.toString))
      case _ => List(ChatFormatting.RED + Misc.toLocal("proxy.unbound"))
    }) ++ List(ChatFormatting.BLUE + Misc.toLocal("proxy.desc"))
  }
}
