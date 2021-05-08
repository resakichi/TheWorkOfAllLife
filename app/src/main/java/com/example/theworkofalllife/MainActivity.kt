package com.example.theworkofalllife

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.theworkofalllife.model.DataBaseHandler
import com.example.theworkofalllife.tesseract.FileUtil
import com.example.theworkofalllife.ui.*
import com.googlecode.tesseract.android.TessBaseAPI
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils.bitmapToMat
import org.opencv.android.Utils.matToBitmap


private const val REQUEST_CODE = 42
private const val REQUEST_TAKE_PHOTO = 1
private const val REQUEST_PRODUCT = 2

class MainActivity : AppCompatActivity() {

    //tesseract
    var currentPhotoPath: String? = null
    lateinit var recivedText: String
    private var imageUri: Uri? = null

    lateinit var listNames: Array<String>
    lateinit var list_adapter: ArrayAdapter<String>
    var ing_position = 0
    var name_card:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        makeCurrentfragment(home())
        readDatabaseSearch()

        if (OpenCVLoader.initDebug()){
            Log.d("opencv", "success")
        }else{
            Log.d("opencv", "sucks")
        }

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> makeCurrentfragment(home())
                R.id.history -> makeCurrentfragment(history())
                R.id.search -> makeCurrentfragment(search())
            }
            true
        }
    }

    @Throws (IOException::class)
    private fun createImage(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}",
            ".jpg",
            storageDir
        ).apply { currentPhotoPath = absolutePath }
    }

    fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE). also{ takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try{
                    createImage()
                }catch (ex:IOException){
                    null
                }
                photoFile?.also {
                    val photoUri: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.theworkofalllife.fileprovider",
                        it)
                    //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    //startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                    CropImage.activity(imageUri).start(this@MainActivity)
                }
            }}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val result = CropImage.getActivityResult(data)
                currentPhotoPath = result.uri.path.toString()

                val takenImage = BitmapFactory.decodeFile(result.uri.path)
                //ConvertTask().execute(File(result.uri.path))
                recivedText = extractText(takenImage)

                startProduct()
            }
        }else if (requestCode == REQUEST_PRODUCT){
            if (resultCode == RESULT_OK) {
                val result = data?.getStringExtra("result")
                val launch = data?.getStringExtra("LAUNCH")

                if (launch == "composit") {
                    if (result != null) {
                        val n = listNames.size
                        //var ind = 0
                        for (i in 0..n - 1) {
                            if (listNames[i] == result) {
                                ing_position = i
                            }
                        }
                        makeCurrentfragment(Ingredient())
                    }
                }
                else if (launch == "history"){
                    if (result != null){
                        currentPhotoPath = takeImage(result)
                        makeCurrentfragment(history())
                    }
                }
            }
            else{
                Log.d("MyLogs", "Result data is null. \n Result code:" + resultCode.toString())
            }
        }
    }

    private fun startProduct() {
        if (recivedText == null) recivedText = "Null"
        val intent = Intent(this@MainActivity, product::class.java)
        val recivedData = coincidences()
        intent.putExtra("IMG_PATH", currentPhotoPath)
        intent.putStringArrayListExtra("RECIVED_DATA", recivedData)
        //intent.putExtra("RECIVED_DATA", recivedData)
        startActivityForResult(intent, REQUEST_PRODUCT)
    }

/*
    @Throws(Exception::class)
    private fun extractText(bitmap: Bitmap): String{

        var tessBaseAPI: TessBaseAPI = TessBaseAPI()
        val datapath = "$filesDir/tesseract/"
        FileUtil.checkFile(
            this@MainActivity,
            datapath.toString(),
            File(datapath + "tessdata")
        )
        Log.d("MyLogs", datapath)
        tessBaseAPI.init(datapath, "eng")
        tessBaseAPI.setImage(bitmap)
        var extractedText: String = tessBaseAPI.utF8Text
        Log.d("Extracted text", extractedText)
        tessBaseAPI.end()
        Log.d("Data", extractedText)
        return extractedText
    }
*/

    //улучшить для распознания текста на чёрном фоне
    @Throws(Exception::class)
    private fun extractText(bitmap: Bitmap): String{

        val tmp = Mat(bitmap.getWidth(), bitmap.getHeight(),CvType.CV_8UC1)
//        val bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        bitmapToMat(bitmap, tmp)

        val gray = Mat(bitmap.getWidth(), bitmap.getHeight(),CvType.CV_8UC1)
        Imgproc.cvtColor(tmp, gray, Imgproc.COLOR_RGB2GRAY)

        val destination = Mat(bitmap.getWidth(), bitmap.getHeight(),CvType.CV_8UC1)
        Imgproc.adaptiveThreshold(gray, destination, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 15, 4.0)
        //Imgproc.threshold(mat, mat, 244.0 , 255.0, Imgproc.THRESH_BINARY)
        //Core.bitwise_not(mat, mat)

        val preparedImage: Bitmap? = null
        matToBitmap(destination, bitmap)

        var tessBaseAPI: TessBaseAPI = TessBaseAPI()
        val datapath = "$filesDir/tesseract/"

        FileUtil.checkFile(
            this@MainActivity,
            datapath.toString(),
            File(datapath + "tessdata")
        )
        Log.d("MyLogs", datapath)

        tessBaseAPI.init(datapath, "eng")
        tessBaseAPI.setImage(bitmap)

        var extractedText: String = tessBaseAPI.utF8Text
        Log.d("Extracted text", extractedText)
        tessBaseAPI.end()
        Log.d("Data", extractedText)
        return extractedText
    }


    fun makeCurrentfragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
    }

    fun readDatabaseSearch(){
        val db = DataBaseHandler(this)
        var dbCursor = db.writableDatabase.rawQuery("SELECT NAME, DESCRIPTION " +
                "FROM INGREDIENTS", null)

        var listData: MutableList<String> = mutableListOf()

        if (dbCursor.moveToFirst()){
            while (!dbCursor.isAfterLast){
                var data = dbCursor.getString(dbCursor.getColumnIndex("NAME"))
                Log.d("DatabaseData", data)
                listData.add(data)
                data = dbCursor.getString(dbCursor.getColumnIndex("DESCRIPTION"))
//                Log.d("DatabaseData", data)
                dbCursor.moveToNext()
            }
        }
        dbCursor.close()

        listNames = listData.toTypedArray()
        list_adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listNames)
        db.close()
    }

    fun readDataBaseIngredient(id_ingr: Int): ArrayList<String> {
        val db = DataBaseHandler(this)
        val name = listNames[id_ingr]
        var dbCursor = db.writableDatabase.rawQuery("SELECT NAME, DESCRIPTION, SEC_NAME, DANGER_COLOR " +
                "FROM INGREDIENTS " +
                "WHERE NAME = '$name';", null)
        var result =  arrayListOf<String>()

        if (dbCursor.moveToFirst()) {
            while (!dbCursor.isAfterLast) {
                var data = dbCursor.getString(dbCursor.getColumnIndex("NAME"))
                result.add(data)
                data = dbCursor.getString(dbCursor.getColumnIndex("DESCRIPTION"))
                result.add(data)
                data = dbCursor.getString(dbCursor.getColumnIndex("SEC_NAME"))
                result.add(data)
                data = dbCursor.getString(dbCursor.getColumnIndex("DANGER_COLOR"))
                result.add(data)
                Log.d("MainData", result.toString())
                dbCursor.moveToNext()
            }
        }
        dbCursor.close()
        db.close()
        return result
    }

    //записывает в истоию ингредиенты (но это не точно)
    private fun coincidences(): ArrayList<String>{
        var composits = arrayListOf<String>()
        var n = listNames.size

        for (i in 0..n-1){
            var result:String? = null
            val search = listNames[i].toRegex()

            try {
                result = search.find(recivedText)!!.toString()
                if (result != null) {
                    composits.add(listNames[i])
                    Log.d("Composits", listNames[i])
                }
            }
            catch (e: NullPointerException){
                Log.d("Composits", "Argument, don't write")
            }
        }
        return composits
    }

    private fun takeImage(name_img: String): String? {
        val db = DataBaseHandler(this)
        var dbCursor = db.writableDatabase.rawQuery("SELECT NAME, IMG_PATH " +
                "FROM PRODUCTS " +
                "WHERE NAME = '$name_img';", null)
        var data:String? = null

        if (dbCursor.moveToFirst()) {
            while (!dbCursor.isAfterLast) {
                data = dbCursor.getString(dbCursor.getColumnIndex("IMG_PATH"))
                dbCursor.moveToNext()
            }
            dbCursor.close()
            return data
        }
        else {
            Log.d("DataBase", "Take image fail")
            return null
        }
        db.close()
    }
}