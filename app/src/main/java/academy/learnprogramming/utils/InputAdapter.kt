package academy.learnprogramming.utils

import android.content.Context
import academy.learnprogramming.R
import academy.learnprogramming.TransactionActivity
import academy.learnprogramming.models.Barang
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


//class inputAdapter: RecyclerView.Adapter<inputAdapter.MyViewHolder>() {

//class inputAdapter(private val barangList: ArrayList<Barang>): RecyclerView.Adapter<inputAdapter.MyViewHolder>() {
// ada 2 penerima parameter, 1 buat bikin warnawarni recview, 2 buat cliclistener
class InputAdapter(val context: Context,var clickListener: TransactionActivity): RecyclerView.Adapter<InputAdapter.MyViewHolder>(){

    var barangList = ArrayList<Barang>()
    //var barangListFilter = ArrayList<Barang>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.barang_itemtx,
            parent,false)
        return MyViewHolder(itemView)
    }

    fun setData(barangList: ArrayList<Barang>){
        this.barangList = barangList
      //  this.barangListFilter = barangList
        //notifyDataSetChanged() //mendengar apakah ada perubahan di data
        notifyItemChanged(barangList.size)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = barangList[position]
        holder.merk.text = currentitem.merk
        holder.namaBarang.text=currentitem.namaBrg
        holder.varian.text=currentitem.varian
        holder.hargaJual.text=currentitem.hargaJual
        // holder.hargaModal.text=currentitem.hargaModal
        holder.stok.text=currentitem.stok
        holder.munit.text=currentitem.unit

        // Updating the background color according to the odd/even positions in list.
        if (position % 2 == 0) {

            holder.cardViewItem.setCardBackgroundColor(ContextCompat.getColor(context,R.color.colorLightGray))
        } else {
            holder.cardViewItem.setCardBackgroundColor(ContextCompat.getColor(context,R.color.white))
        }

        holder.itemView.setOnClickListener{
            clickListener.ClickedItem(currentitem)
            //mendengarkan tekan tombol di baranglist position
        }
    }

    override fun getItemCount(): Int {
        return barangList.size
    }

    class MyViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
        val merk : TextView = itemView.findViewById(R.id.tvmerkTx)
        val namaBarang : TextView = itemView.findViewById(R.id.tvnamabarangTx)
        val varian : TextView = itemView.findViewById(R.id.tvvarianTx)
        val hargaJual : TextView = itemView.findViewById(R.id.tvhargajualTx)
        //val hargaModal : TextView = itemView.findViewById(R.id.tvhargamodal)
        val stok : TextView = itemView.findViewById(R.id.tvstokTx)
        val munit : TextView = itemView.findViewById(R.id.tvunitTx)

        val cardViewItem= itemView.findViewById<CardView>(R.id.card_view_item)

    }

    interface ClickListener{
        fun ClickedItem(barang:Barang)
    }



}


