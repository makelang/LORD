module model {
	requires transitive org.apache.log4j;
	requires com.opencsv;
	exports com.kleegroup.lord.moteur;
	exports com.kleegroup.lord.moteur.contraintes;
	exports com.kleegroup.lord.moteur.exceptions;
}
