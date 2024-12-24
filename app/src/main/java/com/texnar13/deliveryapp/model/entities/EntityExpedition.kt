package com.texnar13.deliveryapp.model.entities

import java.io.Serializable

data class EntityExpedition(
        var id: Long,
        val addressReceiver: EntityAddress,
        val addressSender: EntityAddress,
        val status: ExpeditionStatus,
        val sender: Long,
        val expeditionPackage: EntityPackage
) : Serializable {

    companion object {
        enum class ExpeditionStatus(textMean: String) {
            WAIT_SEND("Wait"),
            SENT("Sent"),
            DONE("Done")
        }
    }

}