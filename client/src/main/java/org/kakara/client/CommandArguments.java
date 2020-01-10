package org.kakara.client;

import org.apache.commons.cli.Option;

public final class CommandArguments {
    static final Option AUTH_KEY = Option.builder().argName("auth-key").longOpt("Auth Key").desc("The Auth Key").required(true).hasArgs().numberOfArgs(1).build();
    static final Option AUTH_SERVER = Option.builder().argName("auth-server").longOpt("Auth Server").desc("The Auth Server").required(true).hasArgs().numberOfArgs(1).build();
    static final Option AUTH_IMPL_JAR = Option.builder().argName("auth_impl").longOpt("Auth API implementation").desc("The Auth Server API implementation").required(true).hasArgs().argName("auth-impl.jar").numberOfArgs(1).build();

}
