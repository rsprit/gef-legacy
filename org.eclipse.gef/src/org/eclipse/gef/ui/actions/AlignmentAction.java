/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.ui.actions;

import java.util.*;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.internal.InternalImages;
import org.eclipse.gef.requests.AlignmentRequest;
import org.eclipse.gef.tools.ToolUtilities;

final public class AlignmentAction extends SelectionAction {

public static final String ID_ALIGN_LEFT   = GEFActionConstants.ALIGN_LEFT;
public static final String ID_ALIGN_RIGHT  = GEFActionConstants.ALIGN_RIGHT;
public static final String ID_ALIGN_TOP    = GEFActionConstants.ALIGN_TOP;
public static final String ID_ALIGN_BOTTOM = GEFActionConstants.ALIGN_BOTTOM;
public static final String ID_ALIGN_CENTER = GEFActionConstants.ALIGN_CENTER;
public static final String ID_ALIGN_MIDDLE = GEFActionConstants.ALIGN_MIDDLE;

private List operationSet;
private int alignment;

/**
 * @deprecated use AlignmentAction(IWorkbenchPart, int align)
 * @param editor the editor
 * @param align the alignment ID
 */
public AlignmentAction(IEditorPart editor, int align) {
	this((IWorkbenchPart)editor, align);
}

/**
 * Constructs an AlignmentAction with the given part and alignment ID.  The alignment ID
 * must by one of:
 * <UL>
 *   <LI>GEFActionConstants.ALIGN_LEFT
 *   <LI>GEFActionConstants.ALIGN_RIGHT
 *   <LI>GEFActionConstants.ALIGN_CENTER
 *   <LI>GEFActionConstants.ALIGN_TOP
 *   <LI>GEFActionConstants.ALIGN_BOTTOM
 *   <LI>GEFActionConstants.ALIGN_MIDDLE
 * </UL>  
 * @param part the workbench part used to obtain context
 * @param align the aligment ID.
 */
public AlignmentAction(IWorkbenchPart part, int align) {
	super(part);
	alignment = align;
	initUI();
}

protected boolean calculateEnabled() {
	operationSet = null;
	Command cmd = createAlignmentCommand();
	if (cmd == null)
		return false;
	return cmd.canExecute();
}

protected Rectangle calculateAlignmentRectangle(Request request) {
	List editparts = getOperationSet(request);
	if (editparts == null || editparts.isEmpty())
		return null;
	GraphicalEditPart part = (GraphicalEditPart)editparts.get(editparts.size()-1);
	Rectangle rect = new PrecisionRectangle(part.getFigure().getBounds());
	part.getFigure().translateToAbsolute(rect);
	return rect;
}

private Command createAlignmentCommand() {
	AlignmentRequest request = new AlignmentRequest(RequestConstants.REQ_ALIGN);
	request.setAlignmentRectangle(calculateAlignmentRectangle(request));
	request.setAlignment(alignment);
	List editparts = getOperationSet(request);
	if (editparts.size() < 2)
		return null;

	CompoundCommand command = new CompoundCommand();
	command.setDebugLabel(getText());
	for (int i=0; i<editparts.size(); i++) {
		EditPart editpart = (EditPart)editparts.get(i);
		command.add(editpart.getCommand(request));
	}
	return command;
}

public void dispose() {
	operationSet = Collections.EMPTY_LIST;
	super.dispose();
}

public ImageDescriptor getHoverImageDescriptor() {
	return super.getHoverImageDescriptor();
}

protected List getOperationSet(Request request) {
	if (operationSet != null)
		return operationSet;
	List editparts = new ArrayList(getSelectedObjects());
	if (editparts.isEmpty() || !(editparts.get(0) instanceof GraphicalEditPart))
		return Collections.EMPTY_LIST;
	editparts = ToolUtilities.getSelectionWithoutDependants(editparts);
	ToolUtilities.filterEditPartsUnderstanding(editparts, request);
	if (editparts.size() < 2)
		return Collections.EMPTY_LIST;
	EditPart parent = ((EditPart)editparts.get(0)).getParent();
	for (int i=1; i<editparts.size(); i++) {
		EditPart part = (EditPart)editparts.get(i);
		if (part.getParent() != parent)
			return Collections.EMPTY_LIST;
	}
	return editparts;
}

protected void initUI() {
	switch (alignment) {
		case PositionConstants.LEFT: {
			setId(GEFActionConstants.ALIGN_LEFT);
			setText(GEFMessages.AlignLeftAction_Label);
			setToolTipText(GEFMessages.AlignLeftAction_Tooltip);
			setHoverImageDescriptor(InternalImages.DESC_HORZ_ALIGN_LEFT);
			setImageDescriptor(InternalImages.DESC_HORZ_ALIGN_LEFT_BW);
			setDisabledImageDescriptor(InternalImages.DESC_HORZ_ALIGN_LEFT_DIS);
			break;
		}
		case PositionConstants.RIGHT: {
			setId(GEFActionConstants.ALIGN_RIGHT);
			setText(GEFMessages.AlignRightAction_Label);
			setToolTipText(GEFMessages.AlignRightAction_Tooltip);
			setHoverImageDescriptor(InternalImages.DESC_HORZ_ALIGN_RIGHT);
			setImageDescriptor(InternalImages.DESC_HORZ_ALIGN_RIGHT_BW);
			setDisabledImageDescriptor(InternalImages.DESC_HORZ_ALIGN_RIGHT_DIS);
			break;
		}
		case PositionConstants.TOP: {
			setId(GEFActionConstants.ALIGN_TOP);
			setText(GEFMessages.AlignTopAction_Label);
			setToolTipText(GEFMessages.AlignTopAction_Tooltip);
			setHoverImageDescriptor(InternalImages.DESC_VERT_ALIGN_TOP);
			setImageDescriptor(InternalImages.DESC_VERT_ALIGN_TOP_BW);
			setDisabledImageDescriptor(InternalImages.DESC_VERT_ALIGN_TOP_DIS);
			break;
		}
		case PositionConstants.BOTTOM: {
			setId(GEFActionConstants.ALIGN_BOTTOM);
			setText(GEFMessages.AlignBottomAction_Label);
			setToolTipText(GEFMessages.AlignBottomAction_Tooltip);
			setHoverImageDescriptor(InternalImages.DESC_VERT_ALIGN_BOTTOM);
			setImageDescriptor(InternalImages.DESC_VERT_ALIGN_BOTTOM_BW);
			setDisabledImageDescriptor(InternalImages.DESC_VERT_ALIGN_BOTTOM_DIS);
			break;
		}
		case PositionConstants.CENTER: {
			setId(GEFActionConstants.ALIGN_CENTER);
			setText(GEFMessages.AlignCenterAction_Label);
			setToolTipText(GEFMessages.AlignCenterAction_Tooltip);
			setHoverImageDescriptor(InternalImages.DESC_HORZ_ALIGN_CENTER);
			setImageDescriptor(InternalImages.DESC_HORZ_ALIGN_CENTER_BW);
			setDisabledImageDescriptor(InternalImages.DESC_HORZ_ALIGN_CENTER_DIS);
			break;
		}
		case PositionConstants.MIDDLE: {
			setId(GEFActionConstants.ALIGN_MIDDLE);
			setText(GEFMessages.AlignMiddleAction_Label);
			setToolTipText(GEFMessages.AlignMiddleAction_Tooltip);
			setHoverImageDescriptor(InternalImages.DESC_VERT_ALIGN_MIDDLE);
			setImageDescriptor(InternalImages.DESC_VERT_ALIGN_MIDDLE_BW);
			setDisabledImageDescriptor(InternalImages.DESC_VERT_ALIGN_MIDDLE_DIS);
			break;
		}
	}
}

public void run() {
	operationSet = null;
	execute(createAlignmentCommand());
}

}
