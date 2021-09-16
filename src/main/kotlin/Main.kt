import util.Constants.MULTICAST_ADDRESS
import java.net.InetAddress

fun main(args: Array<String>) {
    val address = InetAddress.getByName(
        if (args.isNotEmpty()) {
            args[0]
        } else {
            MULTICAST_ADDRESS
        }
    )
    val finder = CopyFinder(address, 8080)
    finder.start()
}