package com.kleegroup.lord.moteur.util;
import java.io.Serializable;
import java.util.Comparator;

import com.kleegroup.lord.moteur.Fichier;

/**Compare deux fichier par "groupe".
 *le groupe est défini par l'utilisateur lors de la création du fichier. 
 *  */
public class FichierComparteurOrdreTopo implements Serializable , Comparator<Fichier>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5L;

	/**{@inheritDoc} */
	@Override
	public int compare(Fichier arg0, Fichier arg1) {
		return arg0.getNiveauTopo() - arg1.getNiveauTopo();
	}

}
