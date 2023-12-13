package com.anggun_chintya.vetric

data class DataElektronik (
    var kategori : String,
    var nama : String,
    var tipe : String,
    var daya : Double,
    var jmlUnit : Int,
    var durasi : Double,
    var ulang : Int
){
    constructor() : this("", "","",0.0,0,0.0,0)
}