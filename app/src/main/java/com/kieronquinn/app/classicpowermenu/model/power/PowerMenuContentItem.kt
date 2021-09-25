package com.kieronquinn.app.classicpowermenu.model.power

sealed class PowerMenuContentItem(val itemType: PowerMenuContentItemType) {
    object Cards : PowerMenuContentItem(PowerMenuContentItemType.CARDS)
    object Controls : PowerMenuContentItem(PowerMenuContentItemType.CONTROLS)
}

enum class PowerMenuContentItemType {
    CARDS, CONTROLS
}