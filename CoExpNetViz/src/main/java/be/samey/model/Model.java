package be.samey.model;

/*
 * #%L
 * CoExpNetViz
 * %%
 * Copyright (C) 2015 PSB/UGent
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

/**
 *
 * @author sam
 */
public class Model {

    public Model() {
        services = new Services();
        guiStatus = new GuiStatus();
        coreStatus = new CoreStatus(this);
    }

    //properties of the app that do not change during execution
    public static final String APP_NAME = "CoExpNetViz";
    public static final String[] SPECIES_CHOICES = {"Tomato", "Arabidopsis", "Patato-ITAG", "Patato-PGSC"};
    public static final int MAX_SPECIES_COUNT = 5;
    public static final String URL = "http://bioinformatics.psb.ugent.be/webtools/morph/coexpr/run.php";

    //all services are kept in this object
    private final Services services;

//    public void setServices(Services services) {
//        this.services = services;
//    }

    public Services getServices() {
        return services;
    }

    //all gui interaction with the user are kept here
    private final GuiStatus guiStatus;

//    public void setGuiStatus(GuiStatus guistatus) {
//        this.guiStatus = guistatus;
//    }

    public GuiStatus getGuiStatus() {
        return guiStatus;
    }

    //information about the current status for the application it kept here
    private final CoreStatus coreStatus;

//    public void setCoreStatus(CoreStatus coreStatus) {
//        this.coreStatus = coreStatus;
//    }

    public CoreStatus getCoreStatus() {
        return coreStatus;
    }

}
