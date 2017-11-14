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

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A directory path in filesystem like {@link Resource} hierarchies. The implementations must implement
 * {@link #hashCode()} and {@link #equals(Object)}
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public interface ResourcePath {

    /**
     * A class that aggregates the {@link ResourcePath} interface and a few common implementations of
     * {@link ResourcePath}.
     */
    class ResourcePaths {

        /**
         * A {@link ResourcePath} implementation that loads resources from the current class path via the given
         * {@link ClassLoader}.
         */
        static class ClassPathResourcePath implements ResourcePath {

            /**
             * A utility method to get the parent path out of a class-path like resource path.
             *
             * @param path
             *            a slash delimite path
             * @return the parent path of the given {@code path} or {@code null} if the path has no parent
             */
            public static String parentPath(String path) {
                if (path == null || "/".equals(path)) {
                    return null;
                } else {
                    int lastSlash = path.lastIndexOf('/');
                    if (lastSlash < 0) {
                        return null;
                    } else if (lastSlash == 0) {
                        return "/";
                    } else {
                        return path.substring(0, lastSlash);
                    }
                }
            }

            private final Charset encoding;
            private final ClassLoader loader;
            private final String path;

            ClassPathResourcePath(ClassLoader loader, String path, Charset encoding) {
                super();
                this.path = path;
                this.loader = loader;
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
                ClassPathResourcePath other = (ClassPathResourcePath) obj;
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
            public ResourcePath getParent() {
                String parentPath = parentPath(path);
                return parentPath == null ? null : new ClassPathResourcePath(loader, parentPath, encoding);
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
            public boolean hasParent() {
                return parentPath(path) != null;
            }

            /** {@inheritDoc} */
            @Override
            public Resource resolve(String name) {
                String newPath = path + "/" + name;
                return new Resource.Resources.ClassPathResource(loader, newPath, encoding);
            }

            /** {@inheritDoc} */
            @Override
            public String toString() {
                return "classpath:" + getPath();
            }

        }

        /**
         * A {@link ResourcePath} implementation based on {@code java.nio.file.Path}. To create a new instance use
         * {@link ResourcePaths#ofPath(Path, Charset)}
         */
        static class PathResourcePath implements ResourcePath {

            private final Charset encoding;
            private final Path path;

            PathResourcePath(Path path, Charset encoding) {
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
                PathResourcePath other = (PathResourcePath) obj;
                return this.path.equals(other.path);
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
            public boolean hasParent() {
                return path.getParent() != null;
            }

            /** {@inheritDoc} */
            @Override
            public Resource resolve(String name) {
                return new Resource.Resources.PathResource(path.resolve(name), encoding);
            }

            @Override
            public String toString() {
                return "path:" + getPath();
            }
        }

        /**
         * A path in a {@link Resources.StringResourceTree}.
         */
        static class StringResourcePath implements ResourcePath {

            private final List<String> path;
            private final Map<List<String>, Resource> resources;

            StringResourcePath(List<String> path, Map<List<String>, Resource> resources) {
                super();
                this.path = path;
                this.resources = resources;
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
                StringResourcePath other = (StringResourcePath) obj;
                return this.path.equals(other.path);
            }

            /** {@inheritDoc} */
            @Override
            public ResourcePath getParent() {
                return hasParent() ? new StringResourcePath(path.subList(0, path.size() - 1), resources) : null;
            }

            /** {@inheritDoc} */
            @Override
            public String getPath() {
                return Resource.Resources.StringResourceTree.toString(path);
            }

            /** {@inheritDoc} */
            @Override
            public int hashCode() {
                return path.hashCode();
            }

            /** {@inheritDoc} */
            @Override
            public boolean hasParent() {
                return path.size() > 1;
            }

            /** {@inheritDoc} */
            @Override
            public Resource resolve(String name) {
                List<String> newPath = new ArrayList<>(path);
                newPath.add(name);
                Resource result = resources.get(newPath);
                if (result == null) {
                    result = new Resource.Resources.StringResource(resources, newPath, null);
                }
                return result;
            }

            /** {@inheritDoc} */
            @Override
            public String toString() {
                return "string:" + getPath();
            }

        }

        /**
         * @param path
         *            the {@link Path} to create a new {@link ResourcePath} from
         * @param encoding
         *            the {@link Charset} to use when reading {@link Resource}s from the returned {@link ResourcePath}
         * @return a new {@link PathResourcePath}
         */
        public static ResourcePath ofPath(Path path, Charset encoding) {
            return new PathResourcePath(path, encoding);
        }

        private ResourcePaths() {
        }
    }

    /**
     * @return the parent {@link ResourcePath} of this {@link ResourcePath} or {@code null} of this {@link ResourcePath}
     *         has no parent
     */
    ResourcePath getParent();

    /**
     * @return this path as a string; the segments are separated by slash {@code /}
     */
    String getPath();

    /**
     * @return {@code true} if this {@link ResourcePath} has parent; {@code false} otherwise
     */
    boolean hasParent();

    /**
     * Resolves an immediate child of this {@link ResourcePath}.
     *
     * @param name
     *            the name of the child; should not contain path separators
     * @return the child {@link Resource}
     */
    Resource resolve(String name);
}