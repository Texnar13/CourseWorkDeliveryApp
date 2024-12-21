package com.texnar13.deliveryapp.model.entities

import com.texnar13.deliveryapp.model.DBAddress
import java.io.Serializable

data class EntityUser(
        var id: String,
        var password: String,
        val address: DBAddress,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val rating: Double
): Serializable