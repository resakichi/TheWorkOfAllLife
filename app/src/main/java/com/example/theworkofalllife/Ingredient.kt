package com.example.theworkofalllife

import android.annotation.SuppressLint
import android.content.ClipData
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import com.example.theworkofalllife.ui.search
import kotlinx.android.synthetic.main.fragment_ingredient.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [Ingredient.newInstance] factory method to
 * create an instance of this fragment.
 */
class Ingredient : Fragment() {

    private var item: ClipData.Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_ingredient, container, false)
        return root
    }

    @SuppressLint("ResourceAsColor")
    override fun onStart() {
        super.onStart()
        val view: View? = view
        val position = (activity as MainActivity).ing_position
        val information = (activity as MainActivity).readDataBaseIngredient(position)
        if (view!= null){
            val name: TextView = view.findViewById(R.id.SecondNameIngredient) as TextView
            val description: TextView = view.findViewById(R.id.DescriptionIngredient) as TextView
            val secondName: TextView = view.findViewById(R.id.IngredientName) as TextView

            Log.d("Ingredient DATA", information.toString())
            name.setText(information[0])
            description.setText(information[1])
            secondName.setText(information[2])
            if (information[3].toInt() == 1) {
                view.findViewById<Button>(R.id.danger_color_btn)
                    .setBackgroundResource(R.drawable.btn_rounded_corners_other)
            }else if (information[3].toInt() == 3) {
                view.findViewById<Button>(R.id.danger_color_btn)
                    .setBackgroundResource(R.drawable.btn_rounded_corners_red)
            }else if (information[3].toInt() == 3){
                view.findViewById<Button>(R.id.danger_color_btn)
                    .setBackgroundResource(R.drawable.btn_rounded_corners_orange)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        back_search.setOnClickListener{
            (activity as MainActivity).makeCurrentfragment(search())
        }
    }

    companion object {

    }
}
