package util

import util.Constants.ID_SIZE
import util.Constants.PART_SIZE
import kotlin.random.Random

object IDGenerator {
    /**
     * PART_SIZE symbols in each block,
     * total ID_SIZE blocks and (ID_SIZE - 1) '-' between blocks
     */
    const val idSize = PART_SIZE * ID_SIZE + ID_SIZE - 1

    /**
     * Generates ID in the form of XXXX-XXXX-XXXX-XXXX
     * @return random generated id
     */
    fun generate(): String {
        var id = String()
        for (i in 0 until ID_SIZE) {
            id += generatePart() + "-"
        }

        return id.trim('-')
    }


    private fun generatePart() = List(PART_SIZE) { Random.nextInt(0, 10) }.joinToString("")
}