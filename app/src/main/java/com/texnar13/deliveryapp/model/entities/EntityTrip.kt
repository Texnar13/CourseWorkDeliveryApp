package com.texnar13.deliveryapp.model.entities

import java.util.Date

class EntityTrip(
        val id: Long,
        val price: String,
        val receivingCountry: String,
        val receivingCity: String,
        val sendCountry: String,
        val sendCity: String,
        val sentDate: Date,
        val availableWeight: Float
)