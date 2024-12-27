package com.texnar13.deliveryapp.model.entities

import java.io.Serializable

data class EntityUser(
        var id: Long,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val address: EntityAddress
): Serializable

//{
//      +  "ID": 3,
//      -  "CreatedAt": "2024-12-25T01:05:36.162233Z",
//      -  "UpdatedAt": "2024-12-25T01:05:36.162233Z",
//      -  "DeletedAt": null,
//      +  "email": "someemail2@email.com",
//      +  "name": "qwe",
//      +  "phone": "+7(952)812",
//      +  "country": "russia",
//      +  "city": "52",
//      +  "district": "52",
//      +  "street": "len"
//}