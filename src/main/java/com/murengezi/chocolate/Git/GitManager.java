package com.murengezi.chocolate.Git;

import org.lwjgl.opengl.Display;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-27 at 14:59
 */
public class GitManager {

	private GitRepositoryState gitRepositoryState;

	public GitManager() {
		try {
			getGitRepositoryState();
			Display.setTitle("Chocolate (1.8.10-" + gitRepositoryState.getCommitIdAbbrev() + "/" + gitRepositoryState.getBranch() + ")");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getGitRepositoryState() throws IOException {
		if (this.gitRepositoryState == null) {
			Properties properties = new Properties();
			properties.load(getClass().getClassLoader().getResourceAsStream("chocolate.properties"));

			this.gitRepositoryState = new GitRepositoryState(properties);
		}
	}
}
