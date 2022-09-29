// Copyright 2020 Google LL
package com.rushmi.myapplication.api

import com.rahulpa.myapplication.model.Place
import com.google.gson.annotations.SerializedName

data class NearbyPlacesResponse(
   @SerializedName("results") val results: List<Place>
)

