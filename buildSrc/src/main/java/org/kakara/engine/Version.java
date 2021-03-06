package org.kakara.engine;

import java.io.IOException;
import java.util.List;

import org.gradle.api.GradleException;

public class Version {
    public static final String GAME_VERSION = "1.0-SNAPSHOT";

    /**
     * Generates the engine version based on the current branch.
     * Ignores for non snapshots.
     * Ignores for master branch
     *
     * @return the engine version.
     */
    public static String getGameVersion(String buildNumber, String branch) throws GradleException {
        String value = GAME_VERSION;
        if (GAME_VERSION.endsWith("-SNAPSHOT")) {
            try {
                String finalBranch = branch;
                if (branch.isEmpty()) {
                    finalBranch = execCmd("git rev-parse --abbrev-ref HEAD").replace("\n", "");
                }
                if (finalBranch.equals("HEAD")) {
                    throw new GradleException("Can not work in HEAD");
                }
                if (!finalBranch.equalsIgnoreCase("master")) {
                    value = GAME_VERSION.replace("-SNAPSHOT", String.format("-%s-SNAPSHOT", finalBranch.replace("/", "-")));

                }
            } catch (IOException e) {
                throw new GradleException("Unable to execute git command", e);
            }

        }
        if (!buildNumber.isEmpty() && !buildNumber.isBlank()) {
            value = value.replace("-SNAPSHOT", String.format("-%s-SNAPSHOT", buildNumber));
        }
        System.out.println("Building with version: " + value);
        return value;
    }

    public static String execCmd(String cmd) throws IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
