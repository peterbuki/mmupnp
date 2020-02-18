/*
 * Copyright (c) 2019 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.upnp.cp.internal.server

import net.mm2d.upnp.common.Http
import net.mm2d.upnp.common.HttpRequest
import net.mm2d.upnp.common.ServerConst
import net.mm2d.upnp.common.internal.message.parseUsn
import net.mm2d.upnp.common.internal.server.Address
import net.mm2d.upnp.common.internal.thread.TaskExecutors
import net.mm2d.upnp.common.internal.thread.ThreadCondition
import net.mm2d.upnp.common.internal.util.closeQuietly
import net.mm2d.upnp.common.util.findInet4Address
import net.mm2d.upnp.common.util.findInet6Address
import net.mm2d.upnp.common.util.toSimpleString
import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.*

internal class MulticastEventReceiver(
    taskExecutors: TaskExecutors,
    val address: Address,
    private val networkInterface: NetworkInterface,
    private val listener: (uuid: String, svcid: String, lvl: String, seq: Long, properties: List<Pair<String, String>>) -> Unit
) : Runnable {
    private val interfaceAddress: InterfaceAddress =
        if (address == Address.IP_V4)
            networkInterface.findInet4Address()
        else
            networkInterface.findInet6Address()
    private var socket: MulticastSocket? = null
    private val threadCondition = ThreadCondition(taskExecutors.server)

    // VisibleForTesting
    @Throws(IOException::class)
    internal fun createMulticastSocket(port: Int): MulticastSocket {
        return MulticastSocket(port).also {
            it.networkInterface = networkInterface
        }
    }

    fun start() {
        threadCondition.start(this)
    }

    fun stop() {
        threadCondition.stop()
        socket.closeQuietly()
    }

    override fun run() {
        val suffix = "-multicast-event-" + networkInterface.name + "-" + interfaceAddress.address.toSimpleString()
        ThreadCondition.setThreadNameSuffix(suffix)
        if (threadCondition.isCanceled()) return
        try {
            val socket = createMulticastSocket(ServerConst.EVENT_PORT)
            this.socket = socket
            socket.joinGroup(address.eventInetAddress)
            threadCondition.notifyReady()
            receiveLoop(socket)
        } catch (ignored: IOException) {
        } finally {
            socket?.leaveGroup(address.eventInetAddress)
            socket.closeQuietly()
            socket = null
        }
    }

    // VisibleForTesting
    @Throws(IOException::class)
    internal fun receiveLoop(socket: MulticastSocket) {
        val buf = ByteArray(1500)
        while (!threadCondition.isCanceled()) {
            try {
                val dp = DatagramPacket(buf, buf.size)
                socket.receive(dp)
                if (threadCondition.isCanceled()) break
                onReceive(dp.data, dp.length)
            } catch (ignored: SocketTimeoutException) {
            }
        }
    }

    // VisibleForTesting
    internal fun onReceive(data: ByteArray, length: Int) {
        val request = HttpRequest.create(ByteArrayInputStream(data, 0, length))
        if (request.getHeader(Http.NT) != Http.UPNP_EVENT) return
        if (request.getHeader(Http.NTS) != Http.UPNP_PROPCHANGE) return
        val lvl = request.getHeader(Http.LVL)
        if (lvl.isNullOrEmpty()) return
        val seq = request.getHeader(Http.SEQ)?.toLongOrNull() ?: return
        val svcid = request.getHeader(Http.SVCID)
        if (svcid.isNullOrEmpty()) return
        val (uuid, _) = request.parseUsn()
        if (uuid.isEmpty()) return

        val properties = request.getBody().parseEventXml()
        if (properties.isEmpty()) return
        listener.invoke(uuid, svcid, lvl, seq, properties)
    }
}
