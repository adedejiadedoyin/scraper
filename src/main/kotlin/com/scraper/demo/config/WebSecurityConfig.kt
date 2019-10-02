package com.scraper.demo.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter


@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter()  {

    @Throws(Exception::class)
    protected override fun configure(http: HttpSecurity) {
        /*
        http.cors().configurationSource {
            CorsConfiguration().applyPermitDefaultValues()
        }

         */
    }
}