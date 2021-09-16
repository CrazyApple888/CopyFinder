import util.Constants.MAX_OFFLINE_CYCLES
import util.Constants.SLEEP_TIME
import util.Constants.SOCKET_TIMEOUT
import util.IDGenerator
import java.io.IOException
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.net.SocketTimeoutException
import java.nio.charset.Charset

class CopyFinder(
    private val address: InetAddress,
    private val port: Int
) {
    private val multicastSocket = MulticastSocket(port)
    private val users: MutableMap<String, UserInfo> = mutableMapOf()
    private val id = IDGenerator.generate()
    private var isConnected = true
    private var isUpdated = false

    init {
        multicastSocket.soTimeout = SOCKET_TIMEOUT
    }

    @Throws(IOException::class)
    fun start() {
        val buffer = ByteArray(IDGenerator.idSize)
        multicastSocket.joinGroup(address)
        val inPacket = DatagramPacket(buffer, buffer.size)
        val message = id.toByteArray()
        val outPacket = DatagramPacket(message, message.size, address, port)

        while (isConnected) {
            multicastSocket.send(outPacket)
            try {
                while (true) {
                    multicastSocket.receive(inPacket)
                    val tmpMessage = inPacket.data.toString(Charset.defaultCharset()).trim()
                    processMessage(tmpMessage)
                }
            } catch (exc: SocketTimeoutException) {
                updateActiveUsers()
            }
            printUsers()
            Thread.sleep(SLEEP_TIME)
        }
    }

    private fun printUsers() {
        if (isUpdated) {
            return
        }
        if (users.isEmpty()) {
            println("$id has no active users")
            isUpdated = true
            return
        }
        println("$id's active users:")
        users.forEach { println("${it.key}: ONLINE. ${MAX_OFFLINE_CYCLES - it.value.offlineCycles} seconds will be considered online") }
        isUpdated = true
    }

    private fun processMessage(newId: String) {
        if (id == newId) {
            return
        }
        if (!users.containsKey(newId)) {
            printJoinMessage(newId)
            isUpdated = false
        }
        users.merge(newId, UserInfo()) { _: UserInfo, new: UserInfo ->
            new.isChecked = true
            return@merge new
        }
    }

    private fun updateActiveUsers() {
        users.forEach {
            if (!it.value.isChecked) {
                it.value.offlineCycles++
                isUpdated = false
            }
            it.value.isChecked = false
        }
        users.values.removeIf { it.offlineCycles == MAX_OFFLINE_CYCLES }
    }

    private fun printJoinMessage(joinId: String) {
        println("-----------------------------------------------------------------")
        println("$joinId has joined!")
        println("-----------------------------------------------------------------")
    }
}