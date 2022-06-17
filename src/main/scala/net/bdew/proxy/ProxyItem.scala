package net.bdew.proxy

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.Text
import net.bdew.lib.Text.pimpTextComponent
import net.bdew.lib.block.BlockPosDim
import net.bdew.lib.items.TooltipBlockItem
import net.bdew.lib.keepdata.BlockItemKeepData
import net.bdew.lib.nbt.NBT
import net.bdew.proxy.registries.Items
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.item.{ItemStack, TooltipFlag}
import net.minecraft.world.level.Level

class ProxyItem(block: ProxyBlock) extends BlockItemKeepData(block, Items.props) with TooltipBlockItem {
  def getTargetPos(stack: ItemStack): Option[BlockPosDim] = {
    if (stack.hasTag && stack.getTag.contains("data") && stack.getTag.getCompound("data").contains("targetPos"))
      stack.getTag.getCompound("data").getVal[BlockPosDim]("targetPos")
    else
      None
  }

  def setTargetPos(stack: ItemStack, pos: BlockPosDim): Unit = {
    stack.setTag(NBT("data" -> NBT("targetPos" -> pos)))
  }

  override def useOn(ctx: UseOnContext): InteractionResult = {
    if (ctx.getPlayer.isCrouching) {
      if (ctx.getLevel.isClientSide) return InteractionResult.SUCCESS
      setTargetPos(ctx.getItemInHand, BlockPosDim(ctx.getClickedPos, ctx.getLevel.dimension()))
      ctx.getPlayer.sendSystemMessage(
        Text.translate("proxy.bound",
          "%d, %d, %d".format(ctx.getClickedPos.getX, ctx.getClickedPos.getY, ctx.getClickedPos.getZ),
          ctx.getLevel.dimension.location.toString
        )
      )
      InteractionResult.CONSUME
    } else {
      if (getTargetPos(ctx.getItemInHand).isEmpty)
        InteractionResult.FAIL
      else
        super.useOn(ctx)
    }
  }

  override def getTooltip(stack: ItemStack, world: Level, flags: TooltipFlag): List[Component] = {
    super.getTooltip(stack, world, flags) :+
      (getTargetPos(stack) match {
        case Some(BlockPosDim(pos, dim)) => Text.translate("proxy.bound", "%d, %d, %d".format(pos.getX, pos.getY, pos.getZ), dim.location.toString)
        case _ => Text.translate("proxy.unbound").setColor(Text.Color.RED)
      }) :+ Text.translate("proxy.desc")
  }
}
