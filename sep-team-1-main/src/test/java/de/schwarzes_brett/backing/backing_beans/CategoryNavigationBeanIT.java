package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.dto.CategoryDTO;
import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerClassExtension;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.model.TreeNode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author Kilian Lichtenauer
 */
@EnableAutoWeld
@ActivateScopes({RequestScoped.class, ApplicationScoped.class, ViewScoped.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ITPerClassExtension.class)
@ExtendWith(MockitoExtension.class)
class CategoryNavigationBeanIT extends ITBase {

    private static final int ID_OF_NODE_TO_EXPAND = 1;
    private static final int ID_OF_NODE_TO_BE_CHILDREN_1 = 4;
    private static final int ID_OF_NODE_TO_BE_CHILDREN_2 = 5;
    @Inject
    private CategoryNavigationBean categoryBean;
    /**
     * Faces context.
     */
    @Produces
    @Default
    @Mock(serializable = true)
    private FacesContext facesContext;

    @Produces
    @Default
    @Mock(serializable = true)
    private ExternalContext externalContext;

    @Test
    void onNodeExpand() {
        CategoryDTO categoryDTO = new CategoryDTO(1, "Autos", "", 0, 2);
        categoryBean.onNodeExpand(categoryDTO);
        TreeNode<CategoryDTO> autosNode = null;
        List<TreeNode<CategoryDTO>> childrenList = categoryBean.getRoot().getChildren();
        for (TreeNode<CategoryDTO> categoryDTOTreeNode : childrenList) {
            if (categoryDTOTreeNode.getData().getId() == ID_OF_NODE_TO_EXPAND) {
                autosNode = categoryDTOTreeNode;
            }
        }
        boolean containsRightChildren = autosNode.getChildren().get(0).getData().getId() == ID_OF_NODE_TO_BE_CHILDREN_1
                                        && autosNode.getChildren().get(1).getData()
                                                    .getId() == ID_OF_NODE_TO_BE_CHILDREN_2;
        assertTrue(containsRightChildren);
    }
}
