package academy.learnprogramming

import academy.learnprogramming.databinding.ActivityInputItemBinding
import academy.learnprogramming.models.Barang
import academy.learnprogramming.utils.Constants
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class InputItem : AppCompatActivity() {
    private lateinit var binding:ActivityInputItemBinding
    private lateinit var   bottomNavigationView: BottomNavigationView
    private lateinit var mUnit : String
    private lateinit var unikIden : String
    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputItemBinding.inflate(layoutInflater)
        setContentView(binding.root)//setContentView(R.layout.activity_input_item)
       // unikIden ="gLewOaXckfeBUdOq6FHbv8HJMt22"
        navigasiBawah()
        initView()

    }

    private fun initView() {
        val radioGroup = binding.radioGroup
//        var mUnit : String
        radioGroup.clearCheck()
        radioGroup.setOnCheckedChangeListener{
                group,checkedId ->

            if (R.id.satuanButton == checkedId){
                Toast.makeText(this,"memilih satuan",Toast.LENGTH_SHORT).show()
                    mUnit ="Satuan"
            }
            if (R.id.perboxButton == checkedId){
                Toast.makeText(this,"memilih perbox",Toast.LENGTH_SHORT).show()
                mUnit ="PerBox"
            }

        }
        binding.registerBtn.setOnClickListener {

            val merk = binding.merk.text.toString().trim { it <= ' ' }
            val namaBrg = binding.namaBrg.text.toString().trim { it <= ' ' }
            val varian = binding.varian.text.toString().trim { it <= ' ' }
            val hargaModal = binding.hrgmodal.text.toString().trim { it <= ' ' }
            val hargaJual = binding.hrgjual.text.toString().trim { it <= ' ' }
            val stok = binding.stok.text.toString().trim { it <= ' ' }
            val perUnit = mUnit
            Constants.UNIKIDENT ="gLewOaXckfeBUdOq6FHbv8HJMt22"
            unikIden ="gLewOaXckfeBUdOq6FHbv8HJMt22"

            database = FirebaseDatabase.getInstance().getReference(Constants.TABELBARANG).child(unikIden)
//ini cara buat node, sesuai gw ingginkan 3 layer
            val key= database.child(Constants.TABELBARANG).child(unikIden).push().key
            val barang = Barang(merk, namaBrg, varian, hargaModal, hargaJual, stok,perUnit,key)

            database.child(key!!).setValue(barang).addOnSuccessListener{
                /*  database.child(merk).setValue(barang).addOnSuccessListener {*/
                binding.merk.text.clear()
                binding.namaBrg.text.clear()
                binding.varian.text.clear()
                binding.hrgmodal.text.clear()
                binding.hrgjual.text.clear()
                binding.stok.text.clear()
                binding.radioGroup.clearCheck()
                Toast.makeText(this, "Successfully Saved", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {

                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigasiBawah() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.inputItem

        bottomNavigationView.setOnItemSelectedListener (object:
            NavigationView.OnNavigationItemSelectedListener,
            NavigationBarView.OnItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.home -> {
                        startActivity(Intent(applicationContext, HomeActivity::class.java))
                        overridePendingTransition(0, 0)
                        return true
                    }

                    R.id.inputItem ->{
                        return true
                    }

                    R.id.transaksi ->{
                        startActivity(Intent(applicationContext, TransactionActivity::class.java))
                        overridePendingTransition(0, 0)
                        return true
                    }

                    R.id.editTransaksi ->{
                        startActivity(Intent(applicationContext, EditTransaksi::class.java))
                        overridePendingTransition(0, 0)
                        return true
                    }


                }
                return false
            }
        })
    }
}