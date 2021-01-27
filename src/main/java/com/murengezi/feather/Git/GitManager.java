package com.murengezi.feather.Git;

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
			Display.setTitle("Feather (1.8.9-" + gitRepositoryState.commitIdAbbrev + "/" + gitRepositoryState.branch + ")");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getGitRepositoryState() throws IOException {
		if (this.gitRepositoryState == null) {
			Properties properties = new Properties();
			properties.load(getClass().getClassLoader().getResourceAsStream("feather.properties"));

			this.gitRepositoryState = new GitRepositoryState(properties);
		}
	}
}
