package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.dictionary.HelpClauses;
import de.schwarzes_brett.backing.session.UserSession;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Manages the help information.
 *
 * @author Kilian Lichtenauer
 */
@Named
@ViewScoped
public class HelpBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Currently selected help clause which get shown on the page.
     */
    private String helpClause;

    /**
     * The bean for finding out if an ad is getting edited or created.
     */
    @Inject
    private EditAdBean adBean;

    /**
     * The bean for finding out if a category is getting edited or created.
     */
    @Inject
    private EditCategoryBean editCategoryBean;

    /**
     * Needed for fetching help clauses from resources.
     */
    @Inject
    private transient HelpClauses helpClauses;

    /**
     * Information about the current user.
     */
    @Inject
    private UserSession userSession;

    /**
     * Tells if the help is currently visible on the page.
     */
    private boolean helpIsVisible = false;

    /**
     * Default constructor.
     */
    public HelpBean() {
    }

    /**
     * Returns if the help clause gets shown currently.
     *
     * @return True if the help is visible.
     */
    public boolean isHelpIsVisible() {
        return helpIsVisible;
    }

    /**
     * Sets the visibility of the help clause.
     *
     * @param helpIsVisible The value if the help is visible.
     */
    public void setHelpIsVisible(boolean helpIsVisible) {
        this.helpIsVisible = helpIsVisible;
    }

    /**
     * Initializes the bean.
     */
    @PostConstruct
    public void init() {
        logger.fine("Help bean gets initialized.");
        this.helpClause = "";
    }

    /**
     * Gets called when the user wants to redirect to the help page.
     *
     * @return The corresponding facelet for the help page.
     */
    public String showHelp() {
        logger.fine("Help messages gets shown.");
        String title = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        helpIsVisible = !helpIsVisible;
        title = title.split("\\?")[0];
        StringBuilder builder = new StringBuilder();
        builder.append(getHeaderClause()).append("\n");

        if (title.equals("/view/admin/editCategory.xhtml")) {
            builder.append(getEditCategoryClause());
        } else if (title.equals("/view/user/editAd.xhtml")) {
            builder.append(getEditAdClause());
        } else if (title.equals("/view/public/ad.xhtml")) {
            builder.append(getAdClause());
        } else if (title.equals("/view/user/profile.xhtml")) {
            builder.append(getProfileClause());
        } else if (title.equals("/view/user/welcome.xhtml") || title.equals("/view/user/adManagement.xhtml")
                   || title.equals("/view/public/landing.xhtml") || title.equals("/view/admin/adAdministration.xhtml")) {
            builder.append(helpClauses.get(title));
            builder.append(helpClauses.get("pagination"));
        } else {
            builder.append(helpClauses.get(title));
        }
        this.helpClause = builder.toString();
        return "";
    }

    private String getProfileClause() {
        logger.finest("Clause for the profile page gets fetched.");
        StringBuilder clause = new StringBuilder();
        clause.append(helpClauses.get("/view/user/profile.xhtml"));
        if (userSession.isAdmin()) {
            clause.append(helpClauses.get("/view/user/profile.xhtml/admin"));
        }
        return clause.toString();
    }


    private String getAdClause() {
        logger.finest("Clause for the ad page gets fetched.");
        StringBuilder clause = new StringBuilder();
        if (userSession.isLoggedIn()) {
            clause.append(helpClauses.get("adBasic"));
            if (userSession.isAdmin()) {
                clause.append(helpClauses.get("/view/public/ad.xhtml/admin"));
            } else {
                clause.append(helpClauses.get("/view/public/ad.xhtml/user"));
            }
        } else {
            clause.append(helpClauses.get("/view/public/ad.xhtml"));
        }
        return clause.toString();
    }

    private String getEditAdClause() {
        logger.finest("Clause for the edit ad page gets fetched.");
        StringBuilder clause = new StringBuilder();
        clause.append(helpClauses.get("/view/user/editAd.xhtml/basic"));
        if (adBean.isNewAd()) {
            clause.append(helpClauses.get("/view/user/editAd.xhtml/newAd"));
        } else {
            clause.append(helpClauses.get("/view/user/editAd.xhtml"));
        }
        return clause.toString();
    }

    private String getEditCategoryClause() {
        logger.finest("Clause for the edit category page gets fetched.");
        StringBuilder clause = new StringBuilder();
        clause.append(helpClauses.get("editCategoryBasic"));
        if (editCategoryBean.isCategoryGettingEdited()) {
            clause.append(helpClauses.get("/view/admin/editCategory.xhtml"));
        } else {
            clause.append(helpClauses.get("/view/admin/editCategory.xhtml/createCategory"));
        }
        return clause.toString();
    }

    private String getHeaderClause() {
        logger.finest("Clause for the header gets fetched.");
        StringBuilder clause = new StringBuilder();
        if (userSession.isLoggedIn()) {
            clause.append(helpClauses.get("header/user"));
            if (userSession.isAdmin()) {
                clause.append(helpClauses.get("header/admin"));
            }
        } else {
            clause.append(helpClauses.get("headerNoUser"));
        }
        clause.append(helpClauses.get("headerBasic"));
        return clause.toString();
    }

    /**
     * Getter for the help clause of the current context. Dependent of role and the currently visited website.
     *
     * @return The help clause of the current context.
     */
    public String getHelpClause() {
        return helpClause;
    }

}
