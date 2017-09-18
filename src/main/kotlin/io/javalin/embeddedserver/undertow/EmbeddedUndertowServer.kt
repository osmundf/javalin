/*
 * Javalin - https://javalin.io
 * Copyright 2017 David Åse
 * Licensed under Apache 2.0: https://github.com/tipsy/javalin/blob/master/LICENSE
 */
package io.javalin.embeddedserver.undertow;

import io.javalin.core.JavalinServlet
import io.javalin.embeddedserver.EmbeddedServer
import io.javalin.embeddedserver.StaticFileConfig
import io.javalin.embeddedserver.jetty.JettyResourceHandler
import io.javalin.embeddedserver.jetty.UndertowResourceHandler
import io.undertow.Handlers
import io.undertow.Undertow
import io.undertow.servlet.Servlets
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.util.HashMap

class EmbeddedUndertowServer(private val javalinServlet: JavalinServlet, private val staticFileConfig: StaticFileConfig?) : EmbeddedServer {

    private val log = LoggerFactory.getLogger(EmbeddedServer::class.java)

    private val attributeMap = HashMap<String, Any>()
    
    private var undertow: Undertow = Undertow.builder().build()
    
    override fun start(host: String, port: Int): Int {
        
        javalinServlet.apply { staticResourceHandler = UndertowResourceHandler(staticFileConfig) }
        
        val servletBuilder = Servlets.deployment()
                .setClassLoader(EmbeddedUndertowServer::class.java.getClassLoader())
                .setContextPath("/")
                .setDeploymentName("javalinDeployment")
                .addServletContextAttribute("javalin-servlet", javalinServlet)
                .addServlets(Servlets.servlet("javalinServlet", UndertowServlet::class.java).addMapping("/"))
        val manager = Servlets.defaultContainer().addDeployment(servletBuilder)
        manager.deploy()
        val httpHandler = manager.start()
        val path = Handlers.path(Handlers.redirect("/")).addPrefixPath("/", httpHandler)
        this.undertow = Undertow.builder().addHttpListener(port, host).setHandler(path).build()
        undertow.start()
        
        return (undertow.getListenerInfo().get(0).getAddress() as InetSocketAddress).port
    }

    override fun stop() = undertow.stop()
    
    override fun activeThreadCount() = -1

    override fun attribute(
            key: String): Any {
        val value = attributeMap[key]
        return value ?: ""
    }

}