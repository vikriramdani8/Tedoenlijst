package com.example.tedoenlijst.Model

class Category {
    var id_category: Int = 0
    var name_category:String? = null
    var jumlahTask: Int = 0

    constructor(){}

    constructor(id_category:Int, name_category:String, jumlahTask: Int){
        this.id_category = id_category
        this.name_category = name_category
        this.jumlahTask = jumlahTask
    }

    constructor(id_category:Int, name_category:String){
        this.id_category = id_category
        this.name_category = name_category
    }
}