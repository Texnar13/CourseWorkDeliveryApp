package com.texnar13.deliveryapp.model.entities

import java.io.Serializable

data class EntityUser(
        var id: String,
        var password: String,
        val address: EntityAddress,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val rating: Double
): Serializable