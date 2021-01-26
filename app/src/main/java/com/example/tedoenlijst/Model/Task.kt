package com.example.tedoenlijst.Model

class Task {
    var id_task: Int = 0
    var id_category: Int = 0
    var name_task:String? = null
    var date_task:String? = null
    var time_task:String? = null
    var repeat: Int = 0
    var task_active: Int = 0
    var pemisah: String? = null

    constructor(){}
    constructor(id_task: Int){
        this.id_task = id_task
    }
    constructor(pemisah: String){
        this.pemisah = pemisah
    }

    constructor(id_task: Int, task_active: Int){
        this.id_task = id_task
        this.task_active = task_active
    }

    constructor(
        id_task: Int,
        id_category: Int,
        name_task: String,
        date_task: String,
        time_task: String,
        repeat: Int,
        task_active: Int
    ){
        this.id_task = id_task
        this.id_category = id_category
        this.name_task = name_task
        this.date_task = date_task
        this.time_task = time_task
        this.repeat = repeat
        this.task_active = task_active
    }
}