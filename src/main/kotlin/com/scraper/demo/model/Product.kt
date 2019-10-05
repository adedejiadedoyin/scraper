package com.scraper.demo.model

class Product {
    var link:String="" // link to the item
    var title:String=""
    var amount:String="00.00" // price of the item
    var description:String=""
    var mainImage:String=""
    lateinit var images:List<String>
}