/*
 * Javalin - https://javalin.io
 * Copyright 2017 David Åse
 * Licensed under Apache 2.0: https://github.com/tipsy/javalin/blob/master/LICENSE
 *
 */

package io.javalin.examples;

import io.javalin.Javalin;

import java.io.IOException;

public class HelloWorld {
    public static void main(String[] args) {
        Javalin app = Javalin.create().port(0).start();
        app.get("/", ctx -> ctx.result("Hello World"));
        System.out.println("http://" + app.hostName() + ":" + app.port());
        try {
            System.in.read();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            app.stop();
        }
    }
}
