import util.Constants.MULTICAST_ADDRESS
import java.io.IOException
import java.net.InetAddress

fun main(args: Array<String>) {
    val address = InetAddress.getByName(
        if (args.isNotEmpty()) {
            args[0]
        } else {
            MULTICAST_ADDRESS
        }
    )
    try {
        CopyFinder(address, 8080).start()
    } catch (exc: IOException) {
        exc.printStackTrace()
    }
}