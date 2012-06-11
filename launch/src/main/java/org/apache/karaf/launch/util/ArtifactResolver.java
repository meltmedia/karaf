package org.apache.karaf.launch.util;

import java.net.URI;

public interface ArtifactResolver {
    URI resolve(URI artifactUri);
}
