/*
 * #%L
 * CoExpNetViz
 * %%
 * Copyright (C) 2015 - 2021 PSB/UGent
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

package be.ugent.psb.coexpnetviz;

import org.cytoscape.property.AbstractConfigDirPropsReader;
import org.cytoscape.property.CyProperty;

/* A properties reader which first reads them from the jar and then from
 * the Cytoscape config dir. Properties are saved to the config dir.
 * 
 * http://wikiold.cytoscape.org/Cytoscape_3/AppDeveloper/Cytoscape_3_App_Cookbook#How_to_use_CyProperty_to_save_values_across_sessions.3F
 */
public class PropsReader extends AbstractConfigDirPropsReader {

	/* Name of the CyProperty
	 */
	public static final String PROPERTY_NAME = Context.NAMESPACE;
	
	/* File name of the properties file in the jar (see our resources dir)
	 * to get defaults from.
	 * 
	 * Cytoscape is hardcoded to use ".props". When using ".properties" it
	 * manages to load the defaults but because it saves user values to ".props",
	 * it will never load the user values when starting Cytoscape.  
	 */
	private static final String FILE_NAME = Context.NAMESPACE + ".props";
	
	public PropsReader() {
		super(PROPERTY_NAME, FILE_NAME, CyProperty.SavePolicy.CONFIG_DIR);
	}

}
