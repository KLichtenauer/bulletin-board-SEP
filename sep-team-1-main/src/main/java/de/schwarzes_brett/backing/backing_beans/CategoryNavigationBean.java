package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.business_logic.services.CategoryService;
import de.schwarzes_brett.dto.CategoryDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Backing Bean for the category navigation. Where a user gets certain ads when clicked on the related category.
 *
 * @author Kilian Lichtenauer
 */
@Named
@ViewScoped
public class CategoryNavigationBean implements NotificationDisplay, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int ID_OF_EMPTY_CATEGORY = -1;

    private static final int ID_OF_ROOT_CATEGORY = 0;

    private static final int INDEX_FOR_BREAD_CRUMB_ORDER = 1;

    /**
     * Includes the categories which got loaded by the user by expanding other nodes and default nodes.
     */
    private final List<TreeNode<CategoryDTO>> nodeList = new ArrayList<>();

    /**
     * Includes categories which are currently showed at the breadcrumb navigation. Gets set if a category at the tree or at the breadcrumb gets
     * clicked.
     */
    private final List<TreeNode<CategoryDTO>> breadCrumbList = new ArrayList<>();

    /**
     * The category service.
     */
    @Inject
    private CategoryService categoryService;

    /**
     * The current category path.
     */
    private List<CategoryDTO> currentPath;

    /**
     * The root category.
     */
    private TreeNode<CategoryDTO> root;

    /**
     * Saves the currently clicked category for breadcrumb navigation.
     */
    private TreeNode<CategoryDTO> currentlyClickedCategory;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public CategoryNavigationBean() {
    }

    /**
     * Getter for the currently used breadcrumb.
     *
     * @return The list of nodes which are currently needed for breadcrumb navigation.
     */
    public List<TreeNode<CategoryDTO>> getBreadCrumbList() {
        return breadCrumbList;
    }

    /**
     * Shows path of chosen categories.
     *
     * @return The list of chosen categories at the category tree.
     */
    public List<CategoryDTO> getCurrentPath() {
        return currentPath;
    }

    /**
     * Sets current path of chosen categories.
     *
     * @param currentPath The path of chosen categories to be set.
     */
    public void setCurrentPath(List<CategoryDTO> currentPath) {
        this.currentPath = currentPath;
    }

    /**
     * Getter for the root node so that primefaces can generate the tree based on the root and appending nodes.
     *
     * @return The root node.
     */
    public TreeNode<CategoryDTO> getRoot() {
        return root;
    }

    /**
     * Sets the root category of the category tree.
     *
     * @param root The new root to be set.
     */
    public void setRoot(TreeNode<CategoryDTO> root) {
        this.root = root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {

    }

    /**
     * Initializes the bean.
     */
    @PostConstruct
    public void init() {
        logger.log(Level.FINEST, "Initialization of the navigation tree.");
        this.root = createRootNode();
        nodeList.add(root);
        currentlyClickedCategory = root;
        setTreeNodesWithParent(categoryService.fetchRootCategories(), root);
        breadCrumbList.add(root);
    }

    private DefaultTreeNode<CategoryDTO> createRootNode() {
        CategoryDTO rootDTO = new CategoryDTO(ID_OF_ROOT_CATEGORY, "Alle Anzeigen", "Diese Kategorie beinhaltet alle" + " Anzeigen", 0, 0);
        return new DefaultTreeNode<>(rootDTO, null);
    }

    private void setTreeNodesWithParent(List<CategoryDTO> list, TreeNode<CategoryDTO> parentNode) {
        for (CategoryDTO currentDTO : list) {
            DefaultTreeNode<CategoryDTO> newCategoryNode = new DefaultTreeNode<>(currentDTO, parentNode);
            nodeList.add(newCategoryNode);
            if (currentDTO.getChildCounter() > 0) {
                new DefaultTreeNode<>(getEmptyCategoryDto(), newCategoryNode);
            }
        }
    }

    private TreeNode<CategoryDTO> findTreeNode(List<TreeNode<CategoryDTO>> treeNodeList, CategoryDTO category) {
        if (category == null) {
            logger.log(Level.FINEST, "Treenode could not be found because searched category is null.");
            return null;
        } else {
            logger.log(Level.FINEST, "Treenode was found.");
            return treeNodeList.stream().filter(treeNode -> category.equals(treeNode.getData())).findFirst().orElse(null);
        }
    }

    /**
     * Updates the list for the breadcrumb.
     *
     * @param currentlyClickedCategory The category that was clicked.
     */
    public void setBreadCrumb(CategoryDTO currentlyClickedCategory) {
        this.currentlyClickedCategory = findTreeNode(nodeList, currentlyClickedCategory);
        breadCrumbList.clear();
        breadCrumbList.add(root);

        while (this.currentlyClickedCategory != root) {
            breadCrumbList.add(INDEX_FOR_BREAD_CRUMB_ORDER, this.currentlyClickedCategory);
            this.currentlyClickedCategory = this.currentlyClickedCategory.getParent();
        }
        logger.log(Level.FINEST, "Breadcrumb got set.");
    }

    /**
     * Gets called when a node got expanded. Fetches children of the node dynamically and ads them to the category tree accordingly.
     *
     * @param expandedCategory The node which got expanded.
     */
    public void onNodeExpand(CategoryDTO expandedCategory) {
        logger.log(Level.FINE, expandedCategory.getName() + "-Category got expanded.");
        List<CategoryDTO> childCategories = categoryService.fetchSubCategories(expandedCategory);
        removeEmptyNodes(expandedCategory);
        TreeNode<CategoryDTO> parentNode = findTreeNode(nodeList, expandedCategory);
        setTreeNodesWithParent(childCategories, parentNode);
    }


    private CategoryDTO getEmptyCategoryDto() {
        return new CategoryDTO(ID_OF_EMPTY_CATEGORY, "", "", ID_OF_EMPTY_CATEGORY, 0);
    }

    private void removeEmptyNodes(CategoryDTO parentCategory) {
        TreeNode<CategoryDTO> parentNode = findTreeNode(nodeList, parentCategory);
        TreeNode<CategoryDTO> emptyNode = findTreeNode(parentNode.getChildren(), getEmptyCategoryDto());
        if (emptyNode != null) {
            parentNode.getChildren().remove(emptyNode);
        }
        logger.log(Level.FINEST, "Empty nodes of category tree got removed.");
    }


}
