package org.apromore.service.csvimporter.impl;

import com.google.common.io.ByteStreams;
import com.opencsv.*;
import com.opencsv.enums.CSVReaderNullFieldIndicator;;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.apromore.service.csvimporter.*;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

/** Test suite for {@link CSVImporterLogicImpl}. */
public class CSVImporterLogicImplUnitTest {

    /** Expected headers for <code>test1-valid.csv</code>. */
    private List<String> TEST1_EXPECTED_HEADER = Arrays.asList("case id", "activity", "start date", "completion time", " process type");

    /** Test instance. */
    private CSVImporterLogic csvImporterLogic = new CSVImporterLogicImpl();



    // Internal methods

    private static CSVReader newCSVReader(String filename, String charset, char delimiter) {
        return new CSVReaderBuilder(new InputStreamReader(CSVImporterLogicImplUnitTest.class.getResourceAsStream(filename), Charset.forName(charset)))
                .withSkipLines(0)
                .withCSVParser((new RFC4180ParserBuilder())
                        .withSeparator(delimiter)
                        .build())
                .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
                .build();
    }

    private static String toString(XLog xlog) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        (new XesXmlSerializer()).serialize(xlog, baos);
        return baos.toString();
    }


    // Test cases

    /** Test {@link CSVImporterLogic.sampleCSV} sampling fewer lines than contained in <code>test1-valid.csv</code>. */
    @Test
    public void testSampleCSV_undersample() throws Exception {
        CSVReader csvReader = newCSVReader("/test1-valid.csv", "utf-8", ',');
        LogSample logSample = csvImporterLogic.sampleCSV(csvReader, 2);

        // Validate result
        assertEquals(TEST1_EXPECTED_HEADER, logSample.getHeader());
        assertEquals(2, logSample.getLines().size());
    }

    /** Test {@link CSVImporterLogic.sampleCSV} sampling more lines than contained in <code>test1-valid.csv</code>. */
    @Test
    public void testSampleCSV_oversample() throws Exception {
        CSVReader csvReader = newCSVReader("/test1-valid.csv", "utf-8", ',');
        LogSample logSample = csvImporterLogic.sampleCSV(csvReader, 5);

        // Validate result
        assertEquals(TEST1_EXPECTED_HEADER, logSample.getHeader());
        assertEquals(3, logSample.getLines().size());
    }

    /** Test {@link CSVImporterLogic.prepareXesModel} against a valid CSV log <code>test1-valid.csv</code>. */
    @Test
    public void testPrepareXesModel_test1_valid() throws Exception {

        System.out.println("\n************************************\ntest1 - Valid csv test ");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test1-valid.csv", "utf-8", ',');
        String expectedXES = new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test1-expected.xes")), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test1-valid.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(3, logModel.getRows().size());
        assertEquals(0, logModel.getInvalidRows().size());
        assert logModel.getInvalidRows().isEmpty();

        // Continue with the XES conversion
        XLog xlog = csvImporterLogic.createXLog(logModel.getRows());

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));


        System.out.println("test1 - Done.\n************************************\n");

    }

    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test2_missing_columns() throws Exception {

        System.out.println("\n************************************\ntest2 - Missing columns test");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test2-missing-columns.csv", "utf-8", ',');
        String expectedXES = new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test2-expected.xes")), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test2-missing-columns.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(2, logModel.getRows().size());
        assertEquals(0, logModel.getInvalidRows().size());
        assert logModel.getInvalidRows().isEmpty();

        // Continue with the XES conversion
        XLog xlog = csvImporterLogic.createXLog(logModel.getRows());

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));


        System.out.println("test2 - Done.\n************************************\n");
    }



    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test(expected = InvalidCSVException.class)
    public void testPrepareXesModel_test3_invalid_end_timestamp() throws Exception {

        System.out.println("\n************************************\ntest3 - Invalid end timestamp");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test3-invalid-end-timestamp.csv", "utf-8", ',');
        String expectedXES = new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test3-expected.xes")), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test3-invalid-end-timestamp.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(3, logModel.getRows().size());
        assertEquals(1, logModel.getInvalidRows().size());

        // Continue with the XES conversion
        XLog xlog = csvImporterLogic.createXLog(logModel.getRows());

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));

        System.out.println("test3 - Done.\n************************************\n");
    }

    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test(expected = InvalidCSVException.class)
    public void testPrepareXesModel_test4_invalid_start_timestamp() throws Exception {

        System.out.println("\n************************************\ntest4 - Invalid start timestamp");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test4-invalid-start-timestamp.csv", "utf-8", ',');
        String expectedXES = new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test4-expected.xes")), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test4-invalid-start-timestamp.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(3, logModel.getRows().size());
        assertEquals(1, logModel.getInvalidRows().size());

        // Continue with the XES conversion
        XLog xlog = csvImporterLogic.createXLog(logModel.getRows());

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));

        System.out.println("test4 - Done.\n************************************\n");
    }


    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test5_empty_caseID() throws Exception {

        System.out.println("\n************************************\ntest5 - Empty caseID");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test5-empty-caseID.csv", "utf-8", ',');
        String expectedXES = new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test5-expected.xes")), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test5-empty-caseID.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(3, logModel.getRows().size());
        assertEquals(0, logModel.getInvalidRows().size());

        // Continue with the XES conversion
        XLog xlog = csvImporterLogic.createXLog(logModel.getRows());

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));

        System.out.println("test5 - Done.\n************************************\n");
    }

    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test6_different_delimiters() throws Exception {

        System.out.println("\n************************************\ntest6 - different delimiters");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test6-different-delimiters.csv", "utf-8", ';');
        String expectedXES = new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test6-expected.xes")), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test6-different-delimiters.csv", "utf-8", ';');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(3, logModel.getRows().size());
        assertEquals(0, logModel.getInvalidRows().size());

        // Continue with the XES conversion
        XLog xlog = csvImporterLogic.createXLog(logModel.getRows());

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));

        System.out.println("test6 - Done.\n************************************\n");
    }

}
