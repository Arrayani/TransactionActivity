package academy.learnprogramming.utils

import android.text.Editable
import java.text.DecimalFormat

/**

 * Author: Roni Reynal Fitri  on $ {DATE} $ {HOUR}: $ {MINUTE}

 * Email: biruprinting@gmail.com

 */
class ValidNumber {

    //ini untuk mengkonversi dari tabel array ke format idr number
     fun deciformat(terimaString:String):String {
        val konversiLong = terimaString.toLong()
        val dcFormat = DecimalFormat("#,###")
        val hasiDeci = dcFormat.format(konversiLong).toString().replace(',','.')
        return hasiDeci
    }

    fun removedot(terimaString: String):String{
        if (terimaString.contains(".")){
            val newterimaString = terimaString.replace(".", "")
            return newterimaString
        }else
            return terimaString
    }
}