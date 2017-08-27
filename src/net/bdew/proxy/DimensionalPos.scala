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

import net.bdew.lib.nbt.Type.TInt
import net.bdew.lib.nbt.{ConvertedType, NBT}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos

case class DimensionalPos(pos: BlockPos, dim: Int)

object DimensionalPos {

  implicit object TDimPos extends ConvertedType[DimensionalPos, NBTTagCompound] {
    override def encode(v: DimensionalPos) =
      NBT(
        "x" -> v.pos.getX,
        "y" -> v.pos.getY,
        "z" -> v.pos.getZ,
        "d" -> v.dim
      )

    override def decode(t: NBTTagCompound) =
      if (t.hasKey("x", TInt.id) && t.hasKey("y", TInt.id) && t.hasKey("z", TInt.id) && t.hasKey("d", TInt.id))
        Some(DimensionalPos(new BlockPos(t.getInteger("x"), t.getInteger("y"), t.getInteger("z")), t.getInteger("d")))
      else
        None
  }

}
