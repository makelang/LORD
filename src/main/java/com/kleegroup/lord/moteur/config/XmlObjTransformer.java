package com.kleegroup.lord.moteur.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.kleegroup.lord.config.xml.ObjectFactory;
import com.kleegroup.lord.config.xml.TypeColonne;
import com.kleegroup.lord.config.xml.TypeContrainte;
import com.kleegroup.lord.config.xml.TypeContrainteMultiColonne;
import com.kleegroup.lord.config.xml.TypeFichier;
import com.kleegroup.lord.config.xml.TypeSchema;
import com.kleegroup.lord.moteur.Colonne;
import com.kleegroup.lord.moteur.Colonne.PRESENCE;
import com.kleegroup.lord.moteur.ContrainteMultiCol;
import com.kleegroup.lord.moteur.ContrainteRegistry;
import com.kleegroup.lord.moteur.ContrainteUniCol;
import com.kleegroup.lord.moteur.Fichier;
import com.kleegroup.lord.moteur.Schema;
import com.kleegroup.lord.moteur.contraintes.ContrainteListeValeursPermises;
import com.kleegroup.lord.moteur.contraintes.ContrainteRegex;
import com.kleegroup.lord.moteur.contraintes.ContrainteTRUE;
import com.kleegroup.lord.moteur.contraintes.ContrainteTaille;
import com.kleegroup.lord.moteur.contraintes.ContrainteTypeChaineDeCaractere;
import com.kleegroup.lord.moteur.contraintes.ContrainteTypeDate;
import com.kleegroup.lord.moteur.contraintes.ContrainteTypeDecimal;
import com.kleegroup.lord.moteur.contraintes.ContrainteTypeEntier;
import com.kleegroup.lord.moteur.contraintes.ContrainteUnique;
import com.kleegroup.lord.moteur.exceptions.SchemaInvalideException;
import com.kleegroup.lord.moteur.util.SeparateurChamps;
import com.kleegroup.lord.moteur.util.SeparateurDecimales;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Sert à tranformer un schéma XML (JAXB) en schéma de moteur.
 */
public class XmlObjTransformer {
	
	private Schema schemaEquiv = new Schema();
	private List<Colonne> colonnesPrinc = new ArrayList<>();
	private List<String> fichierRef = new ArrayList<>();
	private List<String> colonneRef = new ArrayList<>();
	private SeparateurChamps sepChamps = null;
	private SeparateurDecimales sepDecimal = null;
	
	
	/**
	 * Construit un schema a partir d'un XML lu dans inputStream.
	 * 
	 * @param inputStream
	 *            sert a lire le fichier
	 * @return le schema XML
	 * @throws SchemaInvalideException
	 *             si la conversion à partir du XML échoue
	 */
	public static Schema fromXML(InputStream inputStream) throws SchemaInvalideException {

		JAXBContext jc;
		try (InputStream is = inputStream){
			jc = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
			final Unmarshaller u = jc.createUnmarshaller();
			final JAXBElement<?> schemaXML = (JAXBElement<?>) u.unmarshal(is);
			final XmlObjTransformer trans = new XmlObjTransformer();
			
			return trans.transform((TypeSchema) schemaXML.getValue());
		} catch (IOException | JAXBException e) {
			throw new SchemaInvalideException(e);
		}


	}

	/**
	 * @param schemaOriginal	le schema extrait du XML
	 * @return 					le schema de moteur
	 */
	public Schema transform(TypeSchema schemaOriginal) {
		// En cas d'absence de configuration (nouveau fichier)
		if (schemaOriginal == null) {
			return new Schema();
		}

		// Séparateur de décimales
		try {
			sepDecimal = SeparateurDecimales.valueOf(schemaOriginal.getSeparateurDecimal());
		} catch (Exception e) {
			sepDecimal = SeparateurDecimales.SEPARATEUR_VIRGULE;
		}
		schemaEquiv.setSeparateurDecimales(sepDecimal);
		
		// Séparateur de champs
		try {
			sepChamps = SeparateurChamps.valueOf(schemaOriginal.getSeparateurChamps());
		} catch (Exception e) {
			sepChamps = SeparateurChamps.SEPARATEUR_POINT_VIRGULE;
		}
		schemaEquiv.setSeparateurChamp(sepChamps);

		// Autres paramètres globaux
		schemaEquiv.setEncoding(schemaOriginal.getEncodage());
		schemaEquiv.setAfficherExportLogs(schemaOriginal.isAfficherExportLogs());
		for (TypeFichier fichierOriginal : schemaOriginal.getFichier()) {
			schemaEquiv.addFichier(transform(fichierOriginal));
		}
		traiterRefrence();
		return schemaEquiv;
	}

	private void traiterRefrence() {
		for (int i = 0; i < colonnesPrinc.size(); i++) {
			Fichier fRef = schemaEquiv.getFichier(fichierRef.get(i));
			if (fRef != null) {
				Colonne cRef = fRef.getColonne(colonneRef.get(i));
				if (cRef != null) {
					// la colonne a été trouvée
					colonnesPrinc.get(i).getFichierParent().addReference(colonnesPrinc.get(i).getNom(), cRef);
				}
			}
		}
	}

	private Fichier transform(TypeFichier fichierOriginal) {
		Fichier fichierEquiv = new Fichier(fichierOriginal.getNom(), fichierOriginal.getPrefixNom());
		fichierEquiv.setExtension(fichierOriginal.getExtension());
		for (TypeColonne colonneOriginale : fichierOriginal.getColonnes().getColonne()) {
			fichierEquiv.addColonne(transform(colonneOriginale));
		}
		for (TypeContrainteMultiColonne contrainteOriginale : fichierOriginal.getContrainte()) {
			fichierEquiv.addContrainteMultiCol(transform(contrainteOriginale));
		}
		fichierEquiv.setNbLignesEntete(fichierOriginal.getNbLignesEntete());
		fichierEquiv.setSeuilAbandon(fichierOriginal.getSeuilErreurs());
		fichierEquiv.setGroupe(fichierOriginal.getGroupe());
		fichierEquiv.setNomCategorie(fichierOriginal.getCategorie());
		return fichierEquiv;
	}

	private Colonne transform(TypeColonne colonneOriginale) {
		Colonne colonneEquiv = new Colonne(colonneOriginale.getNom());
		String desc = colonneOriginale.getDescription();
		colonneEquiv.setDescription(desc);
		String presence = colonneOriginale.getPresenceValeur();

		colonneEquiv.setColonneReference(colonneOriginale.isColonneDeReference());

		if ("INTERDITE".equals(presence)) {
			colonneEquiv.setPresenceValeur(PRESENCE.INTERDITE);
			return colonneEquiv;
		}
		if ("OBLIGATOIRE".equals(presence)) {
			colonneEquiv.setPresenceValeur(PRESENCE.OBLIGATOIRE);
		}

		for (TypeContrainte contrainteOriginale : colonneOriginale.getContrainte()) {
			if ("ContrainteReference".equals(contrainteOriginale.getType())) {
				addReference(colonneEquiv, contrainteOriginale.getParam().get(0), contrainteOriginale.getParam().get(1));
			} else {
				// on ignore ContrainteTRUE parce que cette contrainte ne fait rien
				if (!"ContrainteTRUE".equals(contrainteOriginale.getType())) {
					colonneEquiv.addContrainte(transform(contrainteOriginale));
				}
			}
		}
		return colonneEquiv;
	}

	private ContrainteMultiCol transform(TypeContrainteMultiColonne contrainteOriginale) {
		String cols[] = new String[contrainteOriginale.getColonne().size()];
		for (int i = 0; i < cols.length; i++) {
			cols[i] = contrainteOriginale.getColonne().get(i).getNom();
		}
		return ContrainteRegistry.ContrainteMulticolEnum.createConstraint(contrainteOriginale.getId(), contrainteOriginale.getMessageErreur(), contrainteOriginale.getNomFonction(), cols);
	}

	private ContrainteUniCol transform(TypeContrainte contrainteOriginale) {
		String type = contrainteOriginale.getType();
		ContrainteUniCol res = createContrainteSimple(type);
		if (res != null) {
			return res;
		}

		if ("ContrainteTaille".equals(type)) {
			int taille = Integer.parseInt(contrainteOriginale.getParam().get(0));
			return new ContrainteTaille(taille);
		}
		if ("ContrainteRegex".equals(type)) {
			return new ContrainteRegex(contrainteOriginale.getParam().get(0));
		}
		if ("ContrainteTypeDate".equals(type)) {
			return new ContrainteTypeDate(contrainteOriginale.getParam().get(0));
		}
		if ("ContrainteTypeDecimal".equals(type)) {
			return new ContrainteTypeDecimal(Integer.parseInt(contrainteOriginale.getParam().get(0)), sepDecimal, Integer.parseInt(contrainteOriginale.getParam().get(1)));
		}
		if ("ContrainteListeValeursPermises".equals(type)) {
			String[] valeursPermises = new String[contrainteOriginale.getParam().size()];
			for (int i = 0; i < valeursPermises.length; i++) {
				valeursPermises[i] = contrainteOriginale.getParam().get(i);
			}
			return new ContrainteListeValeursPermises(valeursPermises);
		}
		return new ContrainteTRUE();
	}

	private static ContrainteUniCol createContrainteSimple(String type) {
		if ("ContrainteUnique".equals(type)) {
			return new ContrainteUnique();
		}
		if ("ContrainteTypeEntier".equals(type)) {
			return new ContrainteTypeEntier();
		}
		if ("ContrainteTypeChaineDeCaractere".equals(type)) {
			return new ContrainteTypeChaineDeCaractere();
		}
		return null;
	}

	private void addReference(Colonne c, String nomFichier, String nomColonne) {
		colonnesPrinc.add(c);
		fichierRef.add(nomFichier);
		colonneRef.add(nomColonne);
	}
}
