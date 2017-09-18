/*
 * Javalin - https://javalin.io
 * Copyright 2017 David Åse
 * Licensed under Apache 2.0: https://github.com/tipsy/javalin/blob/master/LICENSE
 */

package io.javalin.embeddedserver.jetty

import io.javalin.embeddedserver.StaticFileConfig
import io.javalin.embeddedserver.StaticResourceHandler
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class UndertowResourceHandler(staticFileConfig: StaticFileConfig?) : StaticResourceHandler {

    private val log = LoggerFactory.getLogger(UndertowResourceHandler::class.java)

    private var initialized = false

    init {
    }

    override fun handle(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse): Boolean {
        return false
    }

}
