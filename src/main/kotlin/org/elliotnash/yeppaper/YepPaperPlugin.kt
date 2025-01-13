package org.elliotnash.yeppaper

import me.croabeast.lib.advancement.AdvancementInfo
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.plugin.java.JavaPlugin

const val YEP_ADV_FORMAT = "%s\u241E%s\u241F%s\u241F%s\u241F%s\u241F%s"
const val YEP_DEATH_FORMAT = "%s\u241E%s\u241F%s\u241F%s"

const val YEP_GENERIC = "yep:generic"
const val YEP_ADVANCEMENT = "yep:advancement"
const val YEP_DEATH = "yep:death"

const val YEP_ADV_DEFAULT = "DEFAULT"
const val YEP_ADV_GOAL = "GOAL"
const val YEP_ADV_TASK = "TASK"
const val YEP_ADV_CHALLENGE = "CHALLENGE"

class YepPaperPlugin : JavaPlugin(), Listener {
    private val textSerializer = PlainTextComponentSerializer.plainText()

    private val colorCodeRegex = "&[0-9a-fk-orx]".toRegex(RegexOption.IGNORE_CASE)

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this)
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, YEP_GENERIC)
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, YEP_ADVANCEMENT)
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, YEP_DEATH)
        logger.info("Yup paper enabled")
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val message = String.format(
            YEP_DEATH_FORMAT,
            YEP_DEATH,
            event.player.name,
            removeColorCodes(textSerializer.serialize(event.player.displayName())),
            event.deathMessage()?.let { removeColorCodes(textSerializer.serialize(it)) }
        )

        event.player.sendPluginMessage(this, YEP_GENERIC, message.toByteArray(Charsets.UTF_8))
    }

    @EventHandler
    fun onPlayerAdvancement(event: PlayerAdvancementDoneEvent) {
        val advInfo = AdvancementInfo.from(event.advancement) ?: return

        val advType: String = when (advInfo.frame) {
            AdvancementInfo.Frame.CHALLENGE -> YEP_ADV_CHALLENGE
            AdvancementInfo.Frame.GOAL -> YEP_ADV_GOAL
            AdvancementInfo.Frame.TASK -> YEP_ADV_TASK
            else -> YEP_ADV_DEFAULT
        }

        val message: String = String.format(
            YEP_ADV_FORMAT,
            YEP_ADVANCEMENT,
            event.player.name,
            removeColorCodes(textSerializer.serialize(event.player.displayName())),
            advType,
            removeColorCodes(advInfo.title),
            removeColorCodes(advInfo.description)
        )

        event.player.sendPluginMessage(this, YEP_GENERIC, message.toByteArray(Charsets.UTF_8))
    }

    private fun removeColorCodes(input: String): String {
        return colorCodeRegex.replace(input, "")
    }
}
