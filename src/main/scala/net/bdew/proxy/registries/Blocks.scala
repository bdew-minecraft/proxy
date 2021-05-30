package net.bdew.proxy.registries

import net.bdew.lib.managers.BlockManager
import net.bdew.proxy.{ProxyBlock, ProxyEntity, ProxyItem}
import net.minecraft.block.AbstractBlock.Properties
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material

object Blocks extends BlockManager(Items) {
  def proxyProps: Properties = props(Material.STONE)
    .sound(SoundType.STONE)
    .strength(2, 8)

  define("proxy", () => new ProxyBlock(proxyProps))
    .withTE(new ProxyEntity(_))
    .withItem(new ProxyItem(_))
    .register
}