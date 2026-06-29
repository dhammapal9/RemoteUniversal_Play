package com.hari.androidtvremote.ui.app

import androidx.compose.ui.graphics.Color

enum class RemoteShelfMode(
    val storageValue: String,
    val label: String,
    val description: String
) {
    Applications(
        storageValue = "applications",
        label = "Applications",
        description = "Show quick-launch apps above the remote"
    ),
    MediaButtons(
        storageValue = "media",
        label = "Media buttons",
        description = "Show playback controls above the remote"
    ),
    None(
        storageValue = "none",
        label = "None",
        description = "Hide the top strip on the remote"
    );

    companion object {
        fun fromStorage(value: String?): RemoteShelfMode? {
            return entries.firstOrNull { it.storageValue == value }
        }
    }
}

data class RemoteShortcutApp(
    val id: String,
    val launchName: String,
    val label: String,
    val mark: String,
    val iconUrl: String? = null,
    val accent: Color,
    val accentSecondary: Color = accent
)

private fun favicon(domain: String): String =
    "https://www.google.com/s2/favicons?domain=$domain&sz=128"

private val defaultRemoteShortcutApps = listOf(
    RemoteShortcutApp(
        id = "youtube",
        launchName = "YouTube",
        label = "YouTube",
        mark = "YT",
        iconUrl = favicon("youtube.com"),
        accent = Color(0xFFFF2D20),
        accentSecondary = Color(0xFFFF7A66)
    ),
    RemoteShortcutApp(
        id = "prime_video",
        launchName = "Prime Video",
        label = "Prime Video",
        mark = "PV",
        iconUrl = favicon("primevideo.com"),
        accent = Color(0xFF1E88FF),
        accentSecondary = Color(0xFF00B8D9)
    ),
    RemoteShortcutApp(
        id = "netflix",
        launchName = "Netflix",
        label = "Netflix",
        mark = "N",
        iconUrl = favicon("netflix.com"),
        accent = Color(0xFFD61F2C),
        accentSecondary = Color(0xFF5B0D18)
    ),
    RemoteShortcutApp(
        id = "disney_plus",
        launchName = "Disney+",
        label = "Disney+",
        mark = "D+",
        iconUrl = favicon("disneyplus.com"),
        accent = Color(0xFF001E7E),
        accentSecondary = Color(0xFF0072D2)
    ),
    RemoteShortcutApp(
        id = "spotify",
        launchName = "Spotify",
        label = "Spotify",
        mark = "S",
        iconUrl = favicon("spotify.com"),
        accent = Color(0xFF1DB954),
        accentSecondary = Color(0xFF191414)
    ),
    RemoteShortcutApp(
        id = "youtube_music",
        launchName = "YouTube Music",
        label = "YT Music",
        mark = "YM",
        iconUrl = favicon("music.youtube.com"),
        accent = Color(0xFFFF0033),
        accentSecondary = Color(0xFF990022)
    ),
    RemoteShortcutApp(
        id = "twitch",
        launchName = "Twitch",
        label = "Twitch",
        mark = "T",
        iconUrl = favicon("twitch.tv"),
        accent = Color(0xFF9146FF),
        accentSecondary = Color(0xFF6441A5)
    ),
    RemoteShortcutApp(
        id = "plex",
        launchName = "Plex",
        label = "Plex",
        mark = "P",
        iconUrl = favicon("plex.tv"),
        accent = Color(0xFFE5A00D),
        accentSecondary = Color(0xFF282A2D)
    ),
    RemoteShortcutApp(
        id = "crunchyroll",
        launchName = "Crunchyroll",
        label = "Crunchyroll",
        mark = "C",
        iconUrl = favicon("crunchyroll.com"),
        accent = Color(0xFFF47521),
        accentSecondary = Color(0xFF232527)
    ),
    RemoteShortcutApp(
        id = "hulu",
        launchName = "Hulu",
        label = "Hulu",
        mark = "HU",
        iconUrl = favicon("hulu.com"),
        accent = Color(0xFF1CE783),
        accentSecondary = Color(0xFF0B8B4B)
    ),
    RemoteShortcutApp(
        id = "apple_tv",
        launchName = "Apple TV",
        label = "Apple TV",
        mark = "AT",
        iconUrl = favicon("tv.apple.com"),
        accent = Color(0xFF2F3C52),
        accentSecondary = Color(0xFF7D91B3)
    ),
    RemoteShortcutApp(
        id = "max",
        launchName = "Max",
        label = "Max",
        mark = "MX",
        iconUrl = favicon("max.com"),
        accent = Color(0xFF7B31FF),
        accentSecondary = Color(0xFF2C0B63)
    ),
    RemoteShortcutApp(
        id = "paramount_plus",
        launchName = "Paramount+",
        label = "Paramount+",
        mark = "P+",
        iconUrl = favicon("paramountplus.com"),
        accent = Color(0xFF0064FF),
        accentSecondary = Color(0xFF00308F)
    ),
    RemoteShortcutApp(
        id = "peacock",
        launchName = "Peacock",
        label = "Peacock",
        mark = "P",
        iconUrl = favicon("peacocktv.com"),
        accent = Color(0xFF000000),
        accentSecondary = Color(0xFFFAA21B)
    ),
    RemoteShortcutApp(
        id = "discovery_plus",
        launchName = "Discovery+",
        label = "Discovery+",
        mark = "D+",
        iconUrl = favicon("discoveryplus.com"),
        accent = Color(0xFF0073FF),
        accentSecondary = Color(0xFF003B85)
    ),
    RemoteShortcutApp(
        id = "pluto_tv",
        launchName = "Pluto TV",
        label = "Pluto TV",
        mark = "PT",
        iconUrl = favicon("pluto.tv"),
        accent = Color(0xFFFFE000),
        accentSecondary = Color(0xFF1A1F71)
    ),
    RemoteShortcutApp(
        id = "tubi",
        launchName = "Tubi",
        label = "Tubi",
        mark = "Tu",
        iconUrl = favicon("tubitv.com"),
        accent = Color(0xFFFF4D00),
        accentSecondary = Color(0xFF000000)
    ),
    RemoteShortcutApp(
        id = "espn",
        launchName = "ESPN",
        label = "ESPN",
        mark = "E",
        iconUrl = favicon("espn.com"),
        accent = Color(0xFFD11217),
        accentSecondary = Color(0xFF3F0408)
    ),
    RemoteShortcutApp(
        id = "vimeo",
        launchName = "Vimeo",
        label = "Vimeo",
        mark = "V",
        iconUrl = favicon("vimeo.com"),
        accent = Color(0xFF1AB7EA),
        accentSecondary = Color(0xFF005A6F)
    ),
    RemoteShortcutApp(
        id = "mubi",
        launchName = "MUBI",
        label = "MUBI",
        mark = "M",
        iconUrl = favicon("mubi.com"),
        accent = Color(0xFF131313),
        accentSecondary = Color(0xFF4A4A4A)
    ),
    RemoteShortcutApp(
        id = "jiohotstar",
        launchName = "JioHotstar",
        label = "Jio Hotstar",
        mark = "JH",
        iconUrl = favicon("hotstar.com"),
        accent = Color(0xFF6A54FF),
        accentSecondary = Color(0xFF19C6FF)
    ),
    RemoteShortcutApp(
        id = "sonyliv",
        launchName = "SonyLIV",
        label = "SonyLIV",
        mark = "SL",
        iconUrl = favicon("sonyliv.com"),
        accent = Color(0xFF8E5BFF),
        accentSecondary = Color(0xFFFF4D7A)
    ),
    RemoteShortcutApp(
        id = "zee5",
        launchName = "ZEE5",
        label = "ZEE5",
        mark = "Z5",
        iconUrl = favicon("zee5.com"),
        accent = Color(0xFF8230C9),
        accentSecondary = Color(0xFFFF1A6E)
    ),
    RemoteShortcutApp(
        id = "jiocinema",
        launchName = "JioCinema",
        label = "JioCinema",
        mark = "JC",
        iconUrl = "https://upload.wikimedia.org/wikipedia/en/thumb/0/00/JioCinema_logo.svg/512px-JioCinema_logo.svg.png",
        accent = Color(0xFFE60012),
        accentSecondary = Color(0xFFFF7777)
    )
)

fun defaultRemoteShortcutOrder(): List<String> = defaultRemoteShortcutApps.map(RemoteShortcutApp::id)

fun decodeRemoteShortcutOrder(raw: String?): List<String> {
    if (raw.isNullOrBlank()) return defaultRemoteShortcutOrder()
    val knownIds = defaultRemoteShortcutApps.map(RemoteShortcutApp::id).toSet()
    val savedIds = raw.split(',')
        .map(String::trim)
        .filter(String::isNotBlank)
        .filter { it in knownIds }
        .distinct()
    return (savedIds + defaultRemoteShortcutApps.map(RemoteShortcutApp::id)).distinct()
}

fun encodeRemoteShortcutOrder(order: List<String>): String = decodeRemoteShortcutOrder(
    order.joinToString(",")
).joinToString(",")

fun resolveRemoteShortcutApps(order: List<String>): List<RemoteShortcutApp> {
    val appsById = defaultRemoteShortcutApps.associateBy(RemoteShortcutApp::id)
    return decodeRemoteShortcutOrder(order.joinToString(","))
        .mapNotNull(appsById::get)
}

fun moveRemoteShortcutOrderItem(order: List<String>, fromIndex: Int, direction: Int): List<String> {
    val resolved = decodeRemoteShortcutOrder(order.joinToString(",")).toMutableList()
    val targetIndex = fromIndex + direction
    if (fromIndex !in resolved.indices || targetIndex !in resolved.indices) {
        return resolved
    }
    val movedItem = resolved.removeAt(fromIndex)
    resolved.add(targetIndex, movedItem)
    return resolved
}
