package academy.learnprogramming.models




data class Barang (
    val merk : String?= null,
    val namaBrg : String?= null,
    val varian : String?= null,
    val hargaModal : String?= null,
    val hargaJual: String?= null,
    val stok : String?= null,
    val unit : String?=null,
    val brgId : String?=null)

data class BarangCart (
    val merk : String?= null,
    val namaBrg : String?= null,
    val varian : String?= null,
    val hargaModal : String?= null,
    var hargaJual: String?= null,
    val stok : String?= null,
    val unit : String?=null,
    val brgId : String?=null,
    var qty : String?=null,
    var total : String?=null)

   /* data class Barang (
        val merk : String?= null,
        val namaBrg : String?= null,
        val varian : String?= null,
        val hargaModal : String?= null,
        val hargaJual: String?= null,
        val stok : String?= null)*/


//coba tanpa ada null

