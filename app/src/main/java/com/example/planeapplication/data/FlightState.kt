package com.example.planeapplication.data

data class FlightState(
        var icao24 : String?,
        var callsign : String?,
        var origin_country : String?,
        var time_position : Int?,
        var last_contact : Int?,
        var longitude : Float?,
        var latitude : Float?,
        var baro_altitude : Float?,
        var on_ground : Boolean?,
        var velocity : Float?,
        var true_track : Float?,
        var vertical_rate : Float?,
        var geo_altitude : Float?,
        var squawk : String?,
        var spi : Boolean?,
        var position_source : Int?
)