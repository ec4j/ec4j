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
package org.ec4j.core;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.ec4j.core.Resource.Resources.StringResourceTree.Builder;
import org.ec4j.core.ResourcePath.ResourcePaths.ClassPathResourcePath;
import org.ec4j.core.ResourcePath.ResourcePaths.PathResourcePath;
import org.ec4j.core.ResourcePath.ResourcePaths.StringResourcePath;

/**
 * A file in filesystem like {@link Resource} hierarchies. The implementations must implement {@link #hashCode()} and
 * {@link #equals(Object)}
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public interface Resource {

    /**
     * A readed allowing to access a character on any offset in the underlying resource.
     */
    public interface RandomReader extends Closeable {
        /**
         * @return the number of characters
         */
        long getLength();

        /**
         * @param offset
         *            the index from which the character should be read
         * @return the character at the given {@code offset}
         * @throws IndexOutOfBoundsException
         *             if the offset it less than {@code 0} or greater than {@link #getLength()}
         */
        char read(long offset) throws IndexOutOfBoundsException;
    }

    /**
     * A class that aggregates the {@link Resource} interface and a few common implementations of {@link Resource}.
     *
     * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
     */
    class Resources {

        /**
         * A {@link Resource} implementation that loads resources from the current class path via the given
         * {@link ClassLoader}.
         */
        static class ClassPathResource implements Resource {

            private final Charset encoding;
            private final ClassLoader loader;
            private final String path;

            ClassPathResource(ClassLoader loader, String path, Charset encoding) {
                super();
                if (path == null || path.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Path cannot be null or empty to create a new " + getClass().getName());
                } else if (path.charAt(0) != '/') {
                    throw new IllegalArgumentException("Unexpected path \"" + path + "\": must start with slash");
                }
                this.loader = loader;
                this.path = path;
                this.encoding = encoding;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                ClassPathResource other = (ClassPathResource) obj;
                if (loader == null) {
                    if (other.loader != null)
                        return false;
                } else if (!loader.equals(other.loader))
                    return false;
                if (path == null) {
                    if (other.path != null)
                        return false;
                } else if (!path.equals(other.path))
                    return false;
                return true;
            }

            /** {@inheritDoc} */
            @Override
            public boolean exists() {
                return loader.getResource(path.substring(1 /* strip the initial slash */)) != null;
            }

            /** {@inheritDoc} */
            @Override
            public ResourcePath getParent() {
                String parentPath = ClassPathResourcePath.parentPath(path);
                return new ClassPathResourcePath(loader, parentPath, encoding);
            }

            /** {@inheritDoc} */
            @Override
            public String getPath() {
                return path;
            }

            /** {@inheritDoc} */
            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((loader == null) ? 0 : loader.hashCode());
                result = prime * result + ((path == null) ? 0 : path.hashCode());
                return result;
            }

            /** {@inheritDoc} */
            @Override
            public RandomReader openRandomReader() throws IOException {
                return StringRandomReader.ofUrl(loader.getResource(path.substring(1 /* strip the initial slash */)),
                        encoding);
            }

            /** {@inheritDoc} */
            @Override
            public Reader openReader() throws IOException {
                return new InputStreamReader(
                        loader.getResourceAsStream(path.substring(1 /* strip the initial slash */)), encoding);
            }

            /** {@inheritDoc} */
            @Override
            public String toString() {
                return "classpath:" + getPath();
            }

        }

        /**
         * A {@link Resource} implementation based on {@code java.nio.file.Path}. To create a new instance use
         * {@link Resources#ofPath(Path)}.
         */
        static class PathResource implements Resource {

            private final Charset encoding;
            private final Path path;

            PathResource(Path path, Charset encoding) {
                super();
                this.path = path;
                this.encoding = encoding;
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
                PathResource other = (PathResource) obj;
                return this.path.equals(other.path);
            }

            /** {@inheritDoc} */
            @Override
            public boolean exists() {
                return Files.exists(path);
            }

            /** {@inheritDoc} */
            @Override
            public ResourcePath getParent() {
                Path parent = path.getParent();
                return parent == null ? null : new PathResourcePath(parent, encoding);
            }

            /** {@inheritDoc} */
            @Override
            public String getPath() {
                StringBuilder result = new StringBuilder();
                final int len = path.getNameCount();
                for (int i = 0; i < len; i++) {
                    if (i != 0 || path.isAbsolute()) {
                        result.append('/');
                    }
                    result.append(path.getName(i));
                }
                return result.toString();
            }

            /** {@inheritDoc} */
            @Override
            public int hashCode() {
                return path.hashCode();
            }

            /** {@inheritDoc} */
            @Override
            public RandomReader openRandomReader() throws IOException {
                return new StringRandomReader(new String(Files.readAllBytes(path), encoding));
            }

            /** {@inheritDoc} */
            @Override
            public Reader openReader() throws IOException {
                return Files.newBufferedReader(path, encoding);
            }

            /** {@inheritDoc} */
            @Override
            public String toString() {
                return "path:" + getPath();
            }

        }

        /**
         * A {@link RandomReader} that reads from a {@link String}
         */
        public static class StringRandomReader implements RandomReader {

            /**
             * Reads the given {@link Reader} into a {@link String} and creates a new {@link StringRandomReader} out of
             * it.
             *
             * @param reader
             *            the {@link Reader} to read
             * @return a new {@link StringRandomReader}
             * @throws IOException
             *             in case there is any read problem
             */
            public static RandomReader ofReader(Reader reader) throws IOException {
                StringBuilder result = new StringBuilder();
                int n;
                char[] buf = new char[4096];
                while ((n = reader.read(buf)) >= 0) {
                    result.append(buf, 0, n);
                }
                return new StringRandomReader(result.toString());
            }

            /**
             * @param content
             *            the contents the resulting {@link RandomReader} should be based on
             * @return a new {@link StringRandomReader}
             */
            public static RandomReader ofString(String content) {
                return new StringRandomReader(content);
            }

            public static RandomReader ofUrl(URL url, Charset encoding) throws IOException {
                try (Reader r = new InputStreamReader(url.openStream(), encoding)) {
                    return ofReader(r);
                }
            }

            /** The content to read as a {@link String} */
            private final String content;

            StringRandomReader(String content) {
                super();
                this.content = content;
            }

            @Override
            public void close() {
                /* nothing to do */
            }

            /** {@inheritDoc} */
            @Override
            public long getLength() {
                return content == null ? 0 : content.length();
            }

            /** {@inheritDoc} */
            @Override
            public char read(long offset) {
                if (offset < 0 || offset >= getLength()) {
                    throw new IndexOutOfBoundsException("Cannot access index " + offset + " in string "
                            + (content == null ? "null" : ("\"" + content + "\"")));
                }
                return content.charAt((int) offset);
            }
        }

        /**
         * A {@link Resource} based on a {@link String} content.
         */
        static class StringResource implements Resource {

            /** The {@link String} content */
            private final String content;
            /** The list of path segments */
            private final List<String> path;
            /** The tree this resource is bound to. Necessary to resolve relative resources */
            private final Map<List<String>, Resource> resources;

            StringResource(Map<List<String>, Resource> resources, List<String> path, String content) {
                super();
                this.path = path;
                this.resources = resources;
                this.content = content;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                StringResource other = (StringResource) obj;
                return this.path.equals(other.path);
            }

            /** {@inheritDoc} */
            @Override
            public boolean exists() {
                return resources.get(path) != null;
            }

            /** {@inheritDoc} */
            @Override
            public ResourcePath getParent() {
                return new StringResourcePath(path.subList(0, path.size() - 1), resources);
            }

            /** {@inheritDoc} */
            @Override
            public String getPath() {
                return StringResourceTree.toString(path);
            }

            /** {@inheritDoc} */
            @Override
            public int hashCode() {
                return path.hashCode();
            }

            /** {@inheritDoc} */
            @Override
            public RandomReader openRandomReader() throws IOException {
                return new StringRandomReader(content);
            }

            /** {@inheritDoc} */
            @Override
            public Reader openReader() throws IOException {
                return new StringReader(content);
            }

            /** {@inheritDoc} */
            @Override
            public String toString() {
                return "string:" + getPath();
            }

        }

        /**
         * A simple {@link Map} backed tree of {@link StringResource}s. Use
         * {@link Resources#stringResourceTreeBuilder()} to instantiate.
         */
        public static class StringResourceTree {

            public static class Builder {
                private final Map<List<String>, Resource> resources = new LinkedHashMap<>();

                public StringResourceTree build() {
                    return new StringResourceTree(Collections.unmodifiableMap(resources));
                }

                public Builder resource(String path, String content) {
                    List<String> segments = toSegments(path);
                    resources.put(segments, new StringResource(resources, segments, content));
                    return this;
                }

                public Builder resource(String path, URL url, Charset encoding) throws IOException {
                    List<String> segments = toSegments(path);

                    StringBuilder sb = new StringBuilder();
                    try (Reader r = new InputStreamReader(url.openStream(), encoding)) {
                        int len;
                        char[] cbuf = new char[4096];
                        while ((len = r.read(cbuf)) >= 0) {
                            sb.append(cbuf, 0, len);
                        }
                    }
                    resources.put(segments, new StringResource(resources, segments, sb.toString()));
                    return this;
                }

                public Builder touch(String path) {
                    List<String> segments = toSegments(path);
                    resources.put(segments, new StringResource(resources, segments, ""));
                    return this;
                }
            }

            public static Builder builder() {
                return new Builder();
            }

            public static List<String> toSegments(String rawPath) {
                List<String> result = new ArrayList<>();
                StringTokenizer st = new StringTokenizer(rawPath, "/");
                while (st.hasMoreTokens()) {
                    result.add(st.nextToken());
                }
                return Collections.unmodifiableList(result);
            }

            public static String toString(List<String> segments) {
                StringBuilder sb = new StringBuilder();
                for (String segment : segments) {
                    sb.append('/').append(segment);
                }
                return sb.toString();
            }

            private final Map<List<String>, Resource> resources;

            StringResourceTree(Map<List<String>, Resource> resources) {
                super();
                this.resources = resources;
            }

            public Resource getResource(List<String> path) {
                return resources.get(path);
            }

            public Resource getResource(String path) {
                return resources.get(toSegments(path));
            }

        }

        /**
         * Returns a new {@link ClassPathResource} associated with the given {@code path} and {@link ClassLoader}.
         *
         * @param loader
         *            the {@link ClassLoader} to load the resource from
         * @param path
         *            the path to load
         * @param encoding
         *            the encoding of the resource under the given {@code path}
         * @return a new {@link ClassPathResource}
         */
        public static Resource ofClassPath(ClassLoader loader, String path, Charset encoding) {
            return new ClassPathResource(loader, path, encoding);
        }

        /**
         * @param path
         *            the {@link Path} to create a new {@link Resource} from
         * @return a new {@link PathResource}
         */
        public static Resource ofPath(Path path, Charset encoding) {
            return new PathResource(path, encoding);
        }

        /**
         * @param path
         *            the file path of this {@link StringResource}, must have at least one segment, e.g.
         *            {@code "my-file.txt"}, or {@code "path/to/my-file.txt"} or {@code "/path/to/my-file.txt"}
         * @param content
         *            the content of the {@link Resource}
         * @return a new {@link StringResource} with the given {@code path}, bound to a {@link StringResourceTree} that
         *         contains just the given {@code path}
         */
        public static Resource ofString(String path, String content) {
            StringResourceTree tree = StringResourceTree.builder() //
                    .resource(path, content) //
                    .build();
            return tree.getResource(path);
        }

        /**
         * @return a new {@link Builder}
         */
        public static StringResourceTree.Builder stringResourceTreeBuilder() {
            return new StringResourceTree.Builder();
        }

        private Resources() {
        }
    }

    /**
     * @return {@code true} if this {@link Resource} exists; otherwise {@code false}
     */
    boolean exists();

    /**
     * @return the {@link ResourcePath} of the parent of this {@link Resource} or {@code null} if this {@link Resource}
     *         has no parent
     */
    ResourcePath getParent();

    /**
     * @return the path of this {@link Resource} as a string; the segments are separated by slash {@code /}
     */
    String getPath();

    /**
     * Opens a {@link RandomReader} to read the content of this {@link Resource}.
     *
     * @return an open {@link RandomReader}
     * @throws IOException
     */
    RandomReader openRandomReader() throws IOException;

    /**
     * Opens a {@link Reader} to read the content of this {@link Resource}.
     *
     * @return an open {@link Reader}
     * @throws IOException
     */
    Reader openReader() throws IOException;
}