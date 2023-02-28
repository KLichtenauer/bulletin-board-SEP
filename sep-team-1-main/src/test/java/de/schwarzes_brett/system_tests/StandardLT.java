package de.schwarzes_brett.system_tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * The standard load test suit.
 *
 * @author Tim-Florian Feulner
 */
@Suite
@SelectClasses({ExampleST.class, RegistrationST.class, AbonnementST.class, SearchST.class, LoginLogoutST.class, AdminRightsST.class, MessagesST.class,
        UserAdministrationST.class, EditAdST.class, RatingST.class, SecurityST.class, ProfileST.class, CategoriesST.class})
class StandardLT {}
