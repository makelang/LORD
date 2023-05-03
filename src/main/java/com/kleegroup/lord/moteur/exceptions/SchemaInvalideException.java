package com.kleegroup.lord.moteur.exceptions;

import java.io.IOException;

/**
 * Excpetion levee si le schema est invalide.
 *
 */
public class SchemaInvalideException extends IOException {

	private static final long serialVersionUID = 1L;
	
	public SchemaInvalideException() {
		super();
	}
	
	public SchemaInvalideException(Exception e) {
		super(e);
	}


}
