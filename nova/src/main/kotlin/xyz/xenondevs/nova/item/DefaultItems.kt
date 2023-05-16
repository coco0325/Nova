@file:Suppress("unused")

package xyz.xenondevs.nova.item

import net.minecraft.resources.ResourceLocation
import xyz.xenondevs.nova.initialize.InitializationStage
import xyz.xenondevs.nova.initialize.InternalInit
import xyz.xenondevs.nova.item.behavior.ItemBehaviorHolder
import xyz.xenondevs.nova.item.behavior.impl.WrenchBehavior
import xyz.xenondevs.nova.item.logic.ItemLogic
import xyz.xenondevs.nova.registry.NovaRegistries
import xyz.xenondevs.nova.util.set

@InternalInit(stage = InitializationStage.PRE_WORLD)
object DefaultItems {
    
    val WRENCH = registerCoreItem("wrench", WrenchBehavior)
    
}

@InternalInit(stage = InitializationStage.PRE_WORLD)
object DefaultGuiItems {
    
    val ANVIL_OVERLAY_ARROW = registerUnnamedHiddenCoreItem("gui_anvil_overlay_arrow")
    val ANVIL_OVERLAY_ARROW_LEFT = registerUnnamedHiddenCoreItem("gui_anvil_overlay_arrow_left")
    val ANVIL_OVERLAY_PLUS = registerUnnamedHiddenCoreItem("gui_anvil_overlay_plus")
    val ARROW_1_DOWN = registerUnnamedHiddenCoreItem("gui_arrow_1_down")
    val ARROW_1_LEFT = registerUnnamedHiddenCoreItem("gui_arrow_1_left")
    val ARROW_1_RIGHT = registerUnnamedHiddenCoreItem("gui_arrow_1_right")
    val ARROW_1_UP = registerUnnamedHiddenCoreItem("gui_arrow_1_up")
    val ARROW_2_DOWN = registerUnnamedHiddenCoreItem("gui_arrow_2_down")
    val ARROW_2_LEFT = registerUnnamedHiddenCoreItem("gui_arrow_2_left")
    val ARROW_2_RIGHT = registerUnnamedHiddenCoreItem("gui_arrow_2_right")
    val ARROW_2_UP = registerUnnamedHiddenCoreItem("gui_arrow_2_up")
    val CHECKMARK = registerUnnamedHiddenCoreItem("gui_checkmark")
    val CORNER_BOTTOM_LEFT = registerUnnamedHiddenCoreItem("gui_corner_bottom_left")
    val CORNER_BOTTOM_RIGHT = registerUnnamedHiddenCoreItem("gui_corner_bottom_right")
    val CORNER_TOP_LEFT = registerUnnamedHiddenCoreItem("gui_corner_top_left")
    val CORNER_TOP_RIGHT = registerUnnamedHiddenCoreItem("gui_corner_top_right")
    val GREEN_CHECKMARK = registerUnnamedHiddenCoreItem("gui_green_checkmark")
    val HORIZONTAL_DOTS = registerUnnamedHiddenCoreItem("gui_horizontal_dots")
    val HORIZONTAL_DOWN = registerUnnamedHiddenCoreItem("gui_horizontal_down")
    val HORIZONTAL_LINE = registerUnnamedHiddenCoreItem("gui_horizontal_line")
    val HORIZONTAL_UP = registerUnnamedHiddenCoreItem("gui_horizontal_up")
    val INVENTORY_PART = registerUnnamedHiddenCoreItem("gui_inventory_part")
    val INVISIBLE_ITEM = registerUnnamedHiddenCoreItem("gui_invisible")
    val LIGHT_ARROW_1_DOWN = registerUnnamedHiddenCoreItem("gui_light_arrow_1_down")
    val LIGHT_ARROW_1_LEFT = registerUnnamedHiddenCoreItem("gui_light_arrow_1_left")
    val LIGHT_ARROW_1_RIGHT = registerUnnamedHiddenCoreItem("gui_light_arrow_1_right")
    val LIGHT_ARROW_1_UP = registerUnnamedHiddenCoreItem("gui_light_arrow_1_up")
    val LIGHT_ARROW_2_DOWN = registerUnnamedHiddenCoreItem("gui_light_arrow_2_down")
    val LIGHT_ARROW_2_LEFT = registerUnnamedHiddenCoreItem("gui_light_arrow_2_left")
    val LIGHT_ARROW_2_RIGHT = registerUnnamedHiddenCoreItem("gui_light_arrow_2_right")
    val LIGHT_ARROW_2_UP = registerUnnamedHiddenCoreItem("gui_light_arrow_2_up")
    val LIGHT_CHECKMARK = registerUnnamedHiddenCoreItem("gui_light_checkmark")
    val LIGHT_CORNER_BOTTOM_LEFT = registerUnnamedHiddenCoreItem("gui_light_corner_bottom_left")
    val LIGHT_CORNER_BOTTOM_RIGHT = registerUnnamedHiddenCoreItem("gui_light_corner_bottom_right")
    val LIGHT_CORNER_TOP_LEFT = registerUnnamedHiddenCoreItem("gui_light_corner_top_left")
    val LIGHT_CORNER_TOP_RIGHT = registerUnnamedHiddenCoreItem("gui_light_corner_top_right")
    val LIGHT_HORIZONTAL_DOTS = registerUnnamedHiddenCoreItem("gui_light_horizontal_dots")
    val LIGHT_HORIZONTAL_DOWN = registerUnnamedHiddenCoreItem("gui_light_horizontal_down")
    val LIGHT_HORIZONTAL_LINE = registerUnnamedHiddenCoreItem("gui_light_horizontal_line")
    val LIGHT_HORIZONTAL_UP = registerUnnamedHiddenCoreItem("gui_light_horizontal_up")
    val LIGHT_VERTICAL_DOTS = registerUnnamedHiddenCoreItem("gui_light_vertical_dots")
    val LIGHT_VERTICAL_HORIZONTAL = registerUnnamedHiddenCoreItem("gui_light_vertical_horizontal")
    val LIGHT_VERTICAL_LEFT = registerUnnamedHiddenCoreItem("gui_light_vertical_left")
    val LIGHT_VERTICAL_LINE = registerUnnamedHiddenCoreItem("gui_light_vertical_line")
    val LIGHT_VERTICAL_RIGHT = registerUnnamedHiddenCoreItem("gui_light_vertical_right")
    val LIGHT_X = registerUnnamedHiddenCoreItem("gui_light_x")
    val RED_X = registerUnnamedHiddenCoreItem("gui_red_x")
    val VERTICAL_DOTS = registerUnnamedHiddenCoreItem("gui_vertical_dots")
    val VERTICAL_HORIZONTAL = registerUnnamedHiddenCoreItem("gui_vertical_horizontal")
    val VERTICAL_LEFT = registerUnnamedHiddenCoreItem("gui_vertical_left")
    val VERTICAL_LINE = registerUnnamedHiddenCoreItem("gui_vertical_line")
    val VERTICAL_RIGHT = registerUnnamedHiddenCoreItem("gui_vertical_right")
    val X = registerUnnamedHiddenCoreItem("gui_x")
    val AREA_BTN_OFF = registerUnnamedHiddenCoreItem("gui_area_btn_off", "menu.nova.visual_region.show")
    val AREA_BTN_ON = registerUnnamedHiddenCoreItem("gui_area_btn_on", "menu.nova.visual_region.hide")
    val MINUS_BTN_OFF = registerUnnamedHiddenCoreItem("gui_minus_btn_off")
    val MINUS_BTN_ON = registerUnnamedHiddenCoreItem("gui_minus_btn_on")
    val PLUS_BTN_OFF = registerUnnamedHiddenCoreItem("gui_plus_btn_off")
    val PLUS_BTN_ON = registerUnnamedHiddenCoreItem("gui_plus_btn_on")
    val UPGRADES_BTN = registerUnnamedHiddenCoreItem("gui_upgrades_btn", "menu.nova.upgrades")
    val SIDE_CONFIG_BTN = registerUnnamedHiddenCoreItem("gui_side_config_btn", "menu.nova.side_config")
    val ENERGY_BTN_OFF = registerUnnamedHiddenCoreItem("gui_energy_btn_off", "menu.nova.side_config.energy")
    val ENERGY_BTN_ON = registerUnnamedHiddenCoreItem("gui_energy_btn_on", "menu.nova.side_config.energy")
    val ENERGY_BTN_SELECTED = registerUnnamedHiddenCoreItem("gui_energy_btn_selected", "menu.nova.side_config.energy")
    val ITEM_BTN_OFF = registerUnnamedHiddenCoreItem("gui_items_btn_off", "menu.nova.side_config.items")
    val ITEM_BTN_ON = registerUnnamedHiddenCoreItem("gui_items_btn_on", "menu.nova.side_config.items")
    val ITEM_BTN_SELECTED = registerUnnamedHiddenCoreItem("gui_items_btn_selected", "menu.nova.side_config.items")
    val FLUID_BTN_OFF = registerUnnamedHiddenCoreItem("gui_fluids_btn_off", "menu.nova.side_config.fluids")
    val FLUID_BTN_ON = registerUnnamedHiddenCoreItem("gui_fluids_btn_on", "menu.nova.side_config.fluids")
    val FLUID_BTN_SELECTED = registerUnnamedHiddenCoreItem("gui_fluids_btn_selected", "menu.nova.side_config.fluids")
    val SIMPLE_MODE_BTN_OFF = registerUnnamedHiddenCoreItem("gui_simple_mode_btn_off", "menu.nova.side_config.simple_mode")
    val SIMPLE_MODE_BTN_ON = registerUnnamedHiddenCoreItem("gui_simple_mode_btn_on", "menu.nova.side_config.simple_mode")
    val ADVANCED_MODE_BTN_OFF = registerUnnamedHiddenCoreItem("gui_advanced_mode_btn_off", "menu.nova.side_config.advanced_mode")
    val ADVANCED_MODE_BTN_ON = registerUnnamedHiddenCoreItem("gui_advanced_mode_btn_on", "menu.nova.side_config.advanced_mode")
    val BLUE_BTN = registerUnnamedHiddenCoreItem("gui_blue_btn")
    val GRAY_BTN = registerUnnamedHiddenCoreItem("gui_gray_btn")
    val GREEN_BTN = registerUnnamedHiddenCoreItem("gui_green_btn")
    val ORANGE_BTN = registerUnnamedHiddenCoreItem("gui_orange_btn")
    val PINK_BTN = registerUnnamedHiddenCoreItem("gui_pink_btn")
    val RED_BTN = registerUnnamedHiddenCoreItem("gui_red_btn")
    val WHITE_BTN = registerUnnamedHiddenCoreItem("gui_white_btn")
    val YELLOW_BTN = registerUnnamedHiddenCoreItem("gui_yellow_btn")
    val BAR_BLUE = registerUnnamedHiddenCoreItem("gui_bar_blue")
    val BAR_GREEN = registerUnnamedHiddenCoreItem("gui_bar_green")
    val BAR_RED = registerUnnamedHiddenCoreItem("gui_bar_red")
    val BAR_ORANGE = registerUnnamedHiddenCoreItem("gui_bar_orange")
    val TP_BAR_BLUE = registerUnnamedHiddenCoreItem("gui_tp_bar_blue")
    val TP_BAR_GREEN = registerUnnamedHiddenCoreItem("gui_tp_bar_green")
    val TP_BAR_RED = registerUnnamedHiddenCoreItem("gui_tp_bar_red")
    val TP_BAR_ORANGE = registerUnnamedHiddenCoreItem("gui_tp_bar_orange")
    val NUMBER = registerUnnamedHiddenCoreItem("gui_number")
    val MINUS = registerUnnamedHiddenCoreItem("gui_minus")
    val TP_SEARCH = registerUnnamedHiddenCoreItem("gui_tp_search")
    val TP_ARROW_LEFT_BTN_OFF = registerUnnamedHiddenCoreItem("gui_tp_arrow_left_btn_off")
    val TP_ARROW_LEFT_BTN_ON = registerUnnamedHiddenCoreItem("gui_tp_arrow_left_btn_on")
    val TP_ARROW_RIGHT_BTN_OFF = registerUnnamedHiddenCoreItem("gui_tp_arrow_right_btn_off")
    val TP_ARROW_RIGHT_BTN_ON = registerUnnamedHiddenCoreItem("gui_tp_arrow_right_btn_on")
    val TP_STOPWATCH = registerUnnamedHiddenCoreItem("gui_tp_stopwatch")
    val TP_COLOR_PICKER = registerUnnamedHiddenCoreItem("gui_tp_color_picker")
    val TP_PIXEL_ARROW_LEFT_OFF = registerUnnamedHiddenCoreItem("gui_tp_pixel_arrow_left_off")
    val TP_PIXEL_ARROW_LEFT_ON = registerUnnamedHiddenCoreItem("gui_tp_pixel_arrow_left_on")
    val TP_PIXEL_ARROW_RIGHT_OFF = registerUnnamedHiddenCoreItem("gui_tp_pixel_arrow_right_off")
    val TP_PIXEL_ARROW_RIGHT_ON = registerUnnamedHiddenCoreItem("gui_tp_pixel_arrow_right_on")
    
}

@InternalInit(stage = InitializationStage.PRE_WORLD)
object DefaultBlockOverlays {
    
    val BREAK_STAGE_OVERLAY = registerUnnamedHiddenCoreItem("break_stage_overlay")
    val TRANSPARENT_BLOCK = registerUnnamedHiddenCoreItem("transparent_block")
    
}

private fun registerCoreItem(
    name: String,
    vararg itemBehaviors: ItemBehaviorHolder<*>,
    localizedName: String = "item.nova.$name",
    isHidden: Boolean = false
): NovaItem = register(NovaItem(
    ResourceLocation("nova", name),
    localizedName,
    ItemLogic(*itemBehaviors),
    isHidden = isHidden
))

private fun registerUnnamedHiddenCoreItem(
    name: String,
    localizedName: String = "",
    vararg itemBehaviors: ItemBehaviorHolder<*>
): NovaItem = register(NovaItem(
    ResourceLocation("nova", name),
    localizedName,
    ItemLogic(*itemBehaviors),
    isHidden = true
))

private fun register(item: NovaItem): NovaItem =
    item.apply { NovaRegistries.ITEM[id] = this }