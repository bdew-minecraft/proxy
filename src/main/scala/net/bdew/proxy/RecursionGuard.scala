package net.bdew.proxy


import net.bdew.lib.Text
import net.bdew.lib.Text.pimpTextComponent
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.minecraft.world.{Explosion, World}

import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.DynamicVariable

object RecursionGuard {
  val recursionSet = new DynamicVariable(Set.empty[BlockPos])

  def withGuard[T](world: World, pos: BlockPos, invalidResult: T)(f: => T): T = {
    if (recursionSet.value.contains(pos)) {
      if (!world.isClientSide) {
        val toBlow = recursionSet.value
        for (player <- world.players().asScala if toBlow.exists(block => player.distanceToSqr(block.getX, block.getY, block.getZ) <= 25D))
          player.sendMessage(Text.translate("proxy.loop").setColor(Text.Color.RED), Util.NIL_UUID)
        for (p <- toBlow)
          world.explode(null, p.getX + 0.5, p.getY + 0.5, p.getZ + 0.5, 5, Explosion.Mode.BREAK)
      }
      invalidResult
    } else recursionSet.withValue(recursionSet.value + pos)(f)
  }
}
