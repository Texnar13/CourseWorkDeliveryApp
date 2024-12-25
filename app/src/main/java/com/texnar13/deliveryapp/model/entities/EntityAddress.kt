package com.texnar13.deliveryapp.model.entities

import java.io.Serializable

class EntityAddress(
        val address: Array<String>

) : Serializable {

    constructor(
            country: String,
            city: String,
            district: String,
            street: String
    ) : this(arrayOf(country, city, district, street))

    fun getString():String{
        return "${address[0]}, ${address[1]}, ${address[2]}, ${address[3]}"
    }

    fun getCountry():String{
        return address[0]
    }
    fun getCity():String{
        return address[1]
    }
    fun getStreet():String{
        return address[2]
    }
    fun getHouse():String{
        return address[3]
    }
}