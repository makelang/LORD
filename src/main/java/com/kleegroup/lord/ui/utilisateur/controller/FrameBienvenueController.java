package com.kleegroup.lord.ui.utilisateur.controller;

import java.io.FileNotFoundException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.kleegroup.lord.moteur.exceptions.SchemaInvalideException;
import com.kleegroup.lord.ui.utilisateur.model.Model;
import com.kleegroup.lord.ui.utilisateur.view.FrameBienvenue;

/**
 *	Controlleur pour FrameBienvenue.
 *
 */
public class FrameBienvenueController extends FrameController<FrameBienvenue,Model > {

    /**
     * Construit un controlleur pour FrameBienvenue.
     * @param fpc le controlleur de la fenetre principale.
     */
    public FrameBienvenueController(FenetrePrincipaleUtilisateurController fpc) {
	super(fpc);
    }

    /** {@inheritDoc} */
    @Override
    public boolean canGoBack() {
	return false;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean canGoNext(){
	try {
		rechargerFichierConf();
		
		}catch (FileNotFoundException e) {
		    
		    JOptionPane.showInputDialog(null, resourceMap.getString("ErreurChargementConf")+" " +e.getMessage());

            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "JPG & GIF Images", "jpg", "gif");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
               System.out.println("You chose to open this file: " +
                    chooser.getSelectedFile().getName());
            }

		    java.awt.EventQueue.invokeLater(new Runnable() {
			    @Override
				public void run() {
		    fenetrePrincipaleController.quitter();
			    }});
		} catch (SchemaInvalideException e) {
		    JOptionPane.showMessageDialog(null, resourceMap.getString("ErreurConfInvalide") +e.getMessage());
		}
		return true;
    }

    /** {@inheritDoc} */
    @Override
    String getName() {
	return "FrameBienvenue";
    }

    /** {@inheritDoc} */
    @Override
    protected FrameBienvenue createView() {
	return new FrameBienvenue();
    }

    /** {@inheritDoc} */
    @Override
    public void activate() {
	    fenetrePrincipale.setEnabledPrecedent(false);
		fenetrePrincipale.setEtape(0);
    }

    private void rechargerFichierConf() throws FileNotFoundException, SchemaInvalideException {
	    fenetrePrincipaleController.chargerFichierConf();
	    fenetrePrincipale.clear();
	    fenetrePrincipale.addFrame(getFrame(), getName());
	    setNext(new FrameSelectionCheminsFichiersController(fenetrePrincipaleController));
    }

    /** {@inheritDoc} */
    @Override
    public void deactivate() {
	fenetrePrincipale.setEnabledPrecedent(true);
    }

    @Override
    protected Model createModel() {
	return null;
    }

}
