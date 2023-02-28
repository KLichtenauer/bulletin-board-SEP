package de.schwarzes_brett.backing.dictionary;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides phrases in a selected language. Responsible for translating texts on buttons or in error messages in the set language of the user.
 *
 * @author Kilian Lichtenauer
 */
@Named
@Dependent
public class Dictionary {

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
    public Dictionary() {
    }

    /**
     * Translates given phrases to a {@code key}-identified language.
     *
     * @param key Identifies a phrase from the resource bundle.
     * @return Returns the phrase's translation.
     */
    public String get(String key) {
        logger.log(Level.FINE, "Phrase with key:" + key + "is fetched.");
        return facesContext.getApplication().getResourceBundle(facesContext, "phrases")
                           .getString(key);
    }
}
