module schema {
	requires transitive model;
	requires jakarta.xml.bind;
	opens com.kleegroup.lord.config.xml to jakarta.xml.bind;
	exports com.kleegroup.lord.moteur.config;
}