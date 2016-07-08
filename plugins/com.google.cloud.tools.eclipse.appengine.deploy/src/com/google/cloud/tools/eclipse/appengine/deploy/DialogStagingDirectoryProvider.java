package com.google.cloud.tools.eclipse.appengine.deploy;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.common.base.Preconditions;

/**
 * Prompts the user to select a directory for staging.
 */
// TODO should be moved to separate UI bundle (if kept at all in deploy's final implementation)
public class DialogStagingDirectoryProvider implements StagingDirectoryProvider {

  @Override
  public String getStagingDirectory(ExecutionEvent executionEvent) {
    Preconditions.checkNotNull(executionEvent, Messages.getString("argument.executionevent.null")); //$NON-NLS-1$
    
    DirectoryDialog directoryDialog = new DirectoryDialog(HandlerUtil.getActiveShell(executionEvent));
    directoryDialog.setText(Messages.getString("dialog.staging.directory.provider.title")); //$NON-NLS-1$
    directoryDialog.setMessage(Messages.getString("dialog.staging.directory.provider.message")); //$NON-NLS-1$
    return directoryDialog.open();
  }
}
