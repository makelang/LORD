package com.kleegroup.lord.moteur.contraintes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.kleegroup.lord.moteur.ContrainteUniCol;

/**
 * Effectue des vérifications simple sur une chaine de caractères. Notamment, la 
 * chaine ne doit pas contenir les caractères '"' ou '\n'
 * @author maazreibi
 *
 */
public class ContrainteTypeChaineDeCaractere extends ContrainteUniCol {
	protected String[] caracteresInterdits = new String[] { "\n" };

	/** {@inheritDoc}*/
	@Override
	public boolean estConforme(final String valeur) {
		return !Arrays.asList(valeur).contains(valeur);
	}

	/**{@inheritDoc}*/
	@Override
	public List<String> getListeParam() {
		return Collections.emptyList();
	}

	/**{@inheritDoc}*/
	@Override
	public boolean isContrainteType() {
		return true;
	}

	/**{@inheritDoc}*/
	@Override
	public ContrainteTypeChaineDeCaractere copy() {
		return new ContrainteTypeChaineDeCaractere();
	}

}
