package com.radonshadow.focusdrift.domain.model

enum class ShopCategory {
    ORB_SKINS,
    THEMES,
    SOUNDS
}

data class ShopItem(
    val id: String,
    val name: String,
    val category: ShopCategory,
    val description: String,
    val costCoins: Int,
    val previewColor: String? = null,
    val glowColor: String? = null,
    val primaryColor: String? = null,
    val accentColor: String? = null,
    val isSpecialEvent: Boolean = false
)

enum class OrbSkin(val id: String, val displayName: String, val primaryHex: String, val glowHex: String) {
    DEFAULT("orb_default", "Default", "#7C6FE0", "#7C6FE0"),
    DARK_MATTER("orb_dark_matter", "Dark Matter", "#1A1A2E", "#E040FB"),
    SOLAR_FLARE("orb_solar_flare", "Solar Flare", "#FF6D00", "#FFAB40");

    companion object {
        fun fromId(id: String): OrbSkin = entries.find { it.id == id } ?: DEFAULT
    }
}
