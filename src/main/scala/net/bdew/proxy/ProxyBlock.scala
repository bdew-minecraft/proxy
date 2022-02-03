package net.bdew.proxy

import net.bdew.lib.block._
import net.bdew.lib.keepdata.BlockKeepData
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

class ProxyBlock(props: BlockBehaviour.Properties) extends Block(props) with HasTE[ProxyEntity] with BlockKeepData
