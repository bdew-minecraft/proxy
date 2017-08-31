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


import net.bdew.lib.helpers.ChatHelper._
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

import scala.collection.JavaConversions._
import scala.util.DynamicVariable

object RecursionGuard {
  val recursionSet = new DynamicVariable(Set.empty[BlockPos])

  def withGuard[T](world: World, pos: BlockPos, invalidResult: T)(f: => T): T = {
    if (recursionSet.value.contains(pos)) {
      if (!world.isRemote) {
        val toBlow = recursionSet.value
        for (player <- world.playerEntities if toBlow.exists(block => player.getDistanceSq(block) <= 25D))
          player.sendMessage(L("proxy.loop").setColor(Color.RED))
        for (p <- toBlow)
          world.createExplosion(null, p.getX + 0.5, p.getY + 0.5, p.getZ + 0.5, 5, true)
      }
      invalidResult
    }
    recursionSet.withValue(recursionSet.value + pos)(f)
  }
}
