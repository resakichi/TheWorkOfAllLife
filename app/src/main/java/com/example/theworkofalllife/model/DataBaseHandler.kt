package com.example.theworkofalllife.model

import android.content.ContentValues
import android.content.Context
import android.content.res.XmlResourceParser
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.example.theworkofalllife.Commodity
import com.example.theworkofalllife.Composition
import com.example.theworkofalllife.R
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

val DATA_BASE_NAME = "AppData"

class DataBaseHandler(var context: Context): SQLiteOpenHelper(context, DATA_BASE_NAME, null, 1){
    override fun onCreate(p0: SQLiteDatabase?) {
        Log.d("MyApp", p0?.version.toString())

        var createTable = "CREATE TABLE PRODUCTS (" +
                "ID INTEGER," +
                "NAME VARCHAR(20), " +
                "IMG_PATH VARCHAR(200)" +
                ");"
        p0?.execSQL(createTable)

        createTable = "CREATE TABLE COMPOSIT(" +
                "PROD_NAME VARCHAR(50)," +
                "INGREDIENT_NAME CHAR(50)" +
                ");"
        p0?.execSQL(createTable)

        createTable = "CREATE TABLE INGREDIENTS(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME CHAR(50)," +
                "DESCRIPTION TEXT," +
                "SEC_NAME CHAR(100)," +
                "DANGER_COLOR INTEGER"+
                ");"
        p0?.execSQL(createTable)
        Log.d(DATA_BASE_NAME, "work")
        var values = ContentValues()
        val res = context.resources

        var file_xml: XmlResourceParser = res.getXml(R.xml.ingredients_record)
        try {
            var eventType = file_xml.eventType
            while (eventType != XmlPullParser.END_DOCUMENT){
                if ((eventType == XmlPullParser.START_TAG)
                    && (file_xml.name.equals("record"))){
                    val name = file_xml.getAttributeValue(2)
                    val description = file_xml.getAttributeValue(1)
                    val second_name = file_xml.getAttributeValue(3)
                    val danger_color = file_xml.getAttributeValue(0)

                    values.put("NAME", name)
                    values.put("DESCRIPTION", description)
                    values.put("SEC_NAME", second_name)
                    values.put("DANGER_COLOR", danger_color)

                    if (p0 != null) {
                        p0.insert("INGREDIENTS", null, values)
                        Log.d("MyLogsDB","Success write ingredients")
                    }else{
                        Log.d("MyLogsDB","Error write ingredients")
                    }
                }
                eventType = file_xml.next()
            }
        }catch (e:XmlPullParserException){
            Log.e("MyTest", e.message, e)
        }catch (e: IOException){
            Log.e("MyTest", e.message, e)
        }finally {
            file_xml.close()
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        Log.w("TestBase", "Upgarading database from version $p1 to $p2, which will destroy all old data")
        val query:String = "DROP TABLE IF EXIST INGREDIENTS".toString()
        p0?.execSQL(query)
        onCreate(p0)
    }
}