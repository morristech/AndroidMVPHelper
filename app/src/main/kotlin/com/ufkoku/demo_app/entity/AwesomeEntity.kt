package com.ufkoku.demo_app.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

import java.io.Serializable

@Parcelize
data class AwesomeEntity(var importantDataField: Int) : Serializable, Parcelable
