package org.kakara.client;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.util.jar.JarFile;

public class Agent {
    private static Instrumentation inst = null;

    public static void premain(final String agentArgs, final Instrumentation inst) {
        Agent.inst = inst;
    }

    public static void agentmain(final String agentArgs, final Instrumentation inst) {
        Agent.inst = inst;
    }

    @SuppressWarnings("unused")
    static void addToClassPath(final Path jar) {
        if (inst == null) {
            System.err.println("Unable to load jar needed");
            System.exit(1);
            return;
        }
        try {
            inst.appendToSystemClassLoaderSearch(new JarFile(jar.toFile()));
        } catch (final IOException e) {
            System.err.println("Unable to load jar." + jar.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
