# AccordionPanel

AccordionPanel is a container component that displays content in stacked format.
                                  jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.AccordionPanel.html
[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.AccordionPanel.html)

## Info

| Name | Value |
| --- | --- |
| Tag | accordionPanel |
| Component Class | org.primefaces.component.accordionpanel.Accordionpanel |
| Component Type | org.primefaces.component.AccordionPanel |
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.AccordionPanelRenderer |
| Renderer Class | org.primefaces.component.accordionpanel.AccordionPanelRenderer |

## Attributes

| Name           | Default | Type       | Description     |
|----------------| ------- | ---------- | ----------------- |
| id             | null    | String     | Unique identifier of the component
| binding        | null    | Object     | An EL expression that maps to a server side UIComponent instance in a backing bean.
| value          | null    | List       | List to iterate to display dynamic number of tabs.
| var            | null    | String     | Name of iterator to use in a dynamic number of tabs.
| varStatus      | null    | String     | Name of the exported request scoped variable to represent state of the iteration same as in ui:repeat varStatus.
| activeIndex    | 0       | String     | (DEPRECATED, use active instead!) Index of the active tab or a comma separated string of indexes when multiple mode is on. Alternative: all.
| active         | 0       | String     | Index or key of the active tab or a comma separated string of indexes/keys when multiple mode is on. Alternative: all.
| cache          | true    | Boolean    | Defines if activating a dynamic tab should load the contents from server again.
| dir            | ltr     | String     | Defines text direction, valid values are ltr and rtl.
| dynamic        | false   | Boolean    | Defines the toggle mode.
| multiple       | false   | Boolean    | Controls multiple selection.
| onTabChange    | null    | String     | Client side callback to invoke when an inactive tab is clicked.
| onTabClose     | null    | String     | Client side callback to invoke when a tab is closed.
| onTabShow      | null    | String     | Client side callback to invoke when a tab gets activated.
| prependId      | true    | Boolean    | AccordionPanel is a naming container thus prepends its id to its children by default a false value turns this behavior off except for dynamic tabs.
| rendered       | true    | Boolean    | Boolean value to specify the rendering of the component.
| style          | null    | String     | Inline style of the container element.
| styleClass     | null    | String     | Style class of the container element.
| tabController  | null    | MethodExpr | Server side listener to decide whether a tab change or tab close should be allowed.
| tabindex       | 0       | String     | Position of the headers in the tabbing order.
| toggleSpeed    | 500     | Integer    | Speed of toggling in milliseconds
| scrollIntoView |null    | String     | Should the tab scroll into view. One of start, center, end, nearest, or null if disabled. Default is null
| widgetVar      | null    | String     | Name of the client side widget.
| multiViewState | false   | Boolean    | Whether to keep Panel state across views, defaults to false.

## Getting Started with Accordion Panel
Accordion panel consists of one or more tabs and each tab can group any content. Titles can also be
defined with “title” facet.

```html
<p:accordionPanel>
    <p:tab title="First Tab Title">
        <h:outputText value= "Lorem"/>
        ...More content for first tab
    </p:tab>
    <p:tab title="Second Tab Title">
        <h:outputText value="Ipsum" />
    </p:tab>
    //any number of tabs
</p:accordionPanel>
```

> :warning: **Updating a single _tab_ via AJAX is not possible. Please update the whole _accordionPanel_ or a container inside the _tab_!**

## Dynamic Content Loading
AccordionPanel supports lazy loading of tab content, when dynamic option is set true, only active
tab contents will be rendered to the client side and clicking an inactive tab header will do an ajax
request to load the tab contents.

This feature is useful to reduce bandwidth and speed up page loading time. By default activating a
previously loaded dynamic tab does not initiate a request to load the contents again as tab is cached.
To control this behavior use _cache_ option.

```html
<p:accordionPanel dynamic="true">
    //..tabs
</p:accordionPanel>
```

> :warning: **If you attach `p:ajax event="tabChange"`, please make sure you dont _update_ the AccordionPanel or a parent. Otherwise the AccordionPanel is broken after re-rendering.**


## Client Side Callbacks
_onTabChange_ is called before a tab is shown and _onTabShow_ is called after. Both receive container
element of the tab to show as the parameter.

```html
<p:accordionPanel onTabChange="handleChange(panel)">
    //..tabs
</p:accordionPanel>

<script type="text/javascript">
    function handleChange(panel) {
    //panel: new tab content container
    }
</script>
```

## Ajax Behavior Events
_tabChange_ and _tabClose_ are the ajax behavior events of accordion panel. An example with
tabChange would be;

```html
<p:accordionPanel>
 <p:ajax event="tabChange" listener="#{bean.onChange}" />
</p:accordionPanel>
```

```java
public void onChange(TabChangeEvent event) {
    //Tab activeTab = event.getTab();
    //...
}
```

Your listener(if defined) will be invoked with an _org.primefaces.event.TabChangeEvent_ instance
that contains a reference to the new active tab and the accordion panel itself. Similarly
_org.primefaces.event.TabCloseEvent_ is passed to the listener of tabClose event when an active tab is
closed.

## Dynamic Number of Tabs
When the tabs to display are not static, use the built-in iteration feature similar to ui:repeat.

```html
<p:accordionPanel value="#{bean.list}" var="listItem">
    <p:tab title="#{listItem.propertyA}">
        <h:outputText value= "#{listItem.propertyB}"/>
        ...More content
    </p:tab>
</p:accordionPanel>
```

## Disabled Tabs
A tab can be disabled by setting disabled attribute to true.

```html
<p:accordionPanel>
    <p:tab title="First Tab Title" disabled="true">
        <h:outputText value= "Lorem"/>
        ...More content for first tab
    </p:tab>
    <p:tab title="Second Tab Title">
        <h:outputText value="Ipsum" />
    </p:tab>
    //any number of tabs
</p:accordionPanel>
```

## Multiple Selection
By default, only one tab at a time can be active, enable _multiple_ mode to activate multiple tabs.

```html
<p:accordionPanel multiple="true">
    //tabs
</p:accordionPanel>
```

## TabController
`tabController` is a server side listener that can be utilized to decide if a client side tab change or tab close action is allowed.
When one of these two events occur, an AJAX call is made to invoke the `tabController`,
then the boolean return value of this method is sent back to client, to decide if the event should be performed.
An example usecase is disallowing tab change, if current tab has invalid inputs.

```html
<p:accordionPanel tabController="#{accordionView.isTabActionAllowed}">
    //tabs
</p:accordionPanel>
```
```java
@Named
@RequestScoped
public class AccordionView {
    public void isTabActionAllowed(TabEvent event) {
        if (event instanceof TabCloseEvent) {
            return false;
        }
        if (event instanceof TabChangeEvent) {
            return true;
        }
        // should not happen
        return false;
    }
}
```

## Tab Specific Popup Menu
AccordionPanel has built-in support to display fully customizable popup menu for a tab, an icon to display the menu
is placed at top-right corner of the tab header. This feature is enabled by defining a menu component and defining it
as the options facet of the tab.

```xhtml
<p:accordionPanel>
    <p:tab title="Tab With Popup Menu">
        <f:facet name="options">
            <p:menu>
                //Menuitems
            </p:menu>
        </f:facet>
    </p:tab>
</p:panel>
```
## Custom Action
If you’d like to add custom actions to a tab header, use actions facet with icon markup;

```xhtml
<p:accordionPanel>
    <p:tab title="Tab With Custom Action">
        <f:facet name="actions">
            <h:commandLink styleClass="ui-panel-titlebar-icon ui-corner-all ui-state-default">
                <h:outputText styleClass="ui-icon ui-icon-help" />
            </h:commandLink>
        </f:facet>
        //content
    </p:tab>
</p:panel>
```

## Client Side API
Widget: _PrimeFaces.widget.AccordionPanel_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| select(index) | index: Index of tab | boolean | Activates (opens) the tab with given index. This may fail by returning `false`, such as when a callback is registered that prevent the tab from being opened. |
| selectAll() | none | void | Activates (opens) all the tabs if multiple mode is enabled and the first tab in single mode.|
| unselect(index) | index: Index of tab | void | Deactivates tab with given index.|
| unselectAll() | none | void | Deactivates (closes) all the tabs.|

## Skinning
AccordionPanel resides in a main container element which _style_ and _styleClass_ options apply. As
skinning style classes are global, see the main theming section for more information. Following is
the list of structural style classes;

| Class | Element |
| --- | --- |
| .ui-accordion | Main container |
| .ui-accordion-header | Tab header |
| .ui-accordion-content | Tab content |