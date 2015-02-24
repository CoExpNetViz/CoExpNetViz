/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.samey.model;

/**
 *
 * @author sam
 */
public class Model {

    public Model() {
        services = new Services();
        guiStatus = new GuiStatus();
        coreStatus = new CoreStatus();
    }

    //properties of the app that do not change during execution
    public static final String APP_NAME = "CoExpNetViz";
    public static final String[] SPECIES_CHOICES = {"Tomato", "Arabidopsis", "Patato-ITAG", "Patato-PGSC"};

    //all services are kept in this object
    private Services services;

    public void setServices(Services services) {
        this.services = services;
    }

    public Services getServices() {
        return services;
    }

    //all gui interaction with the user are kept here
    private GuiStatus guiStatus;

    public void setGuiStatus(GuiStatus guistatus) {
        this.guiStatus = guistatus;
    }

    public GuiStatus getGuiStatus() {
        return guiStatus;
    }

    //information about the current status for the application it kept here
    private CoreStatus coreStatus;

    public void setCoreStatus(CoreStatus coreStatus) {
        this.coreStatus = coreStatus;
    }

    public CoreStatus getCoreStatus() {
        return coreStatus;
    }

}
