package com.kleegroup.lord.ui;

import java.awt.EventQueue;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.UIManager;

/**
 * La classe qui démarre l'application.
 */
public abstract class UILauncher {
	protected File execDir;

	/**
	 * Démarre le programme.
	 */
	public final void run() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			/** Ne fait rien */
			e.getMessage();
		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				montrerFenetrePrincipale();
			}
		});
	}


	protected abstract void montrerFenetrePrincipale();

	protected File getExecDir() {
		/**
		 * http://weblogs.java.net/blog/kohsuke/archive/2007/04/how_to_convert.html  
		 * Pour corriger un bug. Le programme ne marchait pas 
		 * si le repertoire contenait un espace (%20) dans son chemin.
		 * */

		final URL location = getClass().getProtectionDomain().getCodeSource().getLocation();
		File parent;
		try {
			parent = new File(location.toURI());
		} catch (URISyntaxException e1) {
			parent = new File(location.getPath());
		}
		return parent.getParentFile();
	}
}

