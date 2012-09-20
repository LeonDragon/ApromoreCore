package org.apromore.canoniser.yawl.cpf2yawl.external;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * This test is just checking if any exceptions occur during conversion. No structural checks are done.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 * 
 */
@Ignore
@RunWith(Parameterized.class)
public class FromPNMLTests extends WholeDirectoryTest {

    public static String MODEL_DIR = TestUtils.TEST_RESOURCES_DIRECTORY + "/CPF/External/PNML";

    @Parameters
    public static Collection<Object[]> getFiles() {
        final Collection<Object[]> params = new ArrayList<Object[]>();
        for (final File f : getCPFFiles()) {
            final File anfFile = new File(f.getPath() + (f.getName().replace(".cpf", "")) + ".anf");
            if (anfFile.exists()) {
                final Object[] arr = new Object[] { f, anfFile };
                params.add(arr);
            } else {
                final Object[] arr = new Object[] { f, null };
                params.add(arr);
            }
        }
        return params;

    }

    private static File[] getCPFFiles() {
        return new File(MODEL_DIR).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".cpf");
            }
        });
    }

    public FromPNMLTests(final File testCPFFile, final File testANFFile) {
        super(testCPFFile, null);
    }

}