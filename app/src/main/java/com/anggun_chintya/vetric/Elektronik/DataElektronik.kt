package com.anggun_chintya.vetric.Elektronik

data class DataElektronik(
    var kategori: String,
    var nama: String,
    var tipe: String,
    var daya: Double,
    var jmlUnit: Int,
    var durasi: Double,
    var ulang: Int,
    var userID: String?

){
    constructor() : this("", "","",0.0,0,0.0,0,"")
}