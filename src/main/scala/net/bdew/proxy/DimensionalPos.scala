package net.bdew.proxy

import net.bdew.lib.nbt.Type.TInt
import net.bdew.lib.nbt.{ConvertedType, NBT}
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.{RegistryKey, ResourceLocation}
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants

case class DimensionalPos(pos: BlockPos, dim: RegistryKey[World])

object DimensionalPos {
  implicit object TDimPos extends ConvertedType[DimensionalPos, CompoundNBT] {
    override def encode(v: DimensionalPos): CompoundNBT =
      NBT(
        "x" -> v.pos.getX,
        "y" -> v.pos.getY,
        "z" -> v.pos.getZ,
        "d" -> v.dim.location.toString
      )

    override def decode(t: CompoundNBT): Option[DimensionalPos] =
      if (t.contains("x", Constants.NBT.TAG_INT) && t.contains("y", Constants.NBT.TAG_INT) && t.contains("z", Constants.NBT.TAG_INT) && t.contains("d", Constants.NBT.TAG_STRING))
        Some(DimensionalPos(
          new BlockPos(t.getInt("x"), t.getInt("y"), t.getInt("z")),
          RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(t.getString("d")))
        ))
      else
        None
  }
}
