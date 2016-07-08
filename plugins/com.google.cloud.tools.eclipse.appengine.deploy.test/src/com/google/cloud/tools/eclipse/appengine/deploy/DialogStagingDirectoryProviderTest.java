package com.google.cloud.tools.eclipse.appengine.deploy;

import org.junit.Test;

public class DialogStagingDirectoryProviderTest {

  @Test(expected = NullPointerException.class)
  public void testGetStagingDirectory_nullEvent() {
    new DialogStagingDirectoryProvider().getStagingDirectory(null);
  }

}
