package com.example.theworkofalllife.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theworkofalllife.MainActivity
import com.example.theworkofalllife.Model
import com.example.theworkofalllife.MyAdapter
import com.example.theworkofalllife.R
import com.example.theworkofalllife.model.DataBaseHandler
import kotlinx.android.synthetic.main.fragment_history.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [history.newInstance] factory method to
 * create an instance of this fragment.
 */
class history : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val arrayList = ArrayList<Model>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add_btn.setOnClickListener {
            val product_data = readData()
            fullCardView(product_data)
        }
    }

    private fun readData(): Array<Array<String>> {
        val db = DataBaseHandler(requireContext())
        val cursor = db.writableDatabase.rawQuery("SELECT * FROM PRODUCTS", null)
        val result: MutableList<Array<String>> = mutableListOf()
        if (cursor.moveToFirst()){
            var data: MutableList<String> = mutableListOf()
            while (!cursor.isAfterLast) {
                data.add(cursor.getString(cursor.getColumnIndex("ID")))
                Log.d("HISTORY DATA", "ID:" + data[0])

                data.add( cursor.getString(cursor.getColumnIndex("NAME")))
                Log.d("HISTORY DATA", "NAME:" + data[1])

                data.add(cursor.getString(cursor.getColumnIndex("IMG_PATH")))
                Log.d("HISTORY DATA", "IMG_PATH:" + data[2])
                result.add(data.toTypedArray())
                cursor.moveToNext()
            }
        }
        cursor.close()
        db.close()
        return result.toTypedArray()
    }

    private fun fullCardView(data: Array<Array<String>>) {
        //val appContext = requireContext().applicationContext
        //val image = (activity as MainActivity).currentPhotoPath
        //val name = (activity as MainActivity).name_card
        data.forEach {
            arrayList.add(Model(it[1], it[2]))
        }
        val gridLayout = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        val myAdapter = MyAdapter(arrayList, requireContext())
        recyclerView.layoutManager = gridLayout
        recyclerView.adapter = myAdapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment history.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                history().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}