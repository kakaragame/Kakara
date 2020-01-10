package org.kakara.client;


import org.apache.commons.cli.*;
import org.kakara.core.KakaraCore;

import static org.kakara.client.CommandArguments.*;

public class Main {

    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(AUTH_KEY);
        options.addOption(AUTH_SERVER);
        CommandLineParser parser = new DefaultParser();
        CommandLine parse = parser.parse(options, args);
        //TODO load the AUTH IMPL from the jar via a ServiceLoader/ClassLoader

    }
}
