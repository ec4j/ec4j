/**
 * Copyright (c) 2017 Angelo Zerr and other contributors as
 * indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.opensagres.ec4j.core.model;

/**
 * A version that consists of major, minor, micro and qualifier. The first three parts are delimited with period
 * ({@code .}) and the last optional qualifier is delimited with a dash ({@code -}). Possible version strings can be
 * represented as {@code major.minor.micro[-qualifier]}.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class Version implements Comparable<Version> {

    public static final Version _0_10_0 = Version.of("0.10.0");
    public static final Version CURRENT = Version.of("0.12.0-final");

    private static final char DASH = '-';
    private static final char PERIOD = '.';

    public static Version of(String version) {
        int start = 0;
        int end = version.indexOf(PERIOD, start);
        if (end >= 1) {
            final byte major = Byte.valueOf(version.substring(start, end));
            start = end + 1;
            end = version.indexOf(PERIOD, start);
            if (end >= 0 && end - start >= 1) {
                final byte minor = Byte.valueOf(version.substring(start, end));
                start = end + 1;
                end = version.indexOf(DASH, start);
                if (end >= 0) {
                    /* there is a qualifier */
                    if (end - start >= 1) {
                        final byte micro = Byte.valueOf(version.substring(start, end));
                        final String qualifier = version.substring(end + 1);
                        return new Version(major, minor, micro, qualifier);
                    }
                } else {
                    /* no qualifier */
                    final byte micro = Byte.valueOf(version.substring(start));
                    return new Version(major, minor, micro, null);
                }
            }
        }
        throw new IllegalArgumentException("Cannot parse \"" + version
                + "\" into a version. A string matching major.minor.micro[-qualifier] expected.");
    }

    private final int majorMinorMicro;

    private final String qualifier;

    public Version(byte major, byte minor, byte micro, String qualifier) {
        super();
        this.majorMinorMicro = (major << 24) | (minor << 16) | (micro << 8);
        this.qualifier = qualifier;
    }

    Version(int majorMinorMicro, String qualifier) {
        super();
        this.majorMinorMicro = majorMinorMicro;
        this.qualifier = qualifier;
    }

    @Override
    public int compareTo(Version other) {
        final int mmmCompared = Integer.compare(this.majorMinorMicro, other.majorMinorMicro);
        switch (mmmCompared) {
        case 0:
            return this.qualifier == other.qualifier ? 0
                    : (this.qualifier == null ? -1
                            : (other.qualifier == null ? 1 : this.qualifier.compareTo(other.qualifier)));
        default:
            return mmmCompared;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Version other = (Version) obj;
        if (majorMinorMicro != other.majorMinorMicro)
            return false;
        if (qualifier == null) {
            if (other.qualifier != null)
                return false;
        } else if (!qualifier.equals(other.qualifier))
            return false;
        return true;
    }

    byte getMajor() {
        return (byte) (this.majorMinorMicro >> 24);
    }

    byte getMicro() {
        return (byte) (this.majorMinorMicro >> 8);
    }

    byte getMinor() {
        return (byte) (this.majorMinorMicro >> 16);
    }

    String getQualifier() {
        return qualifier;
    }

    @Override
    public int hashCode() {
        return 31 * (31 + majorMinorMicro) + ((qualifier == null) ? 0 : qualifier.hashCode());
    }

    @Override
    public String toString() {
        return "" + getMajor() + "." + getMinor() + "." + getMicro() + (qualifier == null ? "" : ("-" + qualifier));
    }
}
