package me.madfix.cloudnet.webinterface.model;

import java.util.Objects;

public final class InterfaceConfiguration {

    private final int restPort;
    private final int webSocketPort;
    private final String host;
    private final boolean mobSystem;
    private final boolean signSystem;
    private final DatabaseConfiguration databaseConfiguration;

    public InterfaceConfiguration(int restPort, int webSocketPort, String host,
                                  boolean mobSystem, boolean signSystem, DatabaseConfiguration databaseConfiguration) {
        this.restPort = restPort;
        this.webSocketPort = webSocketPort;
        this.host = host;
        this.mobSystem = mobSystem;
        this.signSystem = signSystem;
        this.databaseConfiguration = databaseConfiguration;
    }

    public int getRestPort() {
        return restPort;
    }

    public int getWebSocketPort() {
        return webSocketPort;
    }

    public String getHost() {
        return host;
    }

    public DatabaseConfiguration getDatabaseConfiguration() {
        return databaseConfiguration;
    }

    public boolean isMobSystem() {
        return mobSystem;
    }

    public boolean isSignSystem() {
        return signSystem;
    }

    @Override
    public String toString() {
        return "InterfaceConfiguration{" +
                "restPort=" + restPort +
                ", webSocketPort=" + webSocketPort +
                ", host='" + host + '\'' +
                ", mobSystem=" + mobSystem +
                ", signSystem=" + signSystem +
                ", databaseConfiguration=" + databaseConfiguration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterfaceConfiguration that = (InterfaceConfiguration) o;
        return restPort == that.restPort &&
                webSocketPort == that.webSocketPort &&
                mobSystem == that.mobSystem &&
                signSystem == that.signSystem &&
                Objects.equals(host, that.host) &&
                Objects.equals(databaseConfiguration, that.databaseConfiguration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restPort, webSocketPort, host, mobSystem, signSystem, databaseConfiguration);
    }
}
