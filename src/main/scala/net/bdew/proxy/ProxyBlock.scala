package net.bdew.proxy

import net.bdew.lib.block._
import net.bdew.lib.keepdata.BlockKeepData
import net.minecraft.block.{AbstractBlock, Block}

class ProxyBlock(props: AbstractBlock.Properties) extends Block(props) with HasTE[ProxyEntity] with BlockKeepData
