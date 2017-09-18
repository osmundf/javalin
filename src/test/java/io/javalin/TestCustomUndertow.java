package io.javalin;

import io.javalin.core.JavalinServlet;
import io.javalin.embeddedserver.EmbeddedServer;
import io.javalin.embeddedserver.EmbeddedServerFactory;
import io.javalin.embeddedserver.StaticFileConfig;
import io.undertow.Undertow;
import org.hamcrest.MatcherAssert;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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
			.hostName("localhost").port(0)
			.embeddedServer(factory)
			.start();
		MatcherAssert.assertThat(app.embeddedServer().attribute("is-custom-server"), is(true));
		app.stop();
	}

	private class CustomServer implements EmbeddedServer {

		private Map<String, Object> attributeMap = new HashMap<>();

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
			return port;
		}

		@Override
		public void stop()
		throws Exception {
		}

		@Override
		public int activeThreadCount() {
			return -1;
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
