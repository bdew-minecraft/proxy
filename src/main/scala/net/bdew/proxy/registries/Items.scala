package net.bdew.proxy.registries

import net.bdew.lib.managers.ItemManager
import net.minecraft.world.item.CreativeModeTabs

object Items extends ItemManager {
  creativeTabs.addToTab(CreativeModeTabs.FUNCTIONAL_BLOCKS, all)
}
