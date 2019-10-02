package com.scraper.demo

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.scraper.demo.model.Product
import com.scraper.demo.util.*
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import org.jsoup.select.Elements
import java.util.logging.Level
import java.util.logging.Logger


// this class uses HtmlUnit to get the html page as a string
// while Jsoup is used to traverse the documents in order to get the necessary data
class JsoupHtmlUnitProductScraper (var url:String) {


    private val LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
    private var product= Product()
    private var htmlDocument: Document

    init {
        val webClient = WebClient(BrowserVersion.CHROME)
        webClient.options.timeout = 60000
        webClient.options.isRedirectEnabled = true
        webClient.options.isJavaScriptEnabled = false
      //  webClient.javaScriptTimeout = 30000 //e.g. 30s . Use this incase you want to speed up scraping process
        webClient.options.isThrowExceptionOnFailingStatusCode = false
        webClient.options.isThrowExceptionOnScriptError = false
        webClient.options.isCssEnabled = false
        webClient.options.isUseInsecureSSL = true

        // continues even when there was an error executing javascripts on the html page
        webClient.options.isThrowExceptionOnScriptError = false

        webClient.options.isThrowExceptionOnFailingStatusCode = false

        var htmlString  = webClient.getPage<HtmlPage>(url).asXml()

        //LOGGER.info("This is the html page : " + htmlString)
        htmlDocument = Jsoup.parse(htmlString)
        LOGGER.level = Level.INFO
    }

    /*
    ------------------------  PUBLIC METHODS -------------------------------------------------------------
     */

    //process Scraping
    fun processScraping(): Product{

        processByHtml()

        // Get the acceptable schema type (ie of Product or ItemPage) as there could be more than one jsonld schema file on a single page
        val jsonList = fetchFilteredJsonLd()
        println("There is/are {${jsonList.count()}} acceptable json list(s) ")

        if (jsonList.isNotEmpty()){
            // scrape from xml derived from jsonld schema
            println("i am scraping by json ld schema")
            val mergedXml = convertAndMergeJSONtoXML(jsonList)
            val outputProduct = processByJsonLd(mergedXml)
            this.product.price = outputProduct.price
            this.product.priceCurrency = outputProduct.priceCurrency

            if (isNullOrEmpty(this.product.mainImage))
                this.product.mainImage = outputProduct.mainImage
        }

        return product
    }



    /*
    ------------------------  HELPER METHODS -------------------------------------------------------------
     */

    private fun processByHtml(): Product {
        product.name = fetchTitle()
        product.url= fetchUrl()
        product.mainImage = fetchImageFromSocial()
        product.images = fetchValidImagesFromPage()
        return product
    }

    //Fetches Title of item/product
    private fun fetchTitle(): String {

        var title: String = ""
        var condition = true
        var position = 1
        while (condition) {

            when (position) {
                1 -> {
                    title = fetchTitleFromMetaTitle()
                    if (isNullOrEmpty(title)) // if it can't be found , increment position in order to move to the next algorithm
                        position++
                    else
                        condition = false
                }

                2 -> {
                    title = fetchTitleFromMetaFacebookOG()
                    if (isNullOrEmpty(title))
                        position++
                    else
                        condition = false
                }

                3 -> {
                    title = fetchTitleFromMetaTwitter()
                    if (isNullOrEmpty(title))
                        position++
                    else
                        condition = false
                }

                4 -> {
                    title = fetchTitleFromTitleTag()
                    if (isNullOrEmpty(title))
                        position++
                    else
                        condition = false
                }

                else -> {
                    condition = false
                }

            } // end when
        } // end while loop
        return title
    }


    //Fetches Url of item/product
    private fun fetchUrl(): String {
        return url
    }

    //Fetches image of product/item from facebook og & twitter
    private fun fetchImageFromSocial(): String {

        var imageUrl: String = ""
        var condition = true
        var position = 1
        while (condition) {

            when (position) {
                1 -> {
                    imageUrl = fetchImageFromMetaFacebookOG()
                    if (isNullOrEmpty(imageUrl)) // if it can't be found , increment position in order to move to the next algorithm
                        position++
                    else
                        condition = false
                }

                2 -> {
                    imageUrl = fetchImageFromMetaTwitter()
                    if (isNullOrEmpty(imageUrl))
                        position++
                    else
                        condition = false
                }

                else -> {
                    condition = false
                }

            } // end when
        } // end while loop
        return imageUrl
    }

    // fetches all valid image url from <img> tags found in the html page, scraping out all gifs, small size pictures
    private fun fetchValidImagesFromPage(): MutableList<String> {
        val imageUrlList = fetchAllImagesFromPage()
        val validImageUrlList = mutableListOf<String>()
        var validImageUrlListWithoutDuplicates: MutableList<String>
        for (imageUrl in imageUrlList) {

            if (isImageValid(imageUrl, INVALID_IMAGE_FILE_EXTENSION_PATTERN) &&
                    isUrlValid(imageUrl) &&
                    isImageOfValidSize(imageUrl, MINIMUM_IMAGE_HEIGHT, MINIMUM_IMAGE_WIDTH) ){ // checks if the imageUrl is valid ie it is not a GIF or BMP
                validImageUrlList.add(imageUrl)
                println("This is a valid image : " + imageUrl)
            }

        }

        validImageUrlListWithoutDuplicates = removeDuplicatesFromStringList(validImageUrlList)

        for (imageUrl in validImageUrlListWithoutDuplicates) {
            println("Without duplicates: This is a valid image : " + imageUrl)
        }


        println("There are ${imageUrlList.size} total images extracted from this url")
        println("There are ${validImageUrlList.size} valid images ")
        println("There are ${validImageUrlListWithoutDuplicates.size} valid images after removing duplicates ")
        return validImageUrlListWithoutDuplicates
    }



    private fun fetchTitleFromMetaTitle(): String {
        val title = htmlDocument.select(META_TITLE_SELECTOR).attr("content")
        LOGGER.info( "Product Title from MetaTitle : " + title)
        return title
    }

    private fun fetchTitleFromMetaFacebookOG(): String {
        val title = htmlDocument.select(OG_TITLE_SELECTOR).attr("content")
        LOGGER.info("Product Title from MetaFacebookOG : " + title)
        return title
    }

    private fun fetchTitleFromMetaTwitter(): String {
        val title = htmlDocument.select(TWITTER_TITLE_SELECTOR).attr("content")
        LOGGER.info("Product Title from MetaTwitter : " + title)
        return title
    }

    private fun fetchTitleFromTitleTag(): String {
        val title = htmlDocument.title()// htmlDocument.select("title").get(0).text()
        LOGGER.info("Product Title from TitleTag : " + title)
        return title
    }

    private fun fetchImageFromMetaFacebookOG(): String {
        val image = htmlDocument.select(OG_IMAGE_SELECTOR).attr("content")
        LOGGER.info("Product Image from MetaFacebookOG : " + image)
        return image
    }

    private fun fetchImageFromMetaTwitter(): String {
        val image = htmlDocument.select(TWITTER_IMAGE_SELECTOR).attr("content")
        LOGGER.info("Product Image from MetaTwitter : " + image)
        return image
    }

    // fetch all links to images
    private fun fetchAllImagesFromPage(): MutableList<String> {
        val images = htmlDocument.select("img")
        val imageUrlList = mutableListOf<String>()
        for (image in images) {

            if (image.hasAttr("data-src")){
                println("This is a data-src image : " + image.attr("abs:data-src"))
                imageUrlList.add(image.attr("abs:data-src"))
            }
            //This gets the absolute url to the image using "abs:src" rather than just using "src"
            println("This is the absolute url of image : " + image.attr("abs:src"))
            imageUrlList.add(image.attr("abs:src"))

        }

        return imageUrlList
    }





    /*
        JSON
     */

    // returns a list of acceptable JsonLd files from this html page; the acceptable ones are of "Product" and "ItemPage"
    private fun fetchFilteredJsonLd(): MutableList<String> {
        val elements = htmlDocument.select(JSONLD_SELECTOR) //htmlDocument.getElementsByTag("script").attr("type", "application/ld+json")
        val jsonList = mutableListOf<String>()

        for (jsonldElement in elements) {

            // only begin to perform operations on the json data if it is not empty
            if (!isNullOrEmpty(jsonldElement.data())){
                if (getSchemaEntityTypeFromJsonLd(jsonldElement.data())  == PRODUCT_SCHEMA_TYPE || getSchemaEntityTypeFromJsonLd(jsonldElement.data()) ==ITEMPAGE_SCHEMA_TYPE){
                    jsonList.add(jsonldElement.data())
                    // LOGGER.info("This is the raw json output : " + jsonldElement.data())
                }
            }


        }

        return jsonList

    }

    // returns the @type of a Schema eg Product,ItemPage etc
    private fun getSchemaEntityTypeFromJsonLd(jsonObject:String):String{
        val formattedJson = formatJsonToValidJSonObject(jsonObject)
        val jsonResult = JSONObject(formattedJson)
        // LOGGER.info("The Schema type for this json is : " + jsonResult.get("@type"))
        return jsonResult.get("@type") as String

    }



    private fun processByJsonLd(xml: String): Product {

        val product = Product()
        product.name = fetchTitleFromXml(xml)
        product.price = fetchPriceFromXml(xml)
        product.priceCurrency= fetchPriceCurrencyFromXml(xml)
        product.images = fetchImageUrlFromXml(xml)
        product.url = url

        return product
    }


    private fun fetchImageUrlFromXml(xml:String): MutableList<String> {
        val imageUrlList = mutableListOf<String>()
        var imageUrlElements = extractFromXMLByCssQuery(xml,"image > contentUrl")

        if (imageUrlElements.isEmpty()) {
            imageUrlElements = extractFromXMLByCssQuery(xml, "image")
        }

        for (imageUrlElement in imageUrlElements) {
            imageUrlList.add(imageUrlElement.text())
        }


        LOGGER.info("The imageUrl of this product is  : " + imageUrlList)

        return imageUrlList

    }

    private fun fetchTitleFromXml(xml:String): String {
        //first look for the product name within the <mainEntity> <name>.... </name> </mainEntity>
        var productTitle:String = extractFromXMLByCssQuery(xml,"mainEntity > name").text()

        //if it name was not found in the above query , then look for it in <name>
        if (isNullOrEmpty(productTitle))
            productTitle= extractFromXMLByCssQuery(xml,"name").text()


        LOGGER.info("The name of this product is  : " + productTitle)

        return productTitle

    }

    private fun fetchPriceFromXml(xml:String): String {
        //first look for the product price within the <offers> <price>.... </price> </offers>
        var productPrice:String = extractFromXMLByCssQuery(xml,"offers > price").text()

        //if it name was not found in the above query , then look for it in <price>
        if (isNullOrEmpty(productPrice)) {
            productPrice = extractFromXMLByCssQuery(xml, "price").text()
        }
        //if it name was not found in the above query , then look for it the <offers> <highPrice>.... </highPrice> </offers>
        else if (isNullOrEmpty(productPrice)) {
            productPrice = extractFromXMLByCssQuery(xml, "offers > highPrice").text()
        }
        //if it name was not found in the above query , then look for it in <highPrice>
        else if (isNullOrEmpty(productPrice)) {
            productPrice = extractFromXMLByCssQuery(xml, "highPrice").text()
        }

        LOGGER.info("The price of this product is  : " + productPrice)

        return productPrice

    }

    private fun fetchPriceCurrencyFromXml(xml:String): String {
        //first look for the product priceCurrency within the <offers> <priceCurrency>.... </priceCurrency> </offers>
        var productPriceCurrency:String = extractFromXMLByCssQuery(xml,"offers > priceCurrency").text()

        //if it name was not found in the above query , then look for it in <priceCurrency>
        if (isNullOrEmpty(productPriceCurrency))
            productPriceCurrency= extractFromXMLByCssQuery(xml,"priceCurrency").text()


        LOGGER.info("The priceCurrency of this product is  : " + productPriceCurrency)

        return productPriceCurrency

    }

    private fun extractFromXMLByCssQuery(xml:String,cssQuery:String):Elements{
        val doc = Jsoup.parse(xml, "", Parser.xmlParser())
        return doc.select(cssQuery)
    }

}