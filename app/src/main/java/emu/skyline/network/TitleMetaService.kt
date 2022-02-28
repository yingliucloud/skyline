/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright © 2022 Skyline Team and Contributors (https://github.com/skyline-emu/)
 */

package emu.skyline.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TitleMetaService {
    @GET("main/{id}/title.json")
    fun getData(@Path("id") titleId : String) : Call<TitleMetaData>
}
