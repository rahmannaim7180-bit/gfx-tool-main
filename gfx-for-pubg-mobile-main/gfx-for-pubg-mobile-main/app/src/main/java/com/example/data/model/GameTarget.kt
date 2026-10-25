package com.example.data.model

data class GameTarget(
    val id: String,
    val packageName: String,
    val displayName: String,
    val variantLabel: String,
    val isInstalled: Boolean = false,
    val installedVersion: String? = null,
    val isVerifiedTarget: Boolean = true,
    val targetEngine: String = "Unreal Engine 4"
) {
    companion object {
        val SUPPORTED_GAMES = listOf(
            GameTarget(
                id = "pubg_global",
                packageName = "com.tencent.ig",
                displayName = "PUBG MOBILE",
                variantLabel = "Global Version",
                targetEngine = "Unreal Engine 4"
            ),
            GameTarget(
                id = "bgmi",
                packageName = "com.pubg.imobile",
                displayName = "BATTLEGROUNDS MOBILE INDIA",
                variantLabel = "BGMI (India)",
                targetEngine = "Unreal Engine 4"
            ),
            GameTarget(
                id = "pubg_kr",
                packageName = "com.pubg.krmobile",
                displayName = "PUBG MOBILE KR",
                variantLabel = "Korea / Japan",
                targetEngine = "Unreal Engine 4"
            ),
            GameTarget(
                id = "pubg_lite",
                packageName = "com.tencent.iglite",
                displayName = "PUBG MOBILE LITE",
                variantLabel = "Global Lite",
                targetEngine = "Unreal Engine 4"
            ),
            GameTarget(
                id = "pubg_vn",
                packageName = "com.vng.pubgmobile",
                displayName = "PUBG MOBILE VN",
                variantLabel = "Vietnam VNG",
                targetEngine = "Unreal Engine 4"
            ),
            GameTarget(
                id = "freefire_max",
                packageName = "com.dts.freefiremax",
                displayName = "Free Fire MAX",
                variantLabel = "MAX Version",
                targetEngine = "Unity"
            )
        )
    }
}
