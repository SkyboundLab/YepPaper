package org.elliotnash.yeppaper

import io.papermc.paper.advancement.AdvancementDisplay
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.plugin.java.JavaPlugin

private const val RECORD_SEPARATOR = '\u241E'

private const val UNIT_SEPARATOR = '\u241F'

const val YEP_GENERIC = "yep:generic"

const val YEP_ADVANCEMENT = "yep:advancement"

const val YEP_DEATH = "yep:death"

const val YEP_ADV_DEFAULT = "DEFAULT"

const val YEP_ADV_GOAL = "GOAL"

const val YEP_ADV_TASK = "TASK"

const val YEP_ADV_CHALLENGE = "CHALLENGE"

class YepPaperPlugin : JavaPlugin(), Listener {
    private val textSerializer = PlainTextComponentSerializer.plainText()

    companion object {
        private val colorCodeRegex = "&[0-9a-fk-orx]".toRegex(RegexOption.IGNORE_CASE)
    }

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this)
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, YEP_GENERIC)
        logger.info("YepPaper enabled")
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        try {
            val playerName = event.player.name
            val displayName = removeColorCodes(textSerializer.serialize(event.player.displayName()))
            val deathMessage = event.deathMessage()?.let { 
                removeColorCodes(textSerializer.serialize(it)) 
            } ?: ""

            val message = "$YEP_DEATH$RECORD_SEPARATOR$playerName$UNIT_SEPARATOR$displayName$UNIT_SEPARATOR$deathMessage"

            event.player.sendPluginMessage(this, YEP_GENERIC, message.toByteArray(Charsets.UTF_8))
        } catch (e: Exception) {
            logger.warning("Failed to send death event for ${event.player.name}: ${e.message}")
        }
    }

    @EventHandler
    fun onPlayerAdvancement(event: PlayerAdvancementDoneEvent) {
        val display = event.advancement.getDisplay() ?: return

        try {
            val advType = when (display.frame()) {
                AdvancementDisplay.Frame.CHALLENGE -> YEP_ADV_CHALLENGE
                AdvancementDisplay.Frame.GOAL -> YEP_ADV_GOAL
                AdvancementDisplay.Frame.TASK -> YEP_ADV_TASK
            }

            val playerName = event.player.name
            val displayName = removeColorCodes(textSerializer.serialize(event.player.displayName()))
            val title = removeColorCodes(textSerializer.serialize(display.title()))
            val description = removeColorCodes(textSerializer.serialize(display.description()))

            val message = "$YEP_ADVANCEMENT$RECORD_SEPARATOR$playerName$UNIT_SEPARATOR$displayName$UNIT_SEPARATOR$advType$UNIT_SEPARATOR$title$UNIT_SEPARATOR$description"

            event.player.sendPluginMessage(this, YEP_GENERIC, message.toByteArray(Charsets.UTF_8))
        } catch (e: Exception) {
            logger.warning("Failed to send advancement event for ${event.player.name}: ${e.message}")
        }
    }

    private fun removeColorCodes(input: String): String {
        return colorCodeRegex.replace(input, "")
    }
}
