package com.murengezi.minecraft.client.resources;

import com.google.common.collect.Sets;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

import java.io.*;
import java.util.Objects;
import java.util.Set;

public class FolderResourcePack extends AbstractResourcePack {

	public FolderResourcePack(File resourcePackFile) {
		super(resourcePackFile);
	}

	protected InputStream getInputStreamByName(String name) throws IOException {
		return new BufferedInputStream(new FileInputStream(new File(this.resourcePackFile, name)));
	}

	protected boolean hasResourceName(String name) {
		return (new File(this.resourcePackFile, name)).isFile();
	}

	public Set<String> getResourceDomains() {
		Set<String> set = Sets.newHashSet();
		File file1 = new File(this.resourcePackFile, "assets/");

		if (file1.isDirectory()) {
			for (File file2 : Objects.requireNonNull(file1.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY))) {
				String s = getRelativeName(file1, file2);

				if (!s.equals(s.toLowerCase())) {
					this.logNameNotLowercase(s);
				} else {
					set.add(s.substring(0, s.length() - 1));
				}
			}
		}

		return set;
	}

}
