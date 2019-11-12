package com.ibm.cic.kotlin.starterkit.constants

class Fufinity {

    // TODO: REPLACE SERVICES AND CHARACTERISTICS TO BURNT UUID
    companion object {

        const val SERVICE_DEFAULT = "FFE0"
        const val CHARACTERISTICS_DEFAULT = "FFE1"

        const val SERVICE_PHRASE = "f000aa00-0451-4000-b000-000000000000"
        const val CHARACTERISTICS_READ_PHRASE = "f000aa01-0451-4000-b000-000000000000"
        const val CHARACTERISTICS_WRITE_PHRASE = "f000aa02-0451-4000-b000-000000000000"

        const val SERVICE_COLOR = "f000aa30-0451-4000-b000-000000000000"
        const val CHARACTERISTICS_READ_COLOR = "f000aa31-0451-4000-b000-000000000000"
        const val CHARACTERISTICS_WRITE_COLOR = "f000aa32-0451-4000-b000-000000000000"

        const val SERVICE_TEST_A = "00001800-0000-1000-8000-00805f9b34fb"
        const val SERVICE_TEST_B = "00001801-0000-1000-8000-00805f9b34fb"
        const val SERVICE_TEST_C = "0000180a-0000-1000-8000-00805f9b34fb"
        const val SERVICE_TEST_D = "0000ffe0-0000-1000-8000-00805f9b34fb"

        const val CHARACTER_TEST_A = "00002a00-0000-1000-8000-00805f9b34fb" // [READABLE]
        const val CHARACTER_TEST_B = "00002a01-0000-1000-8000-00805f9b34fb" // [READABLE]
        const val CHARACTER_TEST_C = "00002a03-0000-1000-8000-00805f9b34fb" // [WRITABLE]
        const val CHARACTER_TEST_D = "00002a04-0000-1000-8000-00805f9b34fb" // [READABLE]

        const val CHARACTER_TEST_E = "00002a05-0000-1000-8000-00805f9b34fb" // [INDICATE]
    }
}