package com.mohamedabdelazeim.islamicapp.data

data class AdhkarItem(
    val id: String = "",
    val type: String = "",
    val title: String = "",
    val text: String = "",
    val repeat: Int = 1,
    val benefit: String = "",
    val audioRes: Int? = null
)

data class AdhkarFile(
    val meta: Meta = Meta(),
    val adhkar: List<AdhkarItem> = emptyList()
)

data class Meta(
    val title: String = "",
    val time_of_day: String = "",
    val language: String = "ar"
)
