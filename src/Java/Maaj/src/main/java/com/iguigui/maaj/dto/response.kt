package com.iguigui.maaj.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject


@Serializable
data class HttpResponse(
    @SerialName("data")
    val data: JsonElement = EmptyBaseData.toJsonElement(),
    @SerialName("code")
    val code: Int = 0,
    @SerialName("message")
    val message: String = "success"
)

@Serializable
data class WsResponse(
    @SerialName("version")
    val data: JsonElement,
    @SerialName("type")
    val type: String,
    @SerialName("code")
    val code: Int,
    @SerialName("message")
    val message: String
)

@Serializable
sealed class BaseData

@Serializable
data class GetVersionResponse(
    @SerialName("version")
    val version: String
) : BaseData()

@Serializable
data class ConnectResponse(
    @SerialName("id")
    val id: String,
    @SerialName("result")
    val result: Boolean
) : BaseData()


@Serializable
data class ListInstanceResponse(
    @SerialName("list")
    val list: List<MaaInstanceInfo>
) : BaseData()

@Serializable
data class MaaInstanceInfo(
    @SerialName("id")
    val id: String,
    @SerialName("host")
    val host: String,
    @SerialName("adbPath")
    val adbPath: String,
    @SerialName("uuid")
    val uuid: String,
    @SerialName("status")
    val status: Int,
) : BaseData()

@Serializable
data class AppendTaskResponse(
    @SerialName("id")
    val id: String,
    @SerialName("taskId")
    val taskId: Int
) : BaseData()


@Serializable
data class SetTaskParamsResponse(
    @SerialName("id")
    val id: String,
    @SerialName("result")
    val result: Boolean
) : BaseData()


@Serializable
data class StartResponse(
    @SerialName("id")
    val id: String,
    @SerialName("result")
    val result: Boolean
) : BaseData()

@Serializable
data class StopResponse(
    @SerialName("id")
    val id: String,
    @SerialName("result")
    val result: Boolean
) : BaseData()

@Serializable
data class CallBackLog(
    @SerialName("id")
    val id: String,
    @SerialName("logId")
    val logId: Long,
    @SerialName("msg")
    val msg: Int,
    @SerialName("details")
    val details: JsonElement,
) : BaseData()


@Serializable
object EmptyBaseData : BaseData()

fun BaseData.toJsonElement() =
    Json.encodeToJsonElement(Json.encodeToJsonElement(this).jsonObject.filterNot { it.key == "type" })


fun BaseData.wapperToResponse() =
    HttpResponse(Json.encodeToJsonElement(Json.encodeToJsonElement(this).jsonObject.filterNot { it.key == "type" }))


