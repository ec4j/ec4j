package org.eclipse.ec4j;

/**
 * Exception which is thrown by {@link EditorConfig#getProperties(String)} if an invalid version number is specified
 *
 * @author Dennis.Ushakov
 */
public class VersionException extends EditorConfigException {
  public VersionException(String s) {
    super(s, null);
  }
}
