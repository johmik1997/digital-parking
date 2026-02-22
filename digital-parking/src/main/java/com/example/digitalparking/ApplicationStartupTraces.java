package com.example.digitalparking;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationStartupTraces {

    private static final String SEPARATOR = "-".repeat(58);
    private static final String BREAK = "\n";
    private static final Logger log = LoggerFactory.getLogger(ApplicationStartupTraces.class);

    private ApplicationStartupTraces() {}

    public static String of(Environment environment) {
        if (environment == null) {
            throw new IllegalArgumentException("Environment must not be null");
        }

        return new ApplicationStartupTracesBuilder()
                .append(BREAK)
                .appendSeparator()
                .append(applicationRunningTrace(environment))
                .append(localUrl(environment))
                .append(externalUrl(environment))
                .append(profilesTrace(environment))
                .appendSeparator()
                .append(configServer(environment))
                .build();
    }

    private static String applicationRunningTrace(Environment environment) {
        String applicationName = environment.getProperty("spring.application.name");
        return StringUtils.isBlank(applicationName) ? "Application is running!" :
                "Application '" + applicationName + "' is running!";
    }

    private static String localUrl(Environment environment) {
        return url("Local", "localhost", environment);
    }

    private static String externalUrl(Environment environment) {
        return url("External", getExternalIp(), environment);
    }

    private static String url(String type, String host, Environment environment) {
        if (notWebEnvironment(environment)) return null;
        return type + ":\t" + protocol(environment) + "://" + host + ":" + port(environment) + contextPath(environment);
    }

    private static boolean notWebEnvironment(Environment environment) {
        return StringUtils.isBlank(environment.getProperty("server.port"));
    }

    private static String protocol(Environment environment) {
        return StringUtils.isBlank(environment.getProperty("server.ssl.key-store")) ? "http" : "https";
    }

    private static String port(Environment environment) {
        return environment.getProperty("server.port");
    }

    private static String profilesTrace(Environment environment) {
        String[] profiles = environment.getActiveProfiles();
        if (ArrayUtils.isEmpty(profiles)) return null;
        return "Profile(s):\t" + Stream.of(profiles).collect(Collectors.joining(", "));
    }

    private static String contextPath(Environment environment) {
        String contextPath = environment.getProperty("server.servlet.context-path");
        return StringUtils.isBlank(contextPath) ? "/" : contextPath;
    }

    private static String configServer(Environment environment) {
        String configServer = environment.getProperty("configserver.status");
        if (StringUtils.isBlank(configServer)) return null;
        return "Config Server: " + configServer + BREAK + SEPARATOR + BREAK;
    }

    private static String getExternalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            log.warn("Failed to get network interfaces: {}", e.getMessage());
        }
        // fallback
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("Cannot determine host IP, using localhost");
        }
        return "localhost";
    }

    private static class ApplicationStartupTracesBuilder {
        private static final String SPACER = "  ";
        private final StringBuilder trace = new StringBuilder();

        public ApplicationStartupTracesBuilder appendSeparator() {
            trace.append(SEPARATOR).append(BREAK);
            return this;
        }

        public ApplicationStartupTracesBuilder append(String line) {
            if (line != null) trace.append(SPACER).append(line).append(BREAK);
            return this;
        }

        public String build() { return trace.toString(); }
    }
}

