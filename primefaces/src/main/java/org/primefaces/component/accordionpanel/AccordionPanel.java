/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.accordionpanel;

import org.primefaces.PrimeFaces;
import org.primefaces.component.tabview.Tab;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;
import org.primefaces.event.TabEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;

import java.util.Collection;
import java.util.Map;

import jakarta.el.ELContext;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.BehaviorEvent;
import jakarta.faces.event.FacesEvent;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class AccordionPanel extends AccordionPanelBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.AccordionPanel";

    public static final String CONTAINER_CLASS = "ui-accordion ui-widget ui-helper-reset ui-hidden-container";
    public static final String ACTIVE_TAB_HEADER_CLASS = "ui-accordion-header ui-helper-reset ui-state-default ui-state-active";
    public static final String TAB_HEADER_CLASS = "ui-accordion-header ui-helper-reset ui-state-default";
    public static final String TAB_HEADER_ICON_CLASS = "ui-icon ui-icon-triangle-1-e";
    public static final String TAB_HEADER_ICON_RTL_CLASS = "ui-icon ui-icon-triangle-1-w";
    public static final String ACTIVE_TAB_HEADER_ICON_CLASS = "ui-icon ui-icon-triangle-1-s";
    public static final String ACTIVE_TAB_CONTENT_CLASS = "ui-accordion-content ui-helper-reset ui-widget-content";
    public static final String INACTIVE_TAB_CONTENT_CLASS = "ui-accordion-content ui-helper-reset ui-widget-content ui-helper-hidden";

    private static final String DEFAULT_EVENT = "tabChange";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("tabChange", TabChangeEvent.class)
            .put("tabClose", TabCloseEvent.class)
            .build();

    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }

    public boolean isContentLoadRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_contentLoad");
    }

    public Tab findTab(String tabClientId) {
        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child.getClientId().equals(tabClientId)) {
                return (Tab) child;
            }
        }

        return null;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = event.getFacesContext();

        if (ComponentUtils.isRequestSource(this, context) && event instanceof AjaxBehaviorEvent) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(context);
            boolean repeating = isRepeating();
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            String tabClientId = params.get(clientId + "_currentTab");
            int tabindex = Integer.parseInt(params.get(clientId + "_tabindex"));

            if (repeating) {
                setIndex(tabindex);
            }

            Object data = repeating ? getIndexData() : null;
            Tab tab = repeating ? getDynamicTab() : findTab(tabClientId);

            TabEvent<?> changeEvent;
            if ("tabChange".equals(eventName)) {
                changeEvent = new TabChangeEvent<>(this, behaviorEvent.getBehavior(), tab, data, eventName, tabindex);
            }
            else if ("tabClose".equals(eventName)) {
                changeEvent  = new TabCloseEvent<>(this, behaviorEvent.getBehavior(), tab, data, eventName, tabindex);
            }
            else {
                throw new FacesException("Unsupported event: " + eventName);
            }

            changeEvent.setPhaseId(behaviorEvent.getPhaseId());

            super.queueEvent(changeEvent);

            if (repeating) {
                setIndex(-1);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        super.processUpdates(context);

        ELContext elContext = getFacesContext().getELContext();
        ValueExpression activeExpr = ValueExpressionAnalyzer.getExpression(elContext,
                getValueExpression(PropertyKeys.active.toString()), true);

        // Fallback to deprecated activeIndex
        if (activeExpr == null) {
            activeExpr = ValueExpressionAnalyzer.getExpression(elContext,
                    getValueExpression(PropertyKeys.activeIndex.toString()), true);
        }

        if (activeExpr != null && !activeExpr.isReadOnly(elContext)) {
            activeExpr.setValue(elContext, getActive());
            resetActive();
        }
    }

    protected void resetActive() {
        getStateHelper().remove(PropertyKeys.active);
        getStateHelper().remove(PropertyKeys.activeIndex);
    }

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);

        if (event instanceof TabEvent) {
            MethodExpression me = getTabController();
            if (me != null) {
                boolean retVal = (Boolean) me.invoke(getFacesContext().getELContext(), new Object[]{event});
                PrimeFaces.current().ajax().addCallbackParam("access", retVal);
            }
        }
    }

    @Override
    public void restoreMultiViewState() {
        AccordionState as = getMultiViewState(false);
        if (as != null) {
            setActive(as.getActive());
        }
    }

    @Override
    public AccordionState getMultiViewState(boolean create) {
        FacesContext fc = getFacesContext();
        String viewId = fc.getViewRoot().getViewId();

        return PrimeFaces.current().multiViewState()
                .get(viewId, getClientId(fc), create, AccordionState::new);
    }

    @Override
    public void resetMultiViewState() {
        setActive(null);
    }
}
