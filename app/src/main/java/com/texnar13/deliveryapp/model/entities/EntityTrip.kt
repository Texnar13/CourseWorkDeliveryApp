package com.texnar13.deliveryapp.model.entities

import java.util.Date

class EntityTrip(
        val id: Long,
        val userId: Long,
        val isBusy: Boolean,
        val receivingAddress: EntityAddress,
        val sendAddress: EntityAddress,
        val depDate: Date,
        val destDate: Date,
        val freeWidth: Float,
        val freeHeight: Float,
        val freeLength: Float,
        val availableWeight: Float


//   + "ID": 9,
//   - "CreatedAt": "2024-12-29T18:33:08.001448Z",
//   - "UpdatedAt": "2024-12-29T18:33:08.001448Z",
//   - "DeletedAt": null,
//   + "user_id": 1,
//   + "is_busy": false,
//   + "dep_dat": "2022-12-29T10:00:00Z",
//   + "dep_country": "Russia",
//   + "dep_city": "Moscow",
//   + "dep_district": "Central",
//   + "dep_street": "Tverskaya Street",
//   + "dest_date": "2023-12-30T18:00:00Z",
//   + "dest_country": "USA",
//   + "dest_city": "New York",
//   + "dest_district": "Montmartre",
//   + "dest_street": "Rue de Rivoli",
//    "free_width": 1.2,
//    "free_height": 1.5,
//    "free_length": 2.5,
//    "free_weight": 20

)