package com.iguigui.maaj.service

import com.iguigui.maaj.dto.ConnectResponse
import com.iguigui.maaj.dto.MaaInstanceInfo
import com.iguigui.maaj.easySample.MeoAssistant
import com.sun.jna.Native
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap


object MaaService {

    private var instancePool: ConcurrentHashMap<String, MaaInstance> = ConcurrentHashMap()

    val meoAssistant: MeoAssistant by lazy {
        val f = File(this.javaClass.getResource("")?.path ?: "")
        var maaPath = f.path
        println(maaPath)
        maaPath = "C:\\Users\\atmzx\\Desktop\\MeoAssistantArknights3"
        System.setProperty("jna.library.path", maaPath)
        val load = Native.load("MeoAssistant", MeoAssistant::class.java)
        load.AsstLoadResource(maaPath)
        load
    }

    fun connect(adbPath: String, host: String, detailJson: String): ConnectResponse {
        val id = sha1(host)
        synchronized(id) {
            if (instancePool.containsKey(id)) {
                return ConnectResponse(id, true)
            }
            val maaInstance = MaaInstance(meoAssistant, host, adbPath, host, detailJson)
            maaInstance.pointer = meoAssistant.AsstCreateEx(maaInstance, maaInstance.id)
            val connect = maaInstance.connect()
            if (!connect) {
                return ConnectResponse("", false)
            }
            instancePool.putIfAbsent(id, maaInstance)
        }
        return ConnectResponse(id, true)
    }

    fun appendTask(host: String, type: String, detailJson: String) =
        instancePool[host]?.appendTask(type, detailJson) ?: 0


    fun setTaskParams(host: String, type: String, taskId: Int, detailJson: String) =
        instancePool[host]?.setTaskParams(type, taskId, detailJson) ?: false

    fun start(host: String) = instancePool[host]?.start() ?: false

    fun stop(host: String) = instancePool[host]?.stop() ?: false

    fun getVersion(): String = meoAssistant.AsstGetVersion()

    private fun sha1(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA")
        val digests = messageDigest.digest(password.toByteArray())
        val stringBuilder = StringBuilder()
        for (i in digests.indices) {
            var halfbyte = digests[i].toInt() ushr 4 and 0x0F
            for (j in 0..1) {
                stringBuilder.append(
                    if (0 <= halfbyte && halfbyte <= 9) ('0'.code + halfbyte).toChar() else ('a'.code + (halfbyte - 10)).toChar()
                )
                halfbyte = digests[i].toInt() and 0x0F
            }
        }
        return stringBuilder.toString()
    }

    fun listInstance(): List<MaaInstanceInfo> =
        instancePool.values.map { e -> MaaInstanceInfo(e.id, e.host, e.adbPath, e.uuid, e.status) }.toList()


}
