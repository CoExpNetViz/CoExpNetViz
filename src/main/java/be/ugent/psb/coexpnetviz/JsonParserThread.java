package be.ugent.psb.coexpnetviz;

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

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Parse backend json output in a separate thread
 */
public class JsonParserThread extends Thread {
	
	private JsonNode response;
	private InputStream input;
	private IOException caughtException;
	
	public JsonParserThread(InputStream input) {
		super();
		this.input = input;
	}

	@Override
	public void run() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			response = mapper.readTree(input);
		} catch (IOException e) {
			caughtException = e;
		}
	}
	
	public JsonNode getResponse() throws IOException {
		if (caughtException != null) {
			throw caughtException;
		}
		return response;
	}

}