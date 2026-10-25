package com.example.data.model

data class NetworkTestEndpoint(
    val id: String,
    val name: String,
    val host: String,
    val port: Int = 53,
    val description: String,
    val isDefault: Boolean = false
) {
    companion object {
        val PRESET_ENDPOINTS = listOf(
            NetworkTestEndpoint(
                id = "cloudflare",
                name = "Cloudflare Anycast (1.1.1.1)",
                host = "1.1.1.1",
                port = 53,
                description = "Ultra low-latency global anycast edge",
                isDefault = true
            ),
            NetworkTestEndpoint(
                id = "google",
                name = "Google Public Edge (8.8.8.8)",
                host = "8.8.8.8",
                port = 53,
                description = "High-availability global tier-1 backbone"
            ),
            NetworkTestEndpoint(
                id = "opendns",
                name = "Cisco OpenDNS (208.67.222.222)",
                host = "208.67.222.222",
                port = 53,
                description = "Reliable enterprise edge DNS routing"
            )
        )
    }
}

data class NetworkPingResult(
    val endpoint: NetworkTestEndpoint,
    val isSuccessful: Boolean,
    val latencyMs: Long,
    val dnsLookupMs: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val errorMessage: String? = null
)

data class NetworkQualityReport(
    val activeTransport: String = "Wi-Fi (5GHz)",
    val isConnected: Boolean = true,
    val linkDownstreamMbps: Int = 450,
    val linkUpstreamMbps: Int = 120,
    val averageLatencyMs: Float = 24.5f,
    val minLatencyMs: Long = 18L,
    val maxLatencyMs: Long = 34L,
    val jitterMs: Float = 2.1f,
    val packetLossPct: Float = 0.0f,
    val dnsResolutionMs: Long = 12L,
    val isTestingActive: Boolean = false,
    val recentPings: List<NetworkPingResult> = emptyList(),
    val connectionStabilityRating: String = "Optimal (Esports Ready)",
    val recommendations: List<String> = listOf(
        "Current Wi-Fi connection latency and jitter are within optimal esports margins (<30ms).",
        "Transparent Note: Diagnostic endpoint ping measures physical network transport latency to the edge; actual PUBG Mobile in-match ping depends on Krafton/Tencent game match server location."
    )
)
