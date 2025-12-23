package com.mikhailskiy.dollarapp.data.api.mapper

interface BaseMapper<VO, API> {

    fun map(api: API): VO

    fun map(list: Collection<API>): List<VO> {
        val result = ArrayList<VO>()
        list.mapTo(result) { map(it) }
        return result
    }
}
