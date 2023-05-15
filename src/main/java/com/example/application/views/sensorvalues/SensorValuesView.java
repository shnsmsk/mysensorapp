package com.example.application.views.sensorvalues;

import com.example.application.data.entity.SensorValues;
import com.example.application.data.service.SensorValuesService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.PermitAll;
import java.time.Duration;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("SensorValues")
@Route(value = "SensorValues/:sensorValuesID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class SensorValuesView extends Div implements BeforeEnterObserver {

    private final String SENSORVALUES_ID = "sensorValuesID";
    private final String SENSORVALUES_EDIT_ROUTE_TEMPLATE = "SensorValues/%s/edit";

    private final Grid<SensorValues> grid = new Grid<>(SensorValues.class, false);

    private DateTimePicker date;
    private TextField temparature;
    private TextField humidity;
    private TextField pressure;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<SensorValues> binder;

    private SensorValues sensorValues;

    private final SensorValuesService sensorValuesService;

    public SensorValuesView(SensorValuesService sensorValuesService) {
        this.sensorValuesService = sensorValuesService;
        addClassNames("sensor-values-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("date").setAutoWidth(true);
        grid.addColumn("temparature").setAutoWidth(true);
        grid.addColumn("humidity").setAutoWidth(true);
        grid.addColumn("pressure").setAutoWidth(true);
        grid.setItems(query -> sensorValuesService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SENSORVALUES_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(SensorValuesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(SensorValues.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(temparature).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("temparature");
        binder.forField(humidity).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("humidity");
        binder.forField(pressure).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("pressure");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.sensorValues == null) {
                    this.sensorValues = new SensorValues();
                }
                binder.writeBean(this.sensorValues);
                sensorValuesService.update(this.sensorValues);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(SensorValuesView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> sensorValuesId = event.getRouteParameters().get(SENSORVALUES_ID).map(Long::parseLong);
        if (sensorValuesId.isPresent()) {
            Optional<SensorValues> sensorValuesFromBackend = sensorValuesService.get(sensorValuesId.get());
            if (sensorValuesFromBackend.isPresent()) {
                populateForm(sensorValuesFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested sensorValues was not found, ID = %s", sensorValuesId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(SensorValuesView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        date = new DateTimePicker("Date");
        date.setStep(Duration.ofSeconds(1));
        temparature = new TextField("Temparature");
        humidity = new TextField("Humidity");
        pressure = new TextField("Pressure");
        formLayout.add(date, temparature, humidity, pressure);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(SensorValues value) {
        this.sensorValues = value;
        binder.readBean(this.sensorValues);

    }
}
