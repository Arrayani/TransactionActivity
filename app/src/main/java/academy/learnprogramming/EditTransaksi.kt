package academy.learnprogramming

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView

class EditTransaksi : AppCompatActivity() {
    private lateinit var   bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaksi)

        navigasiBawah()
    }

    private fun navigasiBawah() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.editTransaksi

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
                        startActivity(Intent(applicationContext, InputItem::class.java))
                        overridePendingTransition(0, 0)
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