package com.texnar13.deliveryapp.model.entities

import java.io.Serializable

data class EntityExpedition(
    var id: Long,
    val addressReceiver: EntityAddress,
    val addressSender: EntityAddress,
    val status: ExpeditionStatus,
    val senderId: Long,
    val courierId: Long?,
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

/*
*
*      + "ID": 2,
*      - "CreatedAt": "2024-12-25T01:05:52.067598Z",
*      - "UpdatedAt": "2024-12-25T01:05:52.067598Z",
*      - "DeletedAt": null,
*      + "sender_id": 3,
*      + "courier_id": null,
*      + "status": null,
*      + "dep_country": "USA",
*      + "dep_city": "New York",
*      + "dep_district": "Manhattan",
*      + "dep_street": "5th Avenue",
*      + "dest_country": "Canada",
*      + "dest_city": "Toronto",
*      + "dest_district": "Downtown",
*      + "dest_street": "King Street",
*      + "name": "Electronics",
*      + "category": "Gadgets",
*      + "description": "Smartphone and accessories",
*      + "width": 15,
*      + "height": 10,
*      + "length": 20,
*      + "weight": 1.5
*
* */