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

package net.bdew.proxy.waila

import java.util
import java.util.Collections

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor, IWailaDataProvider}
import net.bdew.lib.Misc
import net.bdew.proxy.{DimensionalPos, ProxyEntity}
import net.minecraft.item.ItemStack

object ProxyDataProvider extends IWailaDataProvider {
  override def getWailaBody(itemStack: ItemStack, currenttip: util.List[String], accessor: IWailaDataAccessor, config: IWailaConfigHandler) = {
    accessor.getTileEntity match {
      case proxy: ProxyEntity =>
        proxy.targetPosition.value match {
          case Some(DimensionalPos(pos, dim)) => Collections.singletonList(Misc.toLocalF("proxy.bound", "%d, %d, %d".format(pos.getX, pos.getY, pos.getZ), dim.toString))
          case _ => Collections.singletonList(Misc.toLocal("proxy.unbound"))
        }
      case _ => util.Collections.emptyList()
    }
  }
}
