package com.anggun_chintya.vetric.Catatan

import java.util.Date

data class DataCatatan(
    var id: String = "",
    var judul: String = "",
    var tanggal: Date = Date(),
    var pengeluaran: Double = 0.0,
    var userID: String? = null
)