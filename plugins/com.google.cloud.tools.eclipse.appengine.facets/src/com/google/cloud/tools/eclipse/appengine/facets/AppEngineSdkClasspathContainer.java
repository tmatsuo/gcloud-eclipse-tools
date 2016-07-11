package com.google.cloud.tools.eclipse.appengine.facets;

import java.nio.file.Files;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.eclipse.sdk.CloudSdkProvider;

public final class AppEngineSdkClasspathContainer implements IClasspathContainer {

  public static final String CONTAINER_ID = "AppEngineSDK";

  @Override
  public IPath getPath() {
    return new Path(AppEngineSdkClasspathContainer.CONTAINER_ID);
  }

  @Override
  public int getKind() {
    return IClasspathEntry.CPE_CONTAINER;
  }

  @Override
  public String getDescription() {
    return "App Engine SDKs";
  }

  @Override
  public IClasspathEntry[] getClasspathEntries() {
    CloudSdk cloudSdk = new CloudSdkProvider().getCloudSdk();
    if (cloudSdk.getJavaAppEngineSdkPath() != null) {
      java.nio.file.Path jarFile = cloudSdk.getJavaToolsJar();
      if (Files.exists(jarFile)) {
        IClasspathEntry appEngineToolsEntry =
            JavaCore.newLibraryEntry(new Path(jarFile.toString()),
                                     null /* sourceAttachmentPath */,
                                     null /* sourceAttachmentRootPath */,
                                     true /* isExported */);
        return new IClasspathEntry[]{ appEngineToolsEntry };
      }
    }
    return new IClasspathEntry[0];
  }

}
