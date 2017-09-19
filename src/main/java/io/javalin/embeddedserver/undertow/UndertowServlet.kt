package io.javalin.embeddedserver.undertow

import io.javalin.core.JavalinServlet
import java.io.IOException
import javax.servlet.*

/**
 * @author <a href="mailto:osmund.francis@gmail.com">Osmund Francis</a>
 */
class UndertowServlet : Servlet {

    private var config: ServletConfig? = null

    private var javalinServlet: JavalinServlet? = null

    @Throws(ServletException::class)
    override fun init(config: ServletConfig) {
        this.config = config
        javalinServlet = config.servletContext.getAttribute("javalin-servlet") as JavalinServlet
    }

    @Throws(ServletException::class, IOException::class)
    override fun service(request: ServletRequest, response: ServletResponse) {
        if (javalinServlet != null) {
            (javalinServlet as JavalinServlet).service(request, response)
        }
    }

    override fun destroy() {}

    override fun getServletConfig(): ServletConfig {
        return this.config as ServletConfig
    }

    override fun getServletInfo(): String? {
        return null
    }

}