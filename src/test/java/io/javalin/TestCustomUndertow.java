package io.javalin;

import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import io.javalin.core.JavalinServlet;
import io.javalin.embeddedserver.EmbeddedServer;
import io.javalin.embeddedserver.EmbeddedServerFactory;
import io.javalin.embeddedserver.StaticFileConfig;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.resource.PathResourceManager;

import static org.hamcrest.CoreMatchers.is;

/**
 * @author <a href="mailto:osmund.francis@gmail.com">Osmund Francis</a>
 */
public class TestCustomUndertow {

    @Test
    public void test_embeddedServer_setsCustomServer()
        throws Exception {

        EmbeddedServerFactory factory = CustomServer::new;

        Javalin app = Javalin.create()
            .hostName("localhost").port(8080)
            .embeddedServer(factory)
            .start();
        MatcherAssert.assertThat(app, Matchers.notNullValue());
        MatcherAssert.assertThat(app.embeddedServer(), Matchers.notNullValue());
        MatcherAssert.assertThat(app.embeddedServer().attribute("is-custom-server"), is(true));
        MatcherAssert.assertThat(app.embeddedServer().activeThreadCount(), Matchers.greaterThan(0));
        app.stop();
    }

    private class CustomServer implements EmbeddedServer {

        private Map<String, Object> attributeMap = new HashMap<>();

        private Undertow server = null;

        @SuppressWarnings("WeakerAccess")
        public CustomServer(JavalinServlet javalinServlet, StaticFileConfig staticFileConfig) {
            attributeMap.put("javalinServlet", javalinServlet);
            attributeMap.put("staticFileConfig", staticFileConfig);
            attributeMap.put("is-custom-server", true);
        }

        @Override
        public int start(
            @NotNull
                String host, int port)
            throws Exception {

            if (server != null) {
                throw new Exception("Server already started.");
            }

            server = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(Handlers.resource(new PathResourceManager(Paths.get("."), 100)).setDirectoryListingEnabled(true))
                .build();
            server.start();

            return ((InetSocketAddress) server.getListenerInfo().get(0).getAddress()).getPort();
        }

        @Override
        public void stop()
            throws Exception {
            server.stop();
            server = null;
        }

        @Override
        public int activeThreadCount() {
            return server != null && server.getWorker() != null ? server.getWorker().getIoThreadCount() : -1;
        }

        @NotNull
        @Override
        public Object attribute(
            @NotNull
                String key) {
            Object value = attributeMap.get(key);
            return value != null ? value : "";
        }
    }

}
