package de.schwarzes_brett.backing.lifecycle;

import de.schwarzes_brett.backing.exception.UnauthorisedAccessException;
import de.schwarzes_brett.backing.session.UserSession;
import de.schwarzes_brett.logging.LoggerProducer;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;

import java.io.Serial;
import java.util.logging.Logger;


/**
 * Manages access permissions of users by checking if the role, saved in the current session, is matching the needed role for certain actions. If not,
 * the user gets informed that he doesn't have the permission for this action.
 *
 * @author Daniel Lipp
 */
public class TrespassListener implements PhaseListener {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public TrespassListener() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPhase(PhaseEvent event) {
        UserSession userSession = CDI.current().select(UserSession.class).get();
        Logger logger = LoggerProducer.get(TrespassListener.class);
        FacesContext context = event.getFacesContext();
        final UIViewRoot viewRoot = context.getViewRoot();
        if (viewRoot != null) {
            final String url = viewRoot.getViewId();
            if (url.startsWith("/view/admin") && !userSession.isAdmin()) {
                logger.info("Someone tried to access an admin site without the rights.");
                throw new UnauthorisedAccessException("User is not an admin.");
            } else if (url.startsWith("/view/user") && userSession.isAnonymous()) {
                logger.info("Someone tried to access a user site without being logged in.");
                throw new UnauthorisedAccessException("User is not logged in.");
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void beforePhase(PhaseEvent event) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

}
