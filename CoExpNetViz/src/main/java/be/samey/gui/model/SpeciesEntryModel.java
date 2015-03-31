package be.samey.gui.model;

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

import be.samey.gui.model.AbstrModel;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author sam
 */
public class SpeciesEntryModel extends AbstrModel {

    private String speciesName;
    private Path speciesExprDataPath;

    public SpeciesEntryModel() {
        speciesName = "";
        speciesExprDataPath = Paths.get("");
    }

    /**
     * @return the speciesName
     */
    public String getSpeciesName() {
        return speciesName;
    }

    /**
     * @param speciesName the speciesName to set
     */
    public void setSpeciesName(String speciesName) {
        if (!this.speciesName.equals(speciesName) ){
            this.speciesName = speciesName;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * @return the speciesExprDataPath
     */
    public Path getSpeciesFilePath() {
        return speciesExprDataPath;
    }

    /**
     * @param speciesExprDataPath the speciesExprDataPath to set
     */
    public void setSpeciesExprDataPath(Path speciesExprDataPath) {
        if (this.speciesExprDataPath != speciesExprDataPath ){
            this.speciesExprDataPath = speciesExprDataPath;
            setChanged();
            notifyObservers();
        }
    }


}
