package com.texnar13.deliveryapp.model.entities

data class EntityPackage(

        val category: String,

        val description: String,

        val dimensions: Array<Float>,

        val weight: Float,

        val name: String,

        val picture: String
) {

    fun getDimensionsString():String{
        return "${dimensions[0]}, ${dimensions[1]}, ${dimensions[2]}"
    }
}