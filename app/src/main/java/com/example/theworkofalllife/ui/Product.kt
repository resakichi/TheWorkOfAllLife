package com.example.theworkofalllife.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.theworkofalllife.Commodity
import com.example.theworkofalllife.R
import com.example.theworkofalllife.model.DataBaseHandler
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.custom_dialog.view.*

class product : AppCompatActivity() {

    lateinit var name_prod:String
    lateinit var img_path:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        var returnIntent = Intent()
        var tmp_list = findViewById<ListView>(R.id.list_prod)
        //Заполнение списка
        var bundle: Bundle? = intent.extras
        img_path = bundle!!.getString("IMG_PATH").toString()
        var listData = bundle!!.getStringArrayList("RECIVED_DATA")

        if (listData != null) {
            if (listData.isEmpty()) {
                informationResult.text = "Пищевых добавок не найдено"
                add_cart.visibility = View.GONE

            } else {
                var adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    listData!!.toTypedArray()
                )
                tmp_list.adapter = adapter

                //нажатие на элемент
                tmp_list.setOnItemClickListener { adapterView, view, i, l ->

                    returnIntent.putExtra("LAUNCH", "composit")
                    returnIntent.putExtra("result", listData!![i].toString())

                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
            }
        }
        //Image
        val image = BitmapFactory.decodeFile(img_path)
        imgProduct.setImageBitmap(image)

        back_to_main.setOnClickListener{
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()
        }

        //Добавление в историю
        add_cart.setOnClickListener {
            val customLayout: View = layoutInflater.inflate(R.layout.custom_dialog, null)
            var builder: AlertDialog.Builder =
                AlertDialog.Builder(this)
                    .setView(customLayout)
                    .setTitle("Добавление товара")
            val mBuilder = builder.show()
            builder.setCancelable(false)

            customLayout.agree.setOnClickListener {
                mBuilder.dismiss()
                name_prod = customLayout.ed_textDialog.text.toString()
                writeData(name_prod, img_path, 1)
                returnIntent.putExtra("LAUNCH", "history")
                returnIntent.putExtra("result", name_prod)
                finish()
            }
            customLayout.disagree.setOnClickListener { mBuilder.dismiss() }
        }

    }

    fun writeData(name: String, image: String, id:Int) {
        val db = DataBaseHandler(this)
        val data_prod = Commodity(id, name, image)
        Log.d("Product", data_prod.ImgPath.toString() + " " + data_prod.name)
        db.insertProductData(data_prod)
    }
}