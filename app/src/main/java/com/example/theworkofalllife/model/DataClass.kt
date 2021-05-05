package com.example.theworkofalllife

data class Commodity (var id:Int, var name:String, var ImgPath:String){

}
data class Composition (var id_product:Int, var id_ingredient:Int){
}
class Model(val title: String?, val image: String?){
}
/*
class Commodity{
    var id: Int = 0
    var name: String = ""
    var imgPath = ""

    constructor(name:String, imgPath:String){
        this.name = name
        this.imgPath = imgPath
    }
}

class Composition{
    var id_product: Int = 0
    var id_ingredient: Int = 0

    constructor(id_product: Int, id_ingredient:Int){
        this.id_ingredient = id_ingredient
        this.id_product = id_product
    }
}
 */