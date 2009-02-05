package org.gotpike.pdt.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;

class StringStorage extends PlatformObject 
implements IStorage {
private String string;

StringStorage(String input) {
this.string = input;
}

public InputStream getContents() throws CoreException {
return new ByteArrayInputStream(string.getBytes());
}

public IPath getFullPath() {
return null;
}

public String getName() {
int len = Math.min(5, string.length());
return string.substring(0, len).concat("...");
}

public boolean isReadOnly() {
return true;
}
}