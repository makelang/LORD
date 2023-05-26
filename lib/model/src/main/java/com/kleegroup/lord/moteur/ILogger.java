package com.kleegroup.lord.moteur;

import java.util.List;



/**
 * @author maazreibi
 * L'interface Loggueur permet d'utiler différentes sources pour signaler les erreurs rencontrée
 * 
 */
public interface ILogger {

	/**
	 * Inscrit dans le log la collection des messages d'erreur.
	 * @param listeErreurs  - La collection d'erreurs à logger
	 */
	default void log(final List<IErreur> listeErreurs) {
		for (IErreur s : listeErreurs) {
			log(s);
		}
	}
	/**
	 * Inscrit l'erreur dans le log.
	 * @param err l'erreur à inscrire
	 */
	void log(IErreur err);
	/**
	 * Instruit le loggueur à vider son buffer s'il en a.
	 */
	void flushAndClose();
	
	/**
	* Définit le nom du fichier source des erreurs de ce loggeur.
	* @param nomFichier le nom du fichier source des erreurs de ce loggeur
	*/
	void setNomFichier(String nomFichier);
	
	 /**
	 * @return le nom du fcihier source des erreurs.
	 */
	String getNomFichier();
	
	/**
	 * @return true si les erreurs contiennent le champ "Reference".
	 */
	boolean hasReferenceColonne();
	/**
	 * @param val true si les erreurs contiendront le champ reference, false sinon.
	 */
	void setReferenceColonne(boolean val);



}
