package be.ugent.psb.coexpnetviz.gui.model;

import java.util.ArrayList;

import be.ugent.psb.util.Objects;
import be.ugent.psb.util.mvc.model.DefaultValueModel;
import be.ugent.psb.util.mvc.model.ValueModel;

/**
 * State of a job input form
 */
public class JobInputModel implements Cloneable { // TODO rename -> JobInput

    private ValueModel<BaitGroupSource> baitGroupSource;
    private ValueModel<String> baitGroupText;
    private ValueModel<String> baitGroupFile;
    private ArrayList<String> expressionMatrices;
    private ValueModel<GeneFamiliesSource> geneFamiliesSource;
    private ValueModel<String> geneFamiliesFile;
    private ValueModel<Integer> lowerPercentile;
    private ValueModel<Integer> upperPercentile;
    private ValueModel<Boolean> saveOutput;
    private ValueModel<String> outputDirectory;

    public JobInputModel() {
        setBaitGroupSource(new DefaultValueModel<>(BaitGroupSource.FILE));
        baitGroupText = new DefaultValueModel<>("");
        baitGroupFile = new DefaultValueModel<>("");
        expressionMatrices = new ArrayList<String>();
        geneFamiliesSource = new DefaultValueModel<>(GeneFamiliesSource.PLAZA);
        geneFamiliesFile = new DefaultValueModel<>("");
        lowerPercentile = new DefaultValueModel<>(5);
        upperPercentile = new DefaultValueModel<>(95);
        saveOutput = new DefaultValueModel<>(false);
        outputDirectory = new DefaultValueModel<>(System.getProperty("user.home"));
    }

    public Object clone() throws CloneNotSupportedException {
    	// TODO clone all models
    	JobInputModel clone_ = Objects.clone(this);
    	clone_.expressionMatrices = Objects.clone(expressionMatrices);
    	return clone_;
    }

    // TODO old
//    public void setSpeciesEntry(SpeciesEntryModel sem, SpeciesEntryPanel se) {
//        species.put(sem, se);
//        setChanged();
//        notifyObservers();
//    }
//
//    public void addSpecies(SpeciesEntryModel sem, SpeciesEntryPanel se) {
//        if (!species.containsKey(sem)) {
//            species.put(sem, se);
//            setChanged();
//            notifyObservers();
//        }
//    }
//
//    public void removeSpecies(SpeciesEntryModel sem) {
//        if (species.containsKey(sem)) {
//            species.remove(sem);
//            setChanged();
//            notifyObservers();
//        }
//    }
    
	public ValueModel<BaitGroupSource> getBaitGroupSource() {
		return baitGroupSource;
	}

	public ValueModel<String> getBaitGroupText() {
		return baitGroupText;
	}

	public ValueModel<String> getBaitGroupFile() {
		return baitGroupFile;
	}

	public ArrayList<String> getExpressionMatrices() {
		return expressionMatrices;
	}

	public ValueModel<GeneFamiliesSource> getGeneFamiliesSource() {
		return geneFamiliesSource;
	}

	public ValueModel<String> getGeneFamiliesFile() {
		return geneFamiliesFile;
	}

	public ValueModel<Integer> getLowerPercentile() {
		return lowerPercentile;
	}

	public ValueModel<Integer> getUpperPercentile() {
		return upperPercentile;
	}

	public ValueModel<Boolean> getSaveOutput() {
		return saveOutput;
	}

	public ValueModel<String> getOutputDirectory() {
		return outputDirectory;
	}

	public enum BaitGroupSource {
    	FILE, TEXT
    }
    
    public enum GeneFamiliesSource {
    	PLAZA, CUSTOM, NONE
    }

}
