package de.schwarzes_brett.backing.validator_converter;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.data_access.exception.AdDoesNotExistException;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converter for the ad id view parameter.
 *
 * @author Jonas Elsper
 */
@Named
@Dependent
@FacesConverter(value = "adIDConverter", managed = true)
public class AdIDConverter implements Converter<Integer> {

    @Inject
    private Dictionary dict;

    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public AdIDConverter() {}

    /**
     * Converts ad id input string to an integer.
     *
     * @param facesContext Contains all the per-request state information related to the processing of a single request.
     * @param uiComponent  The corresponding ui-component the value comes from.
     * @param s            The ad id as string.
     * @return The ad id as integer.
     */
    @Override
    public Integer getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        int id;
        try {
            id = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            final String msg = dict.get("f_ad_idMustBeAInteger");
            logger.log(Level.WARNING, "View Param ad id must be of type integer.");
            throw new AdDoesNotExistException(msg, e);
        }
        return id;
    }

    /**
     * Converts an ad id to string.
     *
     * @param facesContext Contains all the per-request state information related to the processing of a single request.
     * @param uiComponent  The corresponding ui-component the value comes from.
     * @param integer      The ID of the ad to be converted.
     * @return The ID of the ad as string.
     */
    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Integer integer) {
        return integer == null ? null : integer.toString();
    }
}
