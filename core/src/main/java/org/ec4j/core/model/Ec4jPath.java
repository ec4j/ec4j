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
package org.ec4j.core.model;

import java.nio.file.Path;

/**
 * A path in a tree of resources. An {@link Ec4jPath} consists of segments. When an {@link Ec4jPath} is rendered as a
 * {@link String}, the segments are delimited by a slash {@code /}.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public interface Ec4jPath {

    /**
     * Holds a few basic {@link Ec4jPath} implementations.
     */
    class Ec4jPaths {

        /**
         * An {@link Ec4jPath} based on {@link Path}.
         */
        static class NioPath implements Ec4jPath {
            private static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            private final Path path;
            private String toString;

            NioPath(Path path) {
                super();
                this.path = path;
            }

            /** {@inheritDoc} */
            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                NioPath other = (NioPath) obj;
                return path.equals(other.path);
            }

            /** {@inheritDoc} */
            @Override
            public String getLastSegment() {
                return path.getFileName().toString();
            }

            /** {@inheritDoc} */
            @Override
            public Ec4jPath getParentPath() {
                Path parent = path.getParent();
                return parent == null ? null : new NioPath(parent);
            }

            /** {@inheritDoc} */
            @Override
            public int hashCode() {
                return path.hashCode();
            }

            /** {@inheritDoc} */
            @Override
            public boolean isAbsolute() {
                return path.isAbsolute();
            }

            /** {@inheritDoc} */
            @Override
            public Ec4jPath relativize(Ec4jPath other) {
                if (!(other instanceof StringPath)) {
                    throw new IllegalArgumentException(
                            NioPath.class.getName() + " can relativize only instances of " + NioPath.class.getName());
                }
                return new NioPath(path.relativize(((NioPath) other).path));
            }

            /** {@inheritDoc} */
            @Override
            public Ec4jPath resolve(String name) {
                return new NioPath(path.resolve(name));
            }

            /** {@inheritDoc} */
            @Override
            public String toString() {
                if (isWindows) {
                    if (toString == null) {
                        StringBuilder result = new StringBuilder();
                        final int len = path.getNameCount();
                        for (int i = 0; i < len; i++) {
                            if (i != 0 || path.isAbsolute()) {
                                result.append('/');
                            }
                            result.append(path.getName(i));
                        }
                        toString = result.toString();
                    }
                    return toString;
                } else {
                    return path.toString();
                }
            }

        }

        /**
         * A simple {@link String} based {@link Ec4jPath}.
         */
        static class StringPath implements Ec4jPath {
            private final String path;

            StringPath(String path) {
                if (path == null || path.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Path cannot be null or empty to create a new " + getClass().getName());
                }
                if (path.length() > 1 && path.endsWith("/")) {
                    throw new IllegalArgumentException(
                            "Path cannot end with a slash '/' to create a new " + getClass().getName());
                }
                this.path = path;
            }

            /** {@inheritDoc} */
            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                StringPath other = (StringPath) obj;
                return this.path.equals(other.path);
            }

            /** {@inheritDoc} */
            @Override
            public String getLastSegment() {
                final int lastSlash = path.lastIndexOf('/');
                return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
            }

            /** {@inheritDoc} */
            @Override
            public Ec4jPath getParentPath() {
                if ("/".equals(path)) {
                    return null;
                } else {
                    int lastSlash = path.lastIndexOf('/');
                    if (lastSlash == 0) {
                        return new StringPath("/");
                    } else if (lastSlash > 0) {
                        return new StringPath(path.substring(0, lastSlash));
                    } else {
                        return null;
                    }
                }
            }

            /** {@inheritDoc} */
            @Override
            public int hashCode() {
                return path.hashCode();
            }

            /** {@inheritDoc} */
            @Override
            public boolean isAbsolute() {
                return path != null && !path.isEmpty() && path.charAt(0) == '/';
            }

            /** {@inheritDoc} */
            @Override
            public Ec4jPath relativize(Ec4jPath other) {
                if (!(other instanceof StringPath)) {
                    throw new IllegalArgumentException(StringPath.class.getName() + " can relativize only instances of "
                            + StringPath.class.getName());
                }
                StringPath otherStringPath = (StringPath) other;
                if ("/".equals(path)) {
                    if (otherStringPath.path.startsWith("/") && otherStringPath.path.length() > 1) {
                        return new StringPath(otherStringPath.path.substring(1));
                    } else {
                        throw new IllegalArgumentException("Cannot relativize path " + otherStringPath
                                + " against resource path " + this.toString());
                    }
                } else {
                    /* this path is not root */
                    final int len = path.length();
                    if (!otherStringPath.path.startsWith(path) || otherStringPath.path.length() <= len
                            || otherStringPath.path.charAt(len) != '/') {
                        throw new IllegalArgumentException("Cannot relativize path " + otherStringPath
                                + " against resource path " + this.toString());
                    }
                    return new StringPath(otherStringPath.path.substring(len + 1));
                }
            }

            /** {@inheritDoc} */
            @Override
            public Ec4jPath resolve(String name) {
                if (name.indexOf('/') >= 0) {
                    throw new IllegalArgumentException("Cannot resolve names that contain a slash '/'");
                }
                String newPath = "/".equals(path) ? "/" + name : path + "/" + name;
                return new StringPath(newPath);
            }

            /** {@inheritDoc} */
            @Override
            public String toString() {
                return path;
            }
        }

        private static final Ec4jPath ROOT = new StringPath("/");

        /**
         * @param path
         *        the {@link Path} to create a new {@link Ec4jPath} from
         * @return a new {@link Ec4jPath}
         */
        public static Ec4jPath of(Path path) {
            return new NioPath(path);
        }

        /**
         * @param path
         *        the {@link Path} to create a new {@link Ec4jPath} from
         * @return a new {@link Ec4jPath}
         */
        public static Ec4jPath of(String path) {
            return new StringPath(path);
        }

        /**
         * @return a root path {@code /}
         */
        public static Ec4jPath root() {
            return ROOT;
        }

        Ec4jPaths() {
        }

    }

    /**
     * @return the last segment of this {@link Ec4jPath}
     */
    String getLastSegment();

    /**
     * @return the parent path of this {@link Ec4jPath} or {@code null} of this {@link Ec4jPath} has no parent
     */
    Ec4jPath getParentPath();

    /**
     * @return {@code true} if this {@link Ec4jPath} is absolute (i.e. starting with a slash {@code /}) or {@code false}
     *         otherwise
     */
    boolean isAbsolute();

    /**
     * Constructs a relative path between this path and the given {@link Ec4jPath}. If this {@link Ec4jPath} is
     * {@code /dir1} and {@code other} is {@code /dir1/dir2/file} then the relative path is {@code dir2/file}.
     *
     * @param other
     *        the path to relativize
     * @return a relative path between this path and a given path.
     */
    Ec4jPath relativize(Ec4jPath other);

    /**
     * Creates a new {@link Ec4jPath} by appending the given {@code segment} to this {@link Ec4jPath}.
     *
     * @param segment
     *        the segment to append to this {@link Ec4jPath}
     * @return a new {@link Ec4jPath}
     */
    Ec4jPath resolve(String segment);

    /**
     * @return a {@link String} representation of this {@link Ec4jPath} delimited by slash {@code /}
     */
    @Override
    String toString();

}