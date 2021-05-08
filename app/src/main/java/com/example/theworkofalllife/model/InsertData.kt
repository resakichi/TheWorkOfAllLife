package com.example.theworkofalllife.model

import android.content.ContentValues
import android.content.Context
import android.content.res.XmlResourceParser
import android.util.Log
import android.widget.Toast
import com.example.theworkofalllife.Commodity
import com.example.theworkofalllife.Composition
import com.example.theworkofalllife.R
import com.example.theworkofalllife.model.PrefenceHelper.customPreference
import com.example.theworkofalllife.model.PrefenceHelper.productID
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

class InsertData(var db: DataBaseHandler, var context: Context) {
    val database = db.writableDatabase

    fun insertProductData(commodity: Commodity){
        var cv = ContentValues()

        cv.put("ID", commodity.id)
        cv.put("NAME", commodity.name)
        cv.put("IMG_PATH", commodity.ImgPath)
        Log.d("INSERT DATA", cv.toString())

        var result = database.insert("PRODUCTS", null, cv)

        if (result == -1.toLong()){
            Toast.makeText(context, "Добавлено", Toast.LENGTH_SHORT).show()
            Log.d("MyLogsDB","Success write products. idk maybe it's error be CAREFUL")
        }
        else{
            //Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
            Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
            Log.d("MyLogsDB","Products write fail" + " " + result)
        }
        db.close()
    }

    fun insertIntoIngredients(){
        var values = ContentValues()
        val res = context.resources

        var file_xml: XmlResourceParser = res.getXml(R.xml.ingredients_record)
        try {
            var eventType = file_xml.eventType
            while (eventType != XmlPullParser.END_DOCUMENT){
                if ((eventType == XmlPullParser.START_TAG)
                    && (file_xml.name.equals("record"))){
                    val name = file_xml.getAttributeValue(0)
                    val description = file_xml.getAttributeValue(1)
                    val secondName = file_xml.getAttributeValue(2)

                    values.put("NAME", name)
                    values.put("DESCRIPTION", description)
                    values.put("SEC_NAME", secondName)

                    database.insert("INGREDIENTS", null, values)
                }
                eventType = file_xml.next()
            }
        }catch (e: XmlPullParserException){
            Log.e("MyTest", e.message, e)
        }catch (e: IOException){
            Log.e("MyTest", e.message, e)
        }finally {
            file_xml.close()
        }
        db.close()
    }

    fun insertCompositionData(composition: Composition){
        var cv = ContentValues()

        cv.put("PROD_ID", composition.id_product)
        cv.put("INGREDIENT_ID", composition.id_ingredient)

        val result = database.insert("INGREDIENTS", null, cv)

        if (result == -1.toLong()){
            Toast.makeText(context, "Ingredients write fail", Toast.LENGTH_SHORT).show()
            Log.d("MyLogsDB","Ingredients write fail")
        }
        else{
            Toast.makeText(context, "Success write products", Toast.LENGTH_SHORT).show()
            Log.d("MyLogsDB","Success write products")
        }
    }
}