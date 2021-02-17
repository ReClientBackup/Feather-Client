package com.murengezi.chocolate.Git;

import java.util.Properties;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-27 at 14:56
 */

public class GitRepositoryState {

	private final String branch, commitId, commitIdAbbrev;

	public GitRepositoryState(Properties properties) {
		this.branch = properties.getProperty("git.branch");
		this.commitId = properties.getProperty("git.commit.id");
		this.commitIdAbbrev = properties.getProperty("git.commit.id.abbrev");
	}

	public String getBranch() {
		return branch;
	}

	public String getCommitId() {
		return commitId;
	}

	public String getCommitIdAbbrev() {
		return commitIdAbbrev;
	}
}
