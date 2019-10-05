package com.scraper.demo


import com.scraper.demo.model.Product
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {

    @RequestMapping("/scrape")
    fun index(@RequestParam url:String): Product {
       // val query ="Motorola One Vision (Sapphire Gradient, 128 GB) Motorola"
       // val url="https://www.google.co.in/search?q="+query+"&source=lnms&tbm=isch"
        val product = JsoupHtmlUnitProductScraper (url).processScraping()

        return product
    }

}