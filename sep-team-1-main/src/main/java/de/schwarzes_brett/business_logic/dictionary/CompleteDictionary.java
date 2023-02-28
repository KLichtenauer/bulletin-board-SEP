package de.schwarzes_brett.business_logic.dictionary;

import de.schwarzes_brett.logging.LoggerProducer;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Provides the complete resource bundle, depending on the given locale.
 *
 * @author Tim-Florian Feulner
 */
public final class CompleteDictionary {

    private static final Logger LOGGER = LoggerProducer.get(CompleteDictionary.class);

    /**
     * The location of the resource bundle.
     */
    private static final String RESOURCE_BUNDLE_LOCATION = "de.schwarzes_brett.backing.dictionary.i18n.phrases";

    /**
     * Private constructor, as this class is a utility class.
     */
    private CompleteDictionary() {}

    /**
     * Fetches the value from a resource bundle, depending on the key and on the locale.
     *
     * @param key    The key of the phrase to fetch.
     * @param locale The locale to specify which language to use.
     * @return The resulting phrase.
     */
    public static String get(String key, Locale locale) {
        LOGGER.fine("Phrase with key " + key + " for locale " + locale.toString() + " was fetched.");
        ResourceBundle resourceBundle = PropertyResourceBundle.getBundle(RESOURCE_BUNDLE_LOCATION, locale);
        return resourceBundle.getString(key);
    }

}
