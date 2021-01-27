package com.murengezi.feather.Git;

import java.util.Locale;
import java.util.Properties;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-27 at 14:56
 */

public class GitRepositoryState {

	String branch;
	String commitId;
	String commitIdAbbrev;

	public GitRepositoryState(Properties properties) {
		this.branch = properties.getProperty("git.branch");
		this.commitId = properties.getProperty("git.commit.id");
		this.commitIdAbbrev = properties.getProperty("git.commit.id.abbrev");
	}
}
