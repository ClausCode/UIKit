import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.instance.block.Block

fun main() {
    val server = MinecraftServer.init()

    val instance = MinecraftServer.getInstanceManager().createInstanceContainer()
    instance.setGenerator { it.modifier().fillHeight(0, 4, Block.IRON_BLOCK) }
    MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent::class.java) {
        it.spawningInstance = instance
        it.player.respawnPoint = Pos(.0, 5.0, .0)
    }
    MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent::class.java) {
        TestUI(it.player).show()
    }

    server.start("0.0.0.0", 25565)
}