package de.schwarzes_brett.backing.dictionary;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for fetching help clauses from resources.
 *
 * @author Kilian Lichtenauer
 */
@Named
@Dependent
public class HelpClauses {

    @Inject
    private FacesContext facesContext;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public HelpClauses() {
    }

    /**
     * Translates given phrases to a {@code key}-identified language.
     *
     * @param key Identifies a phrase from the resource bundle.
     * @return Returns the phrase's translation.
     */
    public String get(String key) {
        logger.log(Level.FINE, "Help phrase with key:" + key + "is fetched.");
        return facesContext.getApplication().getResourceBundle(facesContext, "helpPhrases")
                           .getString(key);
    }
}
