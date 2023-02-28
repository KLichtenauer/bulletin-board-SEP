package de.schwarzes_brett.dto;

/**
 * Restriction for the length of user input text. The constants are equal with the used limits in the database, so if a constant changes
 * problems can occur in the data access layer, because the database has to be modified manually.
 *
 * @author Valentin Damjantschitsch.
 */
public final class Limits {

    /**
     * Max length of a number text.
     */
    public static final int EXTRA_SHORT_TEXT_LENGTH = 20;

    /**
     * Max length of an extra short plus text.
     */
    public static final int EXTRA_SHORT_PLUS_TEXT_LENGTH = 30;

    /**
     * Max length of a short plus text.
     */
    public static final int SHORT_PLUS_TEXT_LENGTH = 168;

    /**
     * Max length of a short text.
     */
    public static final int SHORT_TEXT_MAX_LENGTH = 127;

    /**
     * Max length of a medium text.
     */
    public static final int MEDIUM_TEXT_MAX_LENGTH = 255;

    /**
     * Max length of a long text.
     */
    public static final int LONG_TEXT_MAX_LENGTH = 511;

    /**
     * Max length of a huge text.
     */
    public static final int HUGE_TEXT_MAX_LENGTH = 2047;

    /**
     * Max amount of images uploaded to one ad.
     */
    public static final int AD_MAX_IMAGE_COUNT = 5;

    /**
     * Max storage size of an uploaded image (unit: byte).
     */
    public static final int IMAGE_MAX_SIZE = 1_000_000;

    /**
     * Minimum password length.
     */
    public static final int MIN_PASSWORD_LENGTH = 6;

    /**
     * Max number of rating. 5 is best, 1 is lowest.
     */
    public static final int MAX_RATING = 5;

    /**
     * The number of items per page in the pagination.
     */
    public static final int ITEMS_PER_PAGE = 20;


    private Limits() {
    }

}
