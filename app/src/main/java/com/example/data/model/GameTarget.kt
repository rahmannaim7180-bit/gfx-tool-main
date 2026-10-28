package com.example.data.model

data class GameTarget(
    val id: String,
    val displayName: String,
    val packageName: String,
    val variantLabel: String,
    val isInstalled: Boolean = false,
    val installedVersion: String? = null,
    val isVerifiedTarget: Boolean = true,
    val targetEngine: String = "Unreal Engine 4"
) {
    companion object {
        val SUPPORTED_GAMES: List<GameTarget> = listOf(
            GameTarget(
                id = "pubg_global",
                displayName = "PUBG MOBILE",
                packageName = "com.tencent.ig",
                variantLabel = "Global",
                isVerifiedTarget = true,
                targetEngine = "Unreal Engine 4"
            ),
            GameTarget(
                id = "bgmi",
                displayName = "BATTLEGROUNDS MOBILE INDIA",
                packageName = "com.pubg.imobile",
                variantLabel = "India (BGMI)",
                isVerifiedTarget = true,
                targetEngine = "Unreal Engine 4"
            ),
            GameTarget(
                id = "pubg_kr",
                displayName = "PUBG MOBILE KR",
                packageName = "com.pubg.krmobile",
                variantLabel = "Korea/Japan",
                isVerifiedTarget = true,
                targetEngine = "Unreal Engine 4"
            ),
            GameTarget(
                id = "pubg_vn",
                displayName = "PUBG MOBILE VN",
                packageName = "com.vng.pubgmobile",
                variantLabel = "Vietnam",
                isVerifiedTarget = true,
                targetEngine = "Unreal Engine 4"
            ),
            GameTarget(
                id = "pubg_lite",
                displayName = "PUBG MOBILE LITE",
                packageName = "com.tencent.iglite",
                variantLabel = "Lite",
                isVerifiedTarget = false,
                targetEngine = "Unreal Engine 4"
            ),
            GameTarget(
                id = "free_fire_max",
                displayName = "Free Fire MAX",
                packageName = "com.dts.freefiremax",
                variantLabel = "Global",
                isVerifiedTarget = false,
                targetEngine = "Unity"
            )
        )
    }
}
