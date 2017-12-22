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
import java.util.Map;

import org.ec4j.core.Resource.Resources.ClassPathResource;
import org.ec4j.core.Resource.Resources.PathResource;
import org.ec4j.core.Resource.Resources.StringResource;
import org.ec4j.core.model.Ec4jPath;

/**
 * A directory path in filesystem like {@link Resource} hierarchies. The implementations must override
 * {@link Object#hashCode()} and {@link Object#equals(Object)}
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

            final Charset encoding;
            final ClassLoader loader;
            final Ec4jPath path;

            ClassPathResourcePath(ClassLoader loader, Ec4jPath path, Charset encoding) {
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
            public boolean hasParent() {
                return path.getParentPath() != null;
            }

            /** {@inheritDoc} */
            @Override
            public Resource relativize(Resource resource) {
                if (resource instanceof ClassPathResource) {
                    ClassPathResource cpResource = (ClassPathResource) resource;
                    return new ClassPathResource(cpResource.loader, path.relativize(cpResource.path),
                            cpResource.encoding);
                } else {
                    throw new IllegalArgumentException(
                            this.getClass().getName() + ".relativize(Resource resource) can handle only instances of "
                                    + ClassPathResource.class.getName());
                }
            }

            /** {@inheritDoc} */
            @Override
            public Resource resolve(String name) {
                return new Resource.Resources.ClassPathResource(loader, path.resolve(name), encoding);
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
            public Ec4jPath getPath() {
                return Ec4jPath.Ec4jPaths.of(path);
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
            public Resource relativize(Resource resource) {
                if (resource instanceof PathResource) {
                    PathResource pathResource = (PathResource) resource;
                    return new PathResource(this.path.relativize(pathResource.path), pathResource.encoding);
                } else {
                    throw new IllegalArgumentException(
                            this.getClass().getName() + ".relativize(Resource resource) can handle only instances of "
                                    + PathResource.class.getName());
                }
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

            private final Ec4jPath path;
            private final Map<Ec4jPath, Resource> resources;

            StringResourcePath(Ec4jPath path, Map<Ec4jPath, Resource> resources) {
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
            public boolean hasParent() {
                return path.getParentPath() != null;
            }

            /** {@inheritDoc} */
            @Override
            public Resource relativize(Resource resource) {
                if (resource instanceof StringResource) {
                    StringResource strResource = (StringResource) resource;
                    return new StringResource(strResource.resources, path.relativize(strResource.path),
                            strResource.content);
                } else {
                    throw new IllegalArgumentException(
                            this.getClass().getName() + ".relativize(Resource resource) can handle only instances of "
                                    + StringResource.class.getName());
                }
            }

            /** {@inheritDoc} */
            @Override
            public Resource resolve(String name) {
                Ec4jPath newPath = path.resolve(name);
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
    Ec4jPath getPath();

    /**
     * @return {@code true} if this {@link ResourcePath} has parent; {@code false} otherwise
     */
    boolean hasParent();

    /**
     * Constructs a {@link Resource} whose path is a relative path between this {@link ResourcePath#getPath()} and the
     * given {@link Resource#getPath()}.
     *
     * @param resource
     *            the {@link Resource} to relativize
     * @return a new relativized {@link Resource}
     */
    Resource relativize(Resource resource);

    /**
     * Resolves an immediate child of this {@link ResourcePath}.
     *
     * @param name
     *            the name of the child; should not contain path separators
     * @return the child {@link Resource}
     */
    Resource resolve(String name);
}