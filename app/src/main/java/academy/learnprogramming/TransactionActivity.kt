package academy.learnprogramming


//import academy.learnprogramming.databinding.ActivityPushItemtxBinding
import academy.learnprogramming.models.Barang
import academy.learnprogramming.models.BarangCart
import academy.learnprogramming.utils.CartAdapter
import academy.learnprogramming.utils.InputAdapter
import android.app.Dialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import androidx.core.content.ContextCompat
import androidx.core.text.set
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
//import androidx.appcompat.app.AlertDialog
//import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.lang.Exception

//class TransactionActivity : AppCompatActivity(),inputAdapter.ClickListener,cartAdapter.ClickListener{
class TransactionActivity : AppCompatActivity(), InputAdapter.ClickListener {
    //   private var _binding : ActivityPushItemtxBinding? = null
//    private val binding get() = _binding!!

    private lateinit var addsBtn: FloatingActionButton
    private lateinit var dbref: DatabaseReference
    private lateinit var recyclerview: RecyclerView
    private lateinit var dialogBox: Dialog
    private lateinit var recyclerviewCart: RecyclerView // ini recview buat final cart
    private lateinit var btnClose: Button
    private lateinit var discCheckBox: CheckBox
    private lateinit var discountTv : EditText

   // private lateinit var totalBayar: TextView


    // var searchView : SearchView? =null
    // var searchView: SearchView? =null
    //private lateinit var inputAdapter: inputAdapter? =null
    //var inputAdapter: inputAdapter? =null

    //var inputAdapter: inputAdapter? =null
    var barangArrayList = ArrayList<Barang>()
    private var matchedBarang: ArrayList<Barang> = arrayListOf()
    private var inputAdapter: InputAdapter =
        InputAdapter(this@TransactionActivity, this@TransactionActivity)

    //private lateinit var barangArrayListTemp : ArrayList<Barang>
////////////////////////////////////
    var allInCart = ArrayList<BarangCart>()
    private var cartAdapter: CartAdapter = CartAdapter(this@TransactionActivity, allInCart)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //_binding= DataBindingUtil.setContentView(this,R.layout.activity_push_itemtx)

        setContentView(R.layout.activity_transaction)
        //   allInCart = ArrayList() /** menyiapkan kontener array buat recview di transaction activity */

////////////////////////////////////////////////////////////////////////////////
        recyclerviewCart = findViewById(R.id.cartRecview)
        recyclerviewCart.layoutManager = LinearLayoutManager(this)
        recyclerviewCart.adapter = cartAdapter

        ////swipe it
        recyclerviewCart.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

        val callback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.RIGHT) {
                    val deletedItem = allInCart.get(position)
                    cartAdapter.removeItem(position)
                    updateAllinCartRecview()
                    val snackbar = Snackbar.make(
                        // window.decorView.rootView,
                        viewHolder.itemView,
                        " removed from cart!",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.setAction(android.R.string.cancel) {
                        //snackbar.setAction("UNDO") {
                        // undo is selected, restore the deleted item
                        //println(deletedItem)
                        //println("isi array")
                        cartAdapter.restoreItem(deletedItem, position)
                        updateAllinCartRecview()
                    }
                    //snackbar.setActionTextColor(Color.WHITE)
                    //snackbar.setBackgroundTint(Color.BLACK)
                    snackbar.show()

                }else{
                    //TODO : edit modul
                    //val deletedItem = allInCart.get(position)
                    val editedItem = allInCart.get(position)
                    //kenapa di remove?? karena jika tidak diremove, bakal muncul background hijau yg ga tidak hilang
                    cartAdapter.removeItem(position)
                    dialogBox = Dialog(this@TransactionActivity)
                    dialogBox.setContentView(R.layout.single_itemtx)
                    dialogBox.setCanceledOnTouchOutside(true)
                    //jika di cancel maka di kembalikan kembali datanya,
                    dialogBox.setOnCancelListener{
                             cartAdapter.restoreItem(editedItem,position)
                             updateAllinCartRecview()
                              }

                    dialogBox.findViewById<TextView>(R.id.tvmerkSingleTx).text = editedItem.merk
                    dialogBox.findViewById<TextView>(R.id.tvnamabarangSingleTx).text =editedItem.namaBrg
                    dialogBox.findViewById<TextView>(R.id.tvvarianSingleTx).text = editedItem.varian//("${barang.varian}")
                    dialogBox.findViewById<TextView>(R.id.tvstokSingleTx).text = editedItem.stok//("${barang.stok}")
                    dialogBox.findViewById<TextView>(R.id.tvhargajualSingleTx).text =editedItem.hargaJual
                    dialogBox.findViewById<TextView>(R.id.tvQtyBeli).text = editedItem.qty//"1"
                    dialogBox.findViewById<TextView>(R.id.tvunitSingleTx).text = editedItem.unit//("${barang.unit}")
                    val total = editedItem.qty?.toInt()  // ini gw ga tau kenapa otomatis dari intellij, utk mendapat nilai total
                        ?.let { editedItem.hargaJual?.toInt()?.times(it) }
                    dialogBox.findViewById<TextView>(R.id.totalSingleTx).text =total.toString()//("${barang.hargaJual}")
                    dialogBox.show()
                    //** mulai filter validasi item yg di edit
                    val jumlahBeliChange = dialogBox.findViewById<TextView>(R.id.tvQtyBeli)
                    val hargaJualChange = dialogBox.findViewById<TextView>(R.id.tvhargajualSingleTx)
                    var x: Int
                    hargaJualChange.setOnFocusChangeListener { _, focused ->
                        if (focused) {
                            hargaJualChange.setSelectAllOnFocus(true)
                        }else if (!focused) {
                            if(hargaJualChange.text.toString().toInt()<= editedItem.hargaModal!!.toInt() ){
                                Toast.makeText(this@TransactionActivity, "Harga Jual Wajib di atas harga modal", Toast.LENGTH_SHORT)
                                    .show()
                                hargaJualChange.text = editedItem.hargaJual
                                return@setOnFocusChangeListener
                                 }
                              }
                         }
                    hargaJualChange.addTextChangedListener {
                        if (hargaJualChange.text.toString().isEmpty()) {
                            hargaJualChange.text = editedItem.hargaJual
                            Toast.makeText(this@TransactionActivity, "Harga Jual Wajib di atas harga modal", Toast.LENGTH_SHORT)
                                .show()
                            return@addTextChangedListener
                              }
                         }
//ini untuk memfilter jika angka yg di masukan di awal angka 0
                    hargaJualChange.doAfterTextChanged {
                        if (hargaJualChange.text.toString().startsWith("0")) {
                            hargaJualChange.text = editedItem.hargaJual
                            Toast.makeText(this@TransactionActivity, "Harga Jual Wajib di atas harga modal", Toast.LENGTH_SHORT)
                                .show()
                            return@doAfterTextChanged
                        }
                        //ini membuat perubahan di harga jual menjadi responsif di bagian total harga
                        else {
                            x = (jumlahBeliChange.text.toString().toInt()) * (hargaJualChange.text.toString()
                                .toInt())
                            dialogBox.findViewById<TextView>(R.id.totalSingleTx).text = x.toString()
                            }
                     }
                    //kalo di layout ini buat Qty  pembelian
                    jumlahBeliChange.addTextChangedListener {
                        if (jumlahBeliChange.text.toString().isEmpty()) {
                            jumlahBeliChange.text = editedItem.qty
                        } else {
                            x = (jumlahBeliChange.text.toString().toInt()) * (hargaJualChange.text.toString()
                                .toInt())
                            dialogBox.findViewById<TextView>(R.id.totalSingleTx).text = x.toString()
                            }
                        }
                    //ini pake startwith, jika angka pertama itu angka 0, otomatis ganti ke angka 1
                    jumlahBeliChange.doAfterTextChanged {
                        if (jumlahBeliChange.text.toString().startsWith("0")) {
                            jumlahBeliChange.text = editedItem.qty
                            }
                     }
                    //clicklistener menambah jumlah qty beli
                    val qtybuttonPlus = dialogBox.findViewById<Button>(R.id.addButtonBeli)
                    qtybuttonPlus.setOnClickListener {
                        var qty = dialogBox.findViewById<TextView>(R.id.tvQtyBeli).text.toString().toInt()
                        qty++
                        dialogBox.findViewById<TextView>(R.id.tvQtyBeli).text = qty.toString()
                      }
                    //clicklistener mengurangi jumlah qty beli
                    val qtyButtonMinus = dialogBox.findViewById<Button>(R.id.minButtonBeli)
                    var y: Int
                    qtyButtonMinus.setOnClickListener {
                        y = dialogBox.findViewById<TextView>(R.id.tvQtyBeli).text.toString().toInt()
                        if (y <= 1) {
                            dialogBox.findViewById<TextView>(R.id.tvQtyBeli).text = "1"
                        } else {
                            y--
                            dialogBox.findViewById<TextView>(R.id.tvQtyBeli).text = y.toString()
                        }
                     }
                    /** submit proses */
                    //submit proses
                    val btnSubmit = dialogBox.findViewById<Button>(R.id.submit_input_single)
                    btnSubmit.setOnClickListener {
                        val totalbelitx =
                            dialogBox.findViewById<TextView>(R.id.totalSingleTx).text.toString()
                        val scanHargaJual =
                            dialogBox.findViewById<TextView>(R.id.tvhargajualSingleTx).text.toString()
                        val scanQty = dialogBox.findViewById<TextView>(R.id.tvQtyBeli).text.toString()
                        //jika harga jual di edit tapi kurang/ sama dengan harga modal
                        if (hargaJualChange.text.toString().toInt() <= editedItem.hargaModal!!.toInt()) {
                            Toast.makeText(this@TransactionActivity, "Harga Jual Wajib di atas harga modal", Toast.LENGTH_SHORT)
                                .show()
                            dialogBox.findViewById<TextView>(R.id.tvhargajualSingleTx).text =
                                editedItem.hargaJual
                            hargaJualChange.isFocused
                            hargaJualChange.setSelectAllOnFocus(true)
                            return@setOnClickListener

                        }
                        //jika qty  beli melebihi stok
                        if (scanQty.toInt() > editedItem.stok!!.toInt()) {
                            Toast.makeText(
                                this@TransactionActivity,
                                "Stok kurang dari jumlah yang dibeli,mohon tambahkan dahulu stoknya",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                           var temp=
                               BarangCart(
                                   editedItem.merk,editedItem.namaBrg,
                                   editedItem.varian,editedItem.hargaModal,scanHargaJual,
                                   editedItem.stok,editedItem.unit,editedItem.brgId,
                                   scanQty,totalbelitx
                               )
                            cartAdapter.revised(temp,position)

                            //println("uraa"+temp)
                            updateAllinCartRecview()

                            // ini kayanya tempat update array barangcart
                            //println(BarangCart.)
                            dialogBox.dismiss()

            //                btnClose.visibility = View.VISIBLE
            //                btnClose.setOnClickListener {
            //                    this.dialogBox.dismiss()
            //                }
                        }
                    }
                    /** submit proses  end*/
                    val btnCancel = dialogBox.findViewById<Button>(R.id.cancel_input_single)
                    btnCancel.setOnClickListener {
                        dialogBox.cancel()
                        //btnClose.text="Tekan area diluar kotak untuk exit"
                    }

                }
            /**** *88888888* ***/
            }

            // You must use @RecyclerViewSwipeDecorator inside the onChildDraw method
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addSwipeLeftBackgroundColor(
                        ContextCompat.getColor(
                            this@TransactionActivity,
                            R.color.recycler_view_item_swipe_left_background
                        )
                    )
                    .addSwipeLeftActionIcon(R.drawable.ic_archive_white_24dp)
                    .addSwipeRightBackgroundColor(
                        ContextCompat.getColor(
                            this@TransactionActivity,
                            R.color.recycler_view_item_swipe_right_background
                        )
                    )
                    .addSwipeRightActionIcon(R.drawable.ic_delete_white_24dp)
                    .addSwipeRightLabel(getString(R.string.action_delete))
                    .setSwipeRightLabelColor(Color.WHITE)
                    .addSwipeLeftLabel(getString(R.string.action_edit))
                    //.addSwipeLeftLabel(getString(R.string.action_archive))
                    .setSwipeLeftLabelColor(Color.WHITE) //.addCornerRadius(TypedValue.COMPLEX_UNIT_DIP, 16)
                    //.addPadding(TypedValue.COMPLEX_UNIT_DIP, 8, 16, 8)
                    .create()
                    .decorate()
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerviewCart)

///////////////////////////////////////////////////////////////////////////////

        addsBtn = findViewById(R.id.addingBtn) // floating button

        addsBtn.setOnClickListener { addInfo() }

        //inputAdapter = inputAdapter(this@TransactionActivity)
        //inputAdapter!!.setData(barangArrayList)
        //recyclerview= findViewById(R.id.barangListTx)
        //recyclerview.layoutManager= LinearLayoutManager(this)
        //recyclerview.setHasFixedSize(true)
        //barangArrayList = arrayListOf<Barang>()
        //getBarangData()


        //  var totalBayar=findViewById<TextView>(R.id.totalBayar)

        //potongan harga
        discCheckBox = findViewById(R.id.checkBoxDisc)
        discountTv = findViewById(R.id.discountTv)

        discCheckBox.setOnCheckedChangeListener {buttonView, isCheked ->
            if(isCheked){
                discountTv.isEnabled=true
                discountTv.text.append("1000")
                discountTv.setSelectAllOnFocus(true)
                Toast.makeText(this, "halo ini chek",Toast.LENGTH_SHORT).show()
            }else{
                discountTv.editableText.clear()
                discountTv.isEnabled = false
                //discountTv.text ="0"   // tempdiskon.toString().
                //discountTv.clearFocus()
                Toast.makeText(this, "halo ini Unchek",Toast.LENGTH_SHORT).show()
            }
        }
        //ini buat menghapus transaksi
        val delTranx =findViewById<Button>(R.id.hapusTranx)
        delTranx.setOnClickListener(){
            deleteTranx()
        }
        //ini buat menyimpan transaksi
        val saveTranx = findViewById<Button>(R.id.saveTranx)
        saveTranx.setOnClickListener(){
            simpanTranx()
        }
        //ini batas akhir on create
    }

    private fun simpanTranx() {
        TODO("Not yet implemented")
    }

    private fun deleteTranx(){
        allInCart.clear()
        cartAdapter.notifyDataSetChanged()
//kalo info dari web android developer, snackbar lebih sering digunakan di banding toast
//untuk menggunakan snackbar, berikan nama di root layout
        Snackbar.make(
            findViewById(R.id.rootTransactionActivity),
            "Berhasil menghapus Transaksi",
            Snackbar.LENGTH_SHORT
        ).show()
    //setActionTextColor(ContextCompat.getColor(this,R.color.accent)). ini untuk kasih warna di snackbar
    }



    private fun addInfo() {

        //searchView = this.dialogBox.findViewById(R.id.searchViewDialog)
        this.dialogBox = Dialog(this)
        this.dialogBox.setContentView(R.layout.activity_push_itemtx)
        //val searchView = this.dialogBox.findViewById(R.id.searchViewDialog) as SearchView

        this.inputAdapter = InputAdapter(this@TransactionActivity, this@TransactionActivity)
        barangArrayList.clear() // kalo ga di clear data menjadi terus terduplikasi
        inputAdapter.setData(barangArrayList)



        recyclerview = this.dialogBox.findViewById(R.id.barangListTx)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.setHasFixedSize(true)

        getBarangData()
        dialogBox.show()
        performSearch()
        // val btnClose  = dialogBox.findViewById<Button>(R.id.closeInput)
        btnClose = dialogBox.findViewById(R.id.closeInput)
        btnClose.setOnClickListener {
            // this.dialogBox.cancel()
            this.dialogBox.dismiss() // dismiss untuk menghemat memory
            //this.dialogBox.setCanceledOnTouchOutside(true)
        }


    }


//        searchView=findViewById(R.id.searchViewDialog)
//        searchView?.maxWidth = Int.MAX_VALUE


    private fun getBarangData() {
        dbref = FirebaseDatabase.getInstance().getReference("TblBarang")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    for (userSnapshot in snapshot.children) {


                        val barang = userSnapshot.getValue(Barang::class.java)
                        barangArrayList.add(barang!!)

                    }

                    recyclerview.adapter = inputAdapter
                    //barangArrayListTemp=barangArrayList
                    //  inputAdapter?.setData(barangArrayList)
                    // recyclerview.adapter = inputAdapter(barangArrayList,this@TransactionActivity)
                    //recyclerview.adapter = inputAdapter!!.setData(barangArrayList)
                    //inputAdapter.setData(barangArrayList)
                    println(barangArrayList)
                    // println(barangArrayListTemp)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TransactionActivity, "Its a toast!", Toast.LENGTH_SHORT).show()
            }


        })

    }

    override fun ClickedItem(barang: Barang) {
        println(barang.namaBrg!!)
        this.dialogBox = Dialog(this)
        this.dialogBox.setContentView(R.layout.single_itemtx)
        this.dialogBox.findViewById<TextView>(R.id.tvmerkSingleTx).text = ("${barang.merk}")
        this.dialogBox.findViewById<TextView>(R.id.tvnamabarangSingleTx).text =
            ("${barang.namaBrg}")
        this.dialogBox.findViewById<TextView>(R.id.tvvarianSingleTx).text = ("${barang.varian}")
        this.dialogBox.findViewById<TextView>(R.id.tvstokSingleTx).text = ("${barang.stok}")
        this.dialogBox.findViewById<TextView>(R.id.tvhargajualSingleTx).text =
            ("${barang.hargaJual}")
        this.dialogBox.findViewById<TextView>(R.id.tvQtyBeli).text = "1"
        this.dialogBox.findViewById<TextView>(R.id.tvunitSingleTx).text = ("${barang.unit}")
        this.dialogBox.findViewById<TextView>(R.id.totalSingleTx).text = ("${barang.hargaJual}")
        //       findViewById<Button>(R.id.closeInput).visibility = View.INVISIBLE
        //btnClose.visibility = View.INVISIBLE

//        val merk = this.dialogBox.findViewById<TextView>(R.id.tvmerkSingleTx)
//        merk.text = ("${barang.merk}")

        dialogBox.show()


        //onChange merubah harga total ketika ada perubahan jumlah beli dan harga jual
        // val changeBeli = this.dialogBox.findViewById<TextView>(R.id.tvhargajualSingleTx)
        //changeBeli.setOnFocusChangeListener()
        //texthchangelistener  untuk merefresh total jumlah beli
        // juga menghindari input 0 atau kosong
        val jumlahBeliChange = this.dialogBox.findViewById<TextView>(R.id.tvQtyBeli)
        val hargaJualChange = this.dialogBox.findViewById<TextView>(R.id.tvhargajualSingleTx)
        var x: Int

        hargaJualChange.setOnFocusChangeListener { _, focused ->
            if (focused) {
//             this.dialogBox.findViewById<Button>(R.id.hargaSave).visibility = View.VISIBLE
//                this.dialogBox.findViewById<TextView>(R.id.hintHargaJual).visibility = View.VISIBLE
//                Toast.makeText(this, "Harga Jual Wajib di atas harga modal", Toast.LENGTH_SHORT).show()
                hargaJualChange.setSelectAllOnFocus(true)
//                this.dialogBox.findViewById<TextView>(R.id.tvhargajualSingleTx).text=("${barang.hargaJual}")
//                return@setOnFocusChangeListener
            } else if (!focused) {
                if (hargaJualChange.text.toString().toInt() <= ("${barang.hargaModal}").toInt()) {
                    Toast.makeText(this, "Harga Jual Wajib di atas harga modal", Toast.LENGTH_SHORT)
                        .show()
                    hargaJualChange.text = ("${barang.hargaJual}")
                    return@setOnFocusChangeListener
                }
            }

        }
        hargaJualChange.addTextChangedListener {
            if (hargaJualChange.text.toString().isEmpty()) {
                hargaJualChange.text = ("${barang.hargaJual}")
                Toast.makeText(this, "Harga Jual Wajib di atas harga modal", Toast.LENGTH_SHORT)
                    .show()
                return@addTextChangedListener
            }
        }

        hargaJualChange.doAfterTextChanged {
            if (hargaJualChange.text.toString().startsWith("0")) {
                hargaJualChange.text = ("${barang.hargaJual}")
                Toast.makeText(this, "Harga Jual Wajib di atas harga modal", Toast.LENGTH_SHORT)
                    .show()
                return@doAfterTextChanged
            }
            //ini membuat perubahan di harga jual menjadi responsif di bagian total harga
            else {
                x = (jumlahBeliChange.text.toString().toInt()) * (hargaJualChange.text.toString()
                    .toInt())
                dialogBox.findViewById<TextView>(R.id.totalSingleTx).text = x.toString()
            }
        }
//kalo di layout ini buat Qty  pembelian
        jumlahBeliChange.addTextChangedListener {
            if (jumlahBeliChange.text.toString().isEmpty()) {
                jumlahBeliChange.text = "1"
            } else {
                x = (jumlahBeliChange.text.toString().toInt()) * (hargaJualChange.text.toString()
                    .toInt())
                this.dialogBox.findViewById<TextView>(R.id.totalSingleTx).text = x.toString()
            }
        }
//ini pake startwith, jika angka pertama itu angka 0, otomatis ganti ke angka 1
        jumlahBeliChange.doAfterTextChanged {
            if (jumlahBeliChange.text.toString().startsWith("0")) {
                jumlahBeliChange.text = "1"
            }
        }
        //clicklistener menambah jumlah qty beli
        val qtybuttonPlus = dialogBox.findViewById<Button>(R.id.addButtonBeli)

        qtybuttonPlus.setOnClickListener {
            var qty = dialogBox.findViewById<TextView>(R.id.tvQtyBeli).text.toString().toInt()
            qty++
            dialogBox.findViewById<TextView>(R.id.tvQtyBeli).text = qty.toString()
        }
        //clicklistener mengurangi jumlah qty beli
        val qtyButtonMinus = dialogBox.findViewById<Button>(R.id.minButtonBeli)
        var y: Int
        qtyButtonMinus.setOnClickListener {
            y = dialogBox.findViewById<TextView>(R.id.tvQtyBeli).text.toString().toInt()
            // val  m=dialogBox.findViewById<TextView>(R.id.tvJumlahBeli)?.text.toString().trim()
            // if (y<=1 || m.isNullOrEmpty() || m.isNullOrBlank()){
            if (y <= 1) {
                dialogBox.findViewById<TextView>(R.id.tvQtyBeli).text = "1"
            } else {
                y--
                dialogBox.findViewById<TextView>(R.id.tvQtyBeli).text = y.toString()
            }
        }
        //submit proses
        val btnSubmit = dialogBox.findViewById<Button>(R.id.submit_input_single)
        btnSubmit.setOnClickListener {
            val totalbelitx =
                this.dialogBox.findViewById<TextView>(R.id.totalSingleTx).text.toString()
            val scanHargaJual =
                this.dialogBox.findViewById<TextView>(R.id.tvhargajualSingleTx).text.toString()
            val scanQty = this.dialogBox.findViewById<TextView>(R.id.tvQtyBeli).text.toString()
            //jika harga jual di edit tapi kurang/ sama dengan harga modal
            if (hargaJualChange.text.toString().toInt() <= ("${barang.hargaModal}").toInt()) {
                Toast.makeText(this, "Harga Jual Wajib di atas harga modal", Toast.LENGTH_SHORT)
                    .show()
                this.dialogBox.findViewById<TextView>(R.id.tvhargajualSingleTx).text =
                    ("${barang.hargaJual}")
                hargaJualChange.isFocused
                hargaJualChange.setSelectAllOnFocus(true)
                return@setOnClickListener

            }
            //jika qty  beli melebihi stok
            if (scanQty.toInt() > ("${barang.stok}").toInt()) {
                Toast.makeText(
                    this,
                    "Stok kurang dari jumlah yang dibeli,mohon tambahkan dahulu stoknya",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
//                findViewById<TextView>(R.id.tvmerkMoney).text = ("${barang.merk}")
//                findViewById<TextView>(R.id.tvnamabarangMoneyTx).text = ("${barang.namaBrg}")
//                findViewById<TextView>(R.id.tvvarianMoneyTx).text = ("${barang.varian}")
//                findViewById<TextView>(R.id.tvhargajualMoneyTx).text =  scanHargaJual     //("${barang.hargaJual}")
//                findViewById<TextView>(R.id.tvunitMoneyTx).text = ("${barang.unit}")
//                findViewById<TextView>(R.id.tvQtyMoney).text = scanQty
//                findViewById<TextView>(R.id.totalMoneyTx).text = totalbelitx


                allInCart.add(
                    BarangCart(
                        "${barang.merk}", "${barang.namaBrg}",
                        "${barang.varian}", "${barang.hargaModal}", scanHargaJual,
                        "${barang.stok}", "${barang.unit}", "${barang.brgId}",
                        "$scanQty", "$totalbelitx"
                    )
                )

                /////////////////////////////////////////////////////////////////////////////////
                //cartAdapter.notifyItemInserted(1)
                //cartAdapter.notifyDataSetChanged()
                //ini ngetest bisa ngga
                //var totalBayar=findViewById<TextView>(R.id.totalBayar)

                updateAllinCartRecview()

                /////////////////////////////////////////////////////////////////////////////////


// ini kayanya tempat update array barangcart

                println(allInCart)
                this.dialogBox.dismiss()
                btnClose.visibility = View.GONE

//                btnClose.visibility = View.VISIBLE
//                btnClose.setOnClickListener {
//                    this.dialogBox.dismiss()
//                }
            }
        }
//untuk menngcancel di layar ke tiga
        val btnCancel = dialogBox.findViewById<Button>(R.id.cancel_input_single)
        btnCancel.setOnClickListener {
            this.dialogBox.cancel()
            //btnClose.text="Tekan area diluar kotak untuk exit"
            btnClose.visibility = View.GONE
        }
        /*Toast.makeText(this, "$barang", Toast.LENGTH_SHORT).show()
        AlertDialog.Builder(this)
            .setTitle("item yang anda tekan ")
            .setMessage("${barang.merk},${barang.namaBrg},${barang.varian},${barang.brgId}")
              //.setPositiveButton("ok")
            .create().show()*/
    }


    private fun performSearch() {
        val searchView = this.dialogBox.findViewById(R.id.searchViewDialog) as SearchView
        //binding.searchViewDialog.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                search(newText)
                return true
            }
        })
    }

    private fun search(text: String?) {
        matchedBarang = arrayListOf()


        text?.let {
            barangArrayList.forEach { barang ->
                if (barang.merk!!.contains(text, true) ||
                    barang.namaBrg.toString().contains(text, true)
                ) {
                    matchedBarang.add(barang)
                    updateRecyclerView()
                }
            }
            if (matchedBarang.isEmpty()) {
                Toast.makeText(this, "No match found!", Toast.LENGTH_SHORT).show()
            }
            updateRecyclerView()
        }
    }

    private fun updateRecyclerView() {
//        binding.barangListTx.apply {

        inputAdapter.setData(matchedBarang)
        inputAdapter.notifyDataSetChanged()
        //      }
    }

//    override fun ClickedItem(barang: BarangCart) {

    //    }
    ////////////////////////////////////////////////////
    fun updateAllinCartRecview() {
        CartAdapter(this, allInCart)
        cartAdapter.notifyDataSetChanged()

//TODO ini kayanya tempat muncul total belanja
        if (allInCart.isNotEmpty()) {
            var subTotal = 0
            for (hargajual in allInCart.indices) {
                subTotal += allInCart[hargajual].total!!.toInt()
            }
            findViewById<TextView>(R.id.totalBayar).text = subTotal.toString()

            //Toast.makeText(this, "ini harga jual", Toast.LENGTH_SHORT).show()
        } else {
            findViewById<TextView>(R.id.totalBayar).text = "0"
        }
    }
//////////////////////////////////////////////////////////
}