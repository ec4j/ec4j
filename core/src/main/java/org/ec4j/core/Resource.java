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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.ec4j.core.Resource.Resources.StringResourceTree.Builder;
import org.ec4j.core.ResourcePath.ResourcePaths.ClassPathResourcePath;
import org.ec4j.core.ResourcePath.ResourcePaths.PathResourcePath;
import org.ec4j.core.ResourcePath.ResourcePaths.StringResourcePath;
import org.ec4j.core.model.Ec4jPath;
import org.ec4j.core.model.Ec4jPath.Ec4jPaths;

/**
 * A file in filesystem like {@link Resource} hierarchies. The implementations must override {@link Object#hashCode()}
 * and {@link Object#equals(Object)}
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public interface Resource {

    /**
     * An enum of Byte Order Marks (BOMs) to be able to support encodings with BOM unsupported by
     * {@link Charset#forName(String)}.
     *
     * @since 0.0.4
     */
    enum Bom {

        /**
         * {@code utf-8-bom}
         *
         * @since 0.0.4
         */
        UTF_8_BOM("utf-8-bom", new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF }, StandardCharsets.UTF_8);

        /**
         * A {@link Charset} delegating to obe
         */
        static class BomCharset extends Charset {

            private static final String[] EMPTY_STRING_ARRAY = new String[0];
            private final Bom bom;
            private final Charset delegate;

            BomCharset(Bom bom, Charset delegate) {
                super(bom.getName(), EMPTY_STRING_ARRAY);
                this.delegate = delegate;
                this.bom = bom;
            }

            /** {@inheritDoc} */
            @Override
            public boolean contains(Charset cs) {
                return delegate.contains(cs);
            }

            /**
             * @return the associated {@link Bom}
             */
            public Bom getBom() {
                return bom;
            }

            /** {@inheritDoc} */
            @Override
            public CharsetDecoder newDecoder() {
                return delegate.newDecoder();
            }

            /** {@inheritDoc} */
            @Override
            public CharsetEncoder newEncoder() {
                return delegate.newEncoder();
            }

        }

        /**
         * Returns a {@link Bom} having the given {@code name}.
         *
         * @param name name of the encoding
         * @return a {@link Bom} or {@code null} if the name is unknown
         * @since 0.0.4
         */
        public static Bom ofName(String name) {
            final String lcName = name.toLowerCase(Locale.ROOT);
            for (Bom bom : values()) {
                if (bom.name.equals(lcName)) {
                    return bom;
                }
            }
            return null;
        }

        /**
         * If the given {@code charset} is a {@link BomCharset}, skips the BOM sequence in the given {@code bytes} array
         * and returns a new String built out the remaining part of {@code bytes}; otherwise returns a new String built
         * out the {@code bytes}.
         *
         * @param bytes the byte array to process
         * @param charset the encoding of the {@code bytes}
         * @return a new {@link String}
         * @since 0.0.4
         */
        public static String skipBom(byte[] bytes, Charset charset) {
            if (charset instanceof BomCharset) {
                final Bom bom = ((BomCharset) charset).getBom();
                final int bomLength = bom.bomBytes.length;
                if (bytes.length == 0) {
                    /* Handle zero length files as valid */
                    return "";
                }
                else if (bomLength > bytes.length) {
                    throw new IllegalStateException("Input too short; expected to start with Byte Order Mark (BOM)");
                }
                for (int i = 0; i < bomLength; i++) {
                    byte c = bytes[i];
                    if (c != bom.bomBytes[i]) {
                        throw new IllegalStateException(String.format(
                                "Input expected to start with Byte Order Mark (BOM) [%s], found [0x%02X] at offset [%d]",
                                bom.bomBytesHumanReadable(), c, i));
                    }
                }
                return new String(bytes, bomLength, bytes.length - bomLength, charset);
            } else {
                return new String(bytes, charset);
            }
        }

        /**
         * If the given {@code charset} is a {@link BomCharset}, skips the BOM sequence in the given {@code inputStream}
         * and returns the given {@code inputStream}; otherwise just returns the given {@code inputStream}.
         *
         * @param inputStream {@link InputStream} to process
         * @param charset the encoding of the {@code in}
         * @return {@code in}
         * @throws IOException
         * @since 0.0.4
         */
        public static InputStream skipBom(InputStream inputStream, Charset charset) throws IOException {
            if (charset instanceof BomCharset) {
                Bom bom = ((BomCharset) charset).getBom();
                final byte[] bytes = bom.bomBytes;
                for (int i = 0; i < bytes.length; i++) {
                    int c = inputStream.read();
                    if (c < 0) {
                        if (i == 0) {
                            /* Handle zero length files as valid */
                            break;
                        } else {
                            inputStream.close();
                            throw new IllegalStateException(
                                    "Premature end of stream; expected to start with Byte Order Mark (BOM)");
                        }
                    } else if ((byte) c != bytes[i]) {
                        inputStream.close();
                        throw new IllegalStateException(String.format(
                                "Stream expected to start with Byte Order Mark (BOM) [%s], found [0x%02X] at offset [%d]",
                                bom.bomBytesHumanReadable(), c, i));
                    }
                }
            }
            return inputStream;
        }

        /**
         * If the given {@code charset} is a {@link BomCharset}, writes the BOM sequence into the given
         * {@code outpuStream} and returns the given {@code outpuStream}; otherwise just returns the given
         * {@code outpuStream}.
         *
         * @param outpuStream the {@link OutputStream} to write to
         * @param charset the {@link Charset} of the given {@code outputStream}
         * @return {@code outpuStream}
         * @throws IOException
         * @since 0.0.4
         */
        public static OutputStream writeBom(OutputStream outpuStream, Charset charset) throws IOException {
            if (charset instanceof BomCharset) {
                Bom bom = ((BomCharset) charset).getBom();
                outpuStream.write(bom.bomBytes);
            }
            return outpuStream;
        }

        private final byte[] bomBytes;

        private final Charset charset;

        private final String name;

        Bom(String name, byte[] bytes, Charset delegate) {
            this.name = name;
            this.bomBytes = bytes;
            this.charset = new BomCharset(this, delegate);
        }

        /**
         * @return a comma and space separated HEX formatted BOM bytes
         * @since 0.0.4
         */
        public String bomBytesHumanReadable() {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bomBytes.length; i++) {
                if (i == 0) {
                    sb.append(String.format("0x%02X", bomBytes[i]));
                } else {
                    sb.append(String.format(", 0x%02X", bomBytes[i]));
                }
            }
            return sb.toString();
        }

        /**
         * @return a copy of {@link #bomBytes}
         * @since 0.0.4
         */
        public byte[] getBomBytes() {
            final byte[] result = new byte[bomBytes.length];
            System.arraycopy(bomBytes, 0, result, 0, bomBytes.length);
            return result;
        }

        /**
         * @return the {@link BomCharset} associated with this {@link Bom}
         * @since 0.0.4
         */
        public Charset getCharset() {
            return charset;
        }

        /**
         * @return the name of the encoding
         * @since 0.0.4
         */
        public String getName() {
            return name;
        }

    }

    /**
     * A host of {@link #forName(String)}.
     *
     * @since 0.0.4
     */
    class Charsets {

        private Charsets() {
        }

        /**
         * A BOM aware replacement of {@link Charset#forName(String)}
         *
         * @param name the name of the encoding
         * @return a {@link Charset}
         */
        public static Charset forName(String name) {
            Bom bom = Bom.ofName(name);
            if (bom != null) {
                return bom.getCharset();
            } else {
                return Charset.forName(name);
            }
        }

    }

    /**
     * A readed allowing to access a character on any offset in the underlying resource.
     */
    interface RandomReader extends Closeable {
        /**
         * @return the number of characters
         */
        long getLength();

        /**
         * @param offset the index from which the character should be read
         * @return the character at the given {@code offset}
         * @throws IndexOutOfBoundsException if the offset it less than {@code 0} or greater than {@link #getLength()}
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

            private static String removeInitialSlash(Ec4jPath path) {
                return Ec4jPaths.root().relativize(path).toString();
            }

            final Charset encoding;
            final ClassLoader loader;
            final Ec4jPath path;

            ClassPathResource(ClassLoader loader, Ec4jPath path, Charset encoding) {
                super();
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
                return loader.getResource(removeInitialSlash(path)) != null;
            }

            /** {@inheritDoc} */
            @Override
            public ResourcePath getParent() {
                Ec4jPath parentPath = path.getParentPath();
                return parentPath == null ? null : new ClassPathResourcePath(loader, parentPath, encoding);
            }

            /** {@inheritDoc} */
            @Override
            public Ec4jPath getPath() {
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
                return StringRandomReader.ofUrl(loader.getResource(removeInitialSlash(path)), encoding);
            }

            /**
             * {@inheritDoc}
             *
             * @throws IOException
             */
            @Override
            public Reader openReader() throws IOException {
                return new InputStreamReader(
                        Bom.skipBom(loader.getResourceAsStream(removeInitialSlash(path)), encoding), encoding);
            }

            /** {@inheritDoc} */
            @Override
            public String toString() {
                return "classpath:" + getPath();
            }

        }

        /**
         * A {@link Resource} implementation based on {@code java.nio.file.Path}. To create a new instance use
         * {@link Resources#ofPath(Path, Charset)}.
         */
        static class PathResource implements Resource {

            final Charset encoding;
            final Path path;

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
            public Ec4jPath getPath() {
                return Ec4jPaths.of(path);
            }

            /** {@inheritDoc} */
            @Override
            public int hashCode() {
                return path.hashCode();
            }

            /** {@inheritDoc} */
            @Override
            public RandomReader openRandomReader() throws IOException {
                return new StringRandomReader(Bom.skipBom(Files.readAllBytes(path), encoding));
            }

            /** {@inheritDoc} */
            @Override
            public Reader openReader() throws IOException {
                CharsetDecoder decoder = encoding.newDecoder();
                Reader reader = new InputStreamReader(Bom.skipBom(Files.newInputStream(path), encoding), decoder);
                return new BufferedReader(reader);
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
             * @param reader the {@link Reader} to read
             * @return a new {@link StringRandomReader}
             * @throws IOException in case there is any read problem
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
             * @param content the contents the resulting {@link RandomReader} should be based on
             * @return a new {@link StringRandomReader}
             */
            public static RandomReader ofString(String content) {
                return new StringRandomReader(content);
            }

            public static RandomReader ofUrl(URL url, Charset encoding) throws IOException {
                try (Reader r = new InputStreamReader(Bom.skipBom(url.openStream(), encoding), encoding)) {
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
            final String content;
            /** The list of path segments */
            final Ec4jPath path;
            /** The tree this resource is bound to. Necessary to resolve relative resources */
            final Map<Ec4jPath, Resource> resources;

            StringResource(Map<Ec4jPath, Resource> resources, Ec4jPath path, String content) {
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
                Ec4jPath parentPath = path.getParentPath();
                return parentPath == null ? null : new StringResourcePath(parentPath, resources);
            }

            /** {@inheritDoc} */
            @Override
            public Ec4jPath getPath() {
                return path;
            }

            /** {@inheritDoc} */
            @Override
            public int hashCode() {
                return path.hashCode();
            }

            /** {@inheritDoc} */
            @Override
            public RandomReader openRandomReader() {
                return new StringRandomReader(content);
            }

            /** {@inheritDoc} */
            @Override
            public Reader openReader() {
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
                private final Map<Ec4jPath, Resource> resources = new LinkedHashMap<>();

                public StringResourceTree build() {
                    return new StringResourceTree(Collections.unmodifiableMap(resources));
                }

                public Builder resource(String path, String content) {
                    Ec4jPath p = Ec4jPaths.of(path);
                    resources.put(p, new StringResource(resources, p, content));
                    return this;
                }

                public Builder resource(String path, URL url, Charset encoding) throws IOException {
                    Ec4jPath segments = Ec4jPaths.of(path);

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
                    Ec4jPath segments = Ec4jPaths.of(path);
                    resources.put(segments, new StringResource(resources, segments, ""));
                    return this;
                }
            }

            public static Builder builder() {
                return new Builder();
            }

            private final Map<Ec4jPath, Resource> resources;

            StringResourceTree(Map<Ec4jPath, Resource> resources) {
                super();
                this.resources = resources;
            }

            public Resource getResource(Ec4jPath path) {
                return resources.get(path);
            }

            public Resource getResource(String path) {
                return resources.get(Ec4jPaths.of(path));
            }

        }

        /**
         * Returns a new {@link ClassPathResource} associated with the given {@code path} and {@link ClassLoader}.
         *
         * @param loader the {@link ClassLoader} to load the resource from
         * @param path the path to load
         * @param encoding the encoding of the resource under the given {@code path}
         * @return a new {@link ClassPathResource}
         */
        public static Resource ofClassPath(ClassLoader loader, String path, Charset encoding) {
            return new ClassPathResource(loader, Ec4jPaths.of(path), encoding);
        }

        /**
         * @param path the {@link Path} to create a new {@link Resource} from
         * @param encoding the {@link Charset} to use when reading from the given @{code path}
         * @return a new {@link PathResource}
         */
        public static Resource ofPath(Path path, Charset encoding) {
            return new PathResource(path, encoding);
        }

        /**
         * @param path the file path of this {@link StringResource}, must have at least one segment, e.g.
         *        {@code "my-file.txt"}, or {@code "path/to/my-file.txt"} or {@code "/path/to/my-file.txt"}
         * @param content the content of the {@link Resource}
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
     * @return the {@link Ec4jPath} of this {@link Resource}
     */
    Ec4jPath getPath();

    /**
     * Opens a {@link RandomReader} to read the content of this {@link Resource}.
     *
     * @return an open {@link RandomReader}
     * @throws IOException on I/O problems
     */
    RandomReader openRandomReader() throws IOException;

    /**
     * Opens a {@link Reader} to read the content of this {@link Resource}.
     *
     * @return an open {@link Reader}
     * @throws IOException on I/O problems
     */
    Reader openReader() throws IOException;

}