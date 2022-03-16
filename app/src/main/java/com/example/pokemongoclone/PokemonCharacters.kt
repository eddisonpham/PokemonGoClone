package com.example.pokemongoclone

import android.location.Location

class PokemonCharacters {
    var titleOfPokemon: String? = null
    var message:String? = null
    var iconOfPokemon:Int? = null
    var location: Location? = null
    var isKilled: Boolean? = false
    constructor(titleOfPokemon: String,
                message:String,
                iconOfPokemon:Int,
                latitude: Double,
                longitude: Double){

        location = Location("MyProvider")
        this.titleOfPokemon = titleOfPokemon
        this.message = message
        this.iconOfPokemon = iconOfPokemon
        this.location?.latitude = latitude
        this.location?.longitude = longitude

    }
}