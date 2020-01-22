package org.apromore.service.csvimporter.impl;

import com.google.common.io.ByteStreams;
import com.opencsv.*;
import com.opencsv.enums.CSVReaderNullFieldIndicator;;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import org.apromore.service.csvimporter.*;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/** Test suite for {@link CSVImporterLogicImpl}. */
public class CSVImporterLogicImplUnitTest {

    /** Expected headers for <code>test1-valid.csv</code>. */
    private List<String> TEST1_EXPECTED_HEADER = Arrays.asList("case id", "activity", "start date", "completion time", " process type");

    /** Test instance. */
    private CSVImporterLogic csvImporterLogic = new CSVImporterLogicImpl();

    private static final double MAX_ERROR_FRACTION = 0.2;

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
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample, MAX_ERROR_FRACTION);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(3, logModel.getRows().size());
        assertEquals(0, logModel.getErrorCount());
        assert logModel.getInvalidRows().isEmpty();

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));



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
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample, MAX_ERROR_FRACTION);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(2, logModel.getRows().size());
        assertEquals(0, logModel.getErrorCount());
        assert logModel.getInvalidRows().isEmpty();

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));


    }



    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test3_invalid_end_timestamp() throws Exception {

        System.out.println("\n************************************\ntest3 - Invalid end timestamp");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test3-invalid-end-timestamp.csv", "utf-8", ',');
        String expectedXES = new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test3-expected.xes")), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test3-invalid-end-timestamp.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample, MAX_ERROR_FRACTION);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(3, logModel.getRows().size());
        assertEquals(0, logModel.getErrorCount());
        assertEquals(1, logModel.getInvalidRows().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));

    }

    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test4_invalid_start_timestamp() throws Exception {

        System.out.println("\n************************************\ntest4 - Invalid start timestamp");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test4-invalid-start-timestamp.csv", "utf-8", ',');
        String expectedXES = new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test4-expected.xes")), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test4-invalid-start-timestamp.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample,MAX_ERROR_FRACTION);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(3, logModel.getRows().size());
        assertEquals(0, logModel.getErrorCount());
        assertEquals(1, logModel.getInvalidRows().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));

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
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample, MAX_ERROR_FRACTION);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(3, logModel.getRows().size());
        assertEquals(0, logModel.getErrorCount());
        assertEquals(0, logModel.getInvalidRows().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));

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
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample, MAX_ERROR_FRACTION);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(3, logModel.getRows().size());
        assertEquals(0, logModel.getErrorCount());
        assertEquals(0, logModel.getInvalidRows().size());

        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));

    }


    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test7_record_invalid() throws Exception {

        System.out.println("\n************************************\ntest7 - Record invalid");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test7-record-invalid.csv", "utf-8", ',');
        String expectedXES = new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test7-expected.xes")), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test7-record-invalid.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample, MAX_ERROR_FRACTION);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(2, logModel.getRows().size());
        assertEquals(1, logModel.getErrorCount());
        assertEquals(2, logModel.getInvalidRows().size());


//        System.out.println("Error is: " + logModel.getInvalidRows().get(0));

        assertEquals("Row: 3, Warning: Start time stamp field is invalid. Copying end timestamp field into start timestamp",
                logModel.getInvalidRows().get(1));

        assertEquals("Row: 1, Error: number of columns does not match number of headers. Number of headers: 5, Number of columns: 7.\n",
                logModel.getInvalidRows().get(0));
        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));

    }


    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test(expected = InvalidCSVException.class)
    public void testPrepareXesModel_test8_all_invalid() throws Exception {

        System.out.println("\n************************************\ntest8 - All invalid");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test8-all-invalid.csv", "utf-8", ',');
//        String expectedXES = new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test7-expected.xes")), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test8-all-invalid.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample, MAX_ERROR_FRACTION);

        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(3, logModel.getRows().size());
        assertEquals(3, logModel.getErrorCount());
        assertEquals(3, logModel.getInvalidRows().size());

    }


    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test9_differentiate_dates() throws Exception {

        System.out.println("\n************************************\ntest9 - Differentiate dates");
        ArrayList<String> dateFormats = new ArrayList();
        String expectedFormat = null;
        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test9-differentiate-dates.csv", "utf-8", ',');
//        String expectedXES = new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test7-expected.xes")), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test9-differentiate-dates.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample, MAX_ERROR_FRACTION);

        assertNotNull(logModel);

        for(int i =0; i < logModel.getRows().size(); i++) {
            dateFormats.add(logModel.getRows().get(i).getStartTimestamp().toString());
        }
        expectedFormat = Parse.determineFormatForArray(dateFormats, 1);

        assertEquals("yyyy-dd-MM HH:mm:ss.SSS", expectedFormat);
        assertEquals(13, logModel.getLineCount());
        assertEquals(13, logModel.getRows().size());


    }


    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test10_detect_name() throws Exception {

        System.out.println("\n************************************\ntest10 - Detect name");

        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test10-detect-name.csv", "utf-8", ',');
        String expectedXES = new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test10-expected.xes")), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test10-detect-name.csv", "utf-8", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample, MAX_ERROR_FRACTION);

        // Validate result
        assertNotNull(logModel);
        assertEquals(3, logModel.getLineCount());
        assertEquals(3, logModel.getRows().size());
        assertEquals(0, logModel.getErrorCount());
        assertEquals(0, logModel.getInvalidRows().size());
        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);
        assertEquals(expectedXES, toString(xlog));


    }

    /** Test {@link CSVImporterLogic.prepareXesModel} against an invalid CSV log <code>test2-missing-columns.csv</code>. */
    @Test
    public void testPrepareXesModel_test11_encoding() throws Exception {

        System.out.println("\n************************************\ntest11 - Encoding");


        // Set up inputs and expected outputs
        CSVReader csvReader = newCSVReader("/test11-encoding.csv", "windows-1255", ',');
        String expectedXES = new String(ByteStreams.toByteArray(CSVImporterLogicImplUnitTest.class.getResourceAsStream("/test11-expected.xes")), Charset.forName("utf-8"));

        // Perform the test
        LogSample sample = csvImporterLogic.sampleCSV(csvReader, 100);
        csvReader = newCSVReader("/test11-encoding.csv", "windows-1255", ',');
        LogModel logModel = csvImporterLogic.prepareXesModel(csvReader, sample, MAX_ERROR_FRACTION);

        // Validate result
        assertNotNull(logModel);
        assertEquals(5, logModel.getLineCount());
        assertEquals(5, logModel.getRows().size());
        assertEquals(0, logModel.getErrorCount());
        assertEquals(0, logModel.getInvalidRows().size());
        // Continue with the XES conversion
        XLog xlog = logModel.getXLog();

        // Validate result
        assertNotNull(xlog);

        // TODO this isn't working for Windows machines.
//        assertEquals(expectedXES, toString(xlog));


    }

}