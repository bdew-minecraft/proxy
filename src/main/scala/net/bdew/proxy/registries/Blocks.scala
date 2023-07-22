package net.bdew.proxy.registries

import net.bdew.lib.managers.BlockManager
import net.bdew.proxy.{ProxyBlock, ProxyEntity, ProxyItem}
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.material.MapColor

object Blocks extends BlockManager(Items) {
  def proxyProps: Properties =
    props
      .mapColor(MapColor.STONE)
      .sound(SoundType.STONE)
      .strength(2, 8)

  define("proxy", () => new ProxyBlock(proxyProps))
    .withTE(new ProxyEntity(_, _, _))
    .withItem(new ProxyItem(_))
    .register
}