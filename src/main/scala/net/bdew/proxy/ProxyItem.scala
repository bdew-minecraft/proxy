package net.bdew.proxy

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.Text
import net.bdew.lib.Text.pimpTextComponent
import net.bdew.lib.items.TooltipBlockItem
import net.bdew.lib.keepdata.BlockItemKeepData
import net.bdew.lib.nbt.NBT
import net.bdew.proxy.registries.Items
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.{ItemStack, ItemUseContext}
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.{ActionResultType, Util}
import net.minecraft.world.World

class ProxyItem(block: ProxyBlock) extends BlockItemKeepData(block, Items.props) with TooltipBlockItem {
  def getTargetPos(stack: ItemStack): Option[DimensionalPos] = {
    if (stack.hasTag && stack.getTag.contains("data") && stack.getTag.getCompound("data").contains("targetPos"))
      stack.getTag.getCompound("data").getVal[DimensionalPos]("targetPos")
    else
      None
  }

  def setTargetPos(stack: ItemStack, pos: DimensionalPos): Unit = {
    stack.setTag(NBT("data" -> NBT("targetPos" -> pos)))
  }

  override def useOn(ctx: ItemUseContext): ActionResultType = {
    if (ctx.getPlayer.isCrouching) {
      if (ctx.getLevel.isClientSide) return ActionResultType.SUCCESS
      setTargetPos(ctx.getItemInHand, DimensionalPos(ctx.getClickedPos, ctx.getLevel.dimension()))
      ctx.getPlayer.sendMessage(
        Text.translate("proxy.bound",
          "%d, %d, %d".format(ctx.getClickedPos.getX, ctx.getClickedPos.getY, ctx.getClickedPos.getZ),
          ctx.getLevel.dimension.location.toString
        ), Util.NIL_UUID
      )
      ActionResultType.CONSUME
    } else {
      if (getTargetPos(ctx.getItemInHand).isEmpty)
        ActionResultType.FAIL
      else
        super.useOn(ctx)
    }
  }

  override def getTooltip(stack: ItemStack, world: World, flags: ITooltipFlag): List[ITextComponent] = {
    super.getTooltip(stack, world, flags) :+
      (getTargetPos(stack) match {
        case Some(DimensionalPos(pos, dim)) => Text.translate("proxy.bound", "%d, %d, %d".format(pos.getX, pos.getY, pos.getZ), dim.location.toString)
        case _ => Text.translate("proxy.unbound").setColor(Text.Color.RED)
      }) :+ Text.translate("proxy.desc")
  }
}
