package academy.learnprogramming.utils

import academy.learnprogramming.R
import academy.learnprogramming.models.BarangCart
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

class CartAdapter(val context: Context,val itemIncart:ArrayList<BarangCart>):
    RecyclerView.Adapter<CartAdapter.itemViewHolder>(){
/**** ini drill all posibility ***/

    fun removeItem(position: Int): String? {
        var item: String? = null
        try {
           // itemIncart!![position]
            item = itemIncart[position].toString()
            itemIncart.removeAt(position)
            notifyItemRemoved(position)
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
        }
        return item
    }
    fun restoreItem(deletedItem:BarangCart,position: Int){
        itemIncart.add(position,deletedItem)
        notifyItemInserted(position)

    }
    fun revised(editedItem:BarangCart,position: Int){
        itemIncart.add(position,editedItem)
        notifyItemInserted(position)

    }
    companion object {
        private const val TAG = "ADAPTER"
    }
    /**** ini drill all posibility ***/


    class itemViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val merk: TextView = itemView.findViewById(R.id.tvmerkMoney)
        val namaBarang : TextView = itemView.findViewById(R.id.tvnamabarangMoneyTx)
        val varian : TextView = itemView.findViewById(R.id.tvvarianMoneyTx)
        val hargaJual : TextView = itemView.findViewById(R.id.tvhargajualMoneyTx)
        val qty : TextView = itemView.findViewById(R.id.tvQtyMoney)
        val unit : TextView =  itemView.findViewById(R.id.tvunitMoneyTx)
        val totalHarga: TextView = itemView.findViewById(R.id.totalMoneyTx)
        val cardViewItem : CardView =  itemView.findViewById(R.id.card_view_allincart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.itemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.make_money_itemtx,
            parent,false)
        return itemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartAdapter.itemViewHolder, position: Int) {
        val currentitem = itemIncart[position]
        holder.merk.text = currentitem.merk
        holder.namaBarang.text=currentitem.namaBrg
        holder.varian.text=currentitem.varian

        val oriHjual = currentitem.hargaJual.toString()
        val convertHjual = ValidNumber().deciformat(oriHjual)
        holder.hargaJual.text=convertHjual
        //holder.hargaJual.text=currentitem.hargaJual

        val oriQty = currentitem.qty.toString()
        val convertQty = ValidNumber().deciformat(oriQty)
        holder.qty.text=convertQty
        //holder.qty.text=currentitem.qty

        holder.unit.text=currentitem.unit

        val oriTotalHarga = currentitem.total.toString()
        val convertTotalHarga = ValidNumber().deciformat(oriTotalHarga)
        holder.totalHarga.text=convertTotalHarga
        //holder.totalHarga.text=currentitem.total


        // holder.hargaModal.text=currentitem.hargaModal
        // Updating the background color according to the odd/even positions in list.
        if (position % 2 == 0) {

            holder.cardViewItem.setCardBackgroundColor(ContextCompat.getColor(context,R.color.colorLightGray))
        } else {
            holder.cardViewItem.setCardBackgroundColor(ContextCompat.getColor(context,R.color.white))
        }
    }

    override fun getItemCount(): Int {
        return itemIncart.size
    }

    fun deciformat(terimaString:String):String {
        val konversiLong = terimaString.toLong()
        val dcFormat = DecimalFormat("#,###")
        val hasiDeci = dcFormat.format(konversiLong).toString().replace(',','.')
        return hasiDeci
    }
}


