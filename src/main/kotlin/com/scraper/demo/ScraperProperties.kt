package com.scraper.demo

//Properties for valid images
const val INVALID_IMAGE_FILE_EXTENSION_PATTERN = "([^\\s]+(\\.(?i)(gif|bmp|png))$)"
const val MINIMUM_IMAGE_HEIGHT = 250
const val MINIMUM_IMAGE_WIDTH = 250

const val JSONLD_SELECTOR="script[type=application/ld+json]"

const val PRODUCT_SCHEMA_TYPE="Product"
const val ITEMPAGE_SCHEMA_TYPE="ItemPage"

//UserAgent browsers' String
const val CHROME_69_MOBILE_BROWSER = "Mozilla/5.0 (Linux; Android 6.0.1; SM-G532M Build/MMB29T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Mobile Safari/537.36"
const val CHROME_76_DESKTOP_BROWSER = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36"

//CSS Query Selectors for Jsoup
const val META_TITLE_SELECTOR = "meta[name=title]"
const val OG_TITLE_SELECTOR = "meta[property=og:title]"
const val TWITTER_TITLE_SELECTOR = "meta[name=twitter:title]"
const val OG_IMAGE_SELECTOR = "meta[property=og:image]"
const val TWITTER_IMAGE_SELECTOR = "meta[name=twitter:image]"


const val SHOP_AMAZON= "https://www.amazon.co.uk/gp/product/B01N28AN95?pf_rd_p=330fbd82-d4fe-42e5-9c16-d4b886747c64&pf_rd_r=0EG7W3HNS2YHQ0XQFC4D"
const val SHOP_KONGA= "https://www.konga.com/product/scanfrost-scanfrost-gas-cooker-4-burners-grey-ck5400ng-4096530"
const val SHOP_JUMIA= "https://www.jumia.com.ng/senwei-4.5-kva-key-starter-generator-sv6200e2-30981680.html"
const val SHOP_COLUMBIASPORTWEAR="https://www.columbiasportswear.ie/IE/p/mens-inner-limits-jacket-1714181.html"
const val SHOP_NEXT="https://www.next.ie/en/g23360s37"
const val SHOP_ASOS="https://www.asos.com/the-north-face/the-north-face-vault-backpack-28-litres-in-black/prd/10253008"
const val SHOP_ALIEXPRESS="https://www.aliexpress.com/item/32967475378.html?spm=a2g0o.productlist.0.0.694a1a97NbuIrU&algo_pvid=b15484fe-dd3c-4fe7-ab7f-6d7b2faf5981&algo_expid=b15484fe-dd3c-4fe7-ab7f-6d7b2faf5981-3&btsid=d037a01a-4641-41f7-9424-30216a8aea9e&ws_ab_test=searchweb0_0,searchweb201602_9,searchweb201603_52"
const val SHOP_FLIPKART="https://www.flipkart.com/motorola-one-vision-sapphire-gradient-128-gb/p/itmfhr73hytj4hcf?pid=MOBFFUJ8J7B9EPYT&lid=LSTMOBFFUJ8J7B9EPYTAUGKCT&fm=neo%2Fmerchandising&iid=M_419d4bce-8824-4c52-b91c-4779bccb4b95_6.CAD0KXGBL3IZ&ssid=xetgeot4hs0000001568187338472&otracker=clp_omu_Latest%2BLaunches_1_6.dealCard.OMU_mobile-phones-store_mobile-phones-store_CAD0KXGBL3IZ_6&otracker1=clp_omu_PINNED_neo%2Fmerchandising_Latest%2BLaunches_NA_dealCard_cc_1_NA_view-all_6&cid=CAD0KXGBL3IZ"
const val GOOGLE ="https://www.google.com/search?q=Motorola One Vision (Sapphire Gradient, 128 GB) Motorola&tbm=isch"

