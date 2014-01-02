/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package uk.ac.bolton.archimate.editor.diagram.editparts.business;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;

import uk.ac.bolton.archimate.editor.diagram.editparts.AbstractArchimateEditableTextFlowEditPart;
import uk.ac.bolton.archimate.editor.diagram.editparts.RoundedRectangleAnchor;
import uk.ac.bolton.archimate.editor.diagram.figures.IRoundedRectangleFigure;
import uk.ac.bolton.archimate.editor.diagram.figures.business.BusinessFunctionFigure;

/**
 * Business Function Edit Part
 * 
 * @author Phillip Beauvoir
 */
public class BusinessFunctionEditPart
extends AbstractArchimateEditableTextFlowEditPart {            
    
    @Override
    protected IFigure createFigure() {
        return new BusinessFunctionFigure(getModel());
    }
 
    @Override
    protected ConnectionAnchor getDefaultConnectionAnchor() {
        IRoundedRectangleFigure figureDelegate = (IRoundedRectangleFigure)((BusinessFunctionFigure)getFigure()).getFigureDelegate();
        return new RoundedRectangleAnchor(getFigure(), figureDelegate.getArc());
    }

}