package de.schwarzes_brett.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Contains the data of a category.
 *
 * @author Valentin Damjantschitsch.
 */
public class CategoryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The ID of the category.
     */
    private Integer id;

    /**
     * The name of the category.
     */
    private String name;

    /**
     * The description of the category.
     */
    private String description;

    /**
     * The parent of the category.
     */
    private int parentID;

    /**
     * The number of children the category has.
     */
    private int childCounter;

    /**
     * Constructs categoryDTO with needed values. Used for filling data into it at the data access layer and using them in backing/service layer.
     *
     * @param id           The id of the category.
     * @param name         The name of the category.
     * @param description  The description of the category.
     * @param parentID     The ID of the parent a category has.
     * @param childCounter The number of children a category has.
     */
    public CategoryDTO(int id, String name, String description, int parentID, int childCounter) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.parentID = parentID;
        this.childCounter = childCounter;
    }

    /**
     * Default constructor.
     */
    public CategoryDTO() {
    }

    /**
     * Getter for the layer id of a category.
     *
     * @return The layer id of a category.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Setter for the layerID of a category.
     *
     * @param id The id to be set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Getter for the name of a category.
     *
     * @return The name of a category.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name of a category.
     *
     * @param name The name to be set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the description of a category.
     *
     * @return The description of a category.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for the description of a category.
     *
     * @param description The description to be set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for the parent of a category.
     *
     * @return The parent of a category.
     */
    public int getParentID() {
        return parentID;
    }

    /**
     * Setter for the parent of a category.
     *
     * @param parent The parent to be set.
     */
    public void setParentID(int parent) {
        this.parentID = parent;
    }

    /**
     * Getter for the number of children a category has.
     *
     * @return The number of children a category has.
     */
    public int getChildCounter() {
        return childCounter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CategoryDTO that = (CategoryDTO) o;
        return Objects.equals(id, that.id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
