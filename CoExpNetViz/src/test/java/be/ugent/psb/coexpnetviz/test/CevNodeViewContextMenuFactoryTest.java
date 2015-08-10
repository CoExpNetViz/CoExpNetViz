package be.ugent.psb.coexpnetviz.test;

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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import be.ugent.psb.coexpnetviz.gui.CENVNodeViewContextMenuFactory;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author sam
 */
public class CevNodeViewContextMenuFactoryTest {

    public CevNodeViewContextMenuFactoryTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of parseFamilies method, of class CevNodeViewContextMenuFactory.
     */
    @Test
    public void testParseFamilies() {
        System.out.println("parseFamilies");

        CENVNodeViewContextMenuFactory instance = new CENVNodeViewContextMenuFactory(null);

        String families
            = "From PGSC - ITAG mapping: "
            + "from_itag_to_pgsc_blast14952, from_itag_to_pgsc_blast20310, "
            + "from_itag_to_pgsc_blast26786. "
            + "From Plaza Dicot: "
            + "ORTHO03D000499, ORTHO03D000564, ORTHO03D003161, ORTHO03D016271, "
            + "ORTHO03D016277, ORTHO03D018014. "
            + "From Plaza Monocot: "
            + "ORTHO03M000010, ORTHO03M001323, ORTHO03M013957";
        Map<String, List<String>> famsMap = instance.parseFamilies(families);

        List<String> expectedDicots = Arrays.asList(
            "ORTHO03D000499", "ORTHO03D000564", "ORTHO03D003161",
            "ORTHO03D016271", "ORTHO03D016277", "ORTHO03D018014");

        assertThat(famsMap.get("Plaza Dicots"), is(expectedDicots));

        List<String> expectedMonocots = Arrays.asList(
            "ORTHO03M000010", "ORTHO03M001323", "ORTHO03M013957"
        );

        assertThat(famsMap.get("Plaza Monocots"), is(expectedMonocots));
    }

}
