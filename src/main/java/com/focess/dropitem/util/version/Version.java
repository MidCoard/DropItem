package com.focess.dropitem.util.version;

import java.util.Objects;

public class Version {
    private final int mainVersion;
    private final int subVersion;

    public Version(final String version) {
        final String[] temp = version.split("\\.");
        this.mainVersion = Integer.parseInt(temp[0]);
        this.subVersion = Integer.parseInt(temp[1]);
    }

    public int getMainVersion() {
        return this.mainVersion;
    }

    public int getSubVersion() {
        return this.subVersion;
    }

    public boolean newerThan(final Version version) {
        return this.mainVersion > version.getMainVersion() || (this.mainVersion == version.getMainVersion() && this.subVersion > version.getSubVersion());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Version version = (Version) o;
        return this.mainVersion == version.mainVersion &&
                this.subVersion == version.subVersion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.mainVersion, this.subVersion);
    }

    public String getVersion() {
        return this.mainVersion + "." + this.subVersion;
    }

    @Override
    public String toString() {
        return this.getVersion();
    }

    public boolean isNew(final Version version) {
        return this.newerThan(version) || this.equals(version);
    }
}

