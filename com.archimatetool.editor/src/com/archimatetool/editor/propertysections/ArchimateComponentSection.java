/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.editor.propertysections;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.model.IArchimateComponent;
import com.archimatetool.model.IArchimatePackage;



/**
 * Property Section for an Archimate Component
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateComponentSection extends AbstractArchimatePropertySection {
    
    private static final String HELP_ID = "com.archimatetool.help.elementPropertySection"; //$NON-NLS-1$
    
    /**
     * Filter to show or reject this section depending on input value
     */
    public static class Filter extends ObjectFilter {
        @Override
        protected boolean isRequiredType(Object object) {
            return object instanceof IArchimateComponent;
        }

        @Override
        protected Class<?> getAdaptableType() {
            return IArchimateComponent.class;
        }
    }

    /*
     * Adapter to listen to changes made elsewhere (including Undo/Redo commands)
     */
    private Adapter eAdapter = new AdapterImpl() {
        @Override
        public void notifyChanged(Notification msg) {
            Object feature = msg.getFeature();
            // Element Name event (Undo/Redo and here)
            if(feature == IArchimatePackage.Literals.NAMEABLE__NAME) {
                refreshNameField();
                fPage.labelProviderChanged(null); // Update Main label
            }
            // Element Documentation event (Undo/Redo and here)
            else if(feature == IArchimatePackage.Literals.DOCUMENTABLE__DOCUMENTATION) {
                refreshDocumentationField();
            }
        }
    };
    
    private IArchimateComponent fArchimateComponent;

    private PropertySectionTextControl fTextName;
    private PropertySectionTextControl fTextDocumentation;
    
    @Override
    protected void createControls(Composite parent) {
        fTextName = createNameControl(parent, Messages.ArchimateElementSection_0);
        fTextDocumentation = createDocumentationControl(parent, Messages.ArchimateElementSection_1);

        // Help ID
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_ID);
    }

    @Override
    protected void setElement(Object element) {
        fArchimateComponent = (IArchimateComponent)new Filter().adaptObject(element);
        if(fArchimateComponent == null) {
            System.err.println(getClass() + " failed to get element for " + element); //$NON-NLS-1$
        }
        
        refreshControls();
    }
    
    protected void refreshControls() {
        refreshNameField();
        refreshDocumentationField();
    }
    
    protected void refreshNameField() {
        if(fIsExecutingCommand) {
            return; 
        }
        fTextName.refresh(fArchimateComponent);
    }
    
    protected void refreshDocumentationField() {
        if(fIsExecutingCommand) {
            return; 
        }
        fTextDocumentation.refresh(fArchimateComponent);
    }

    @Override
    protected Adapter getECoreAdapter() {
        return eAdapter;
    }
    
    @Override
    protected EObject getEObject() {
        return fArchimateComponent;
    }

    @Override
    public boolean shouldUseExtraSpace() {
        return true;
    }
}
