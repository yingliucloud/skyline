/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright © 2022 Skyline Team and Contributors (https://github.com/skyline-emu/)
 */

package emu.skyline.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TitleMetaData(
    val name : String,
    val id : String,
    val version : String,
    val rating : TitleRating,
    val discussion : String,
    val issues : List<Issue>? = null,
    val notes : List<String>? = null,
    val cheats : Map<String, Cheat>? = null,
)

@Serializable
enum class TitleRating {
    None,
    @SerialName("crash") Crash,
    @SerialName("intro") Intro,
    @SerialName("major-bugs") MajorBugs,
    @SerialName("minor-bugs") MinorBugs,
    @SerialName("perfect") Perfect,
}

@Serializable
data class Issue(
    val title : String,
    val description : String? = null,
    val url : String,
    //val workarounds : List<List<String>>, // TODO
)

@Serializable
data class Cheat(
    val title : String,
    val description : String? = null,
    val author : String? = null,
    val code : String,
)
