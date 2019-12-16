package org.apromore.service.csvimporter;

import java.util.List;
import java.util.Map;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Window;

/**
 * A sample of a CSV log.
 */
public interface LogSample {

    // Accessors

    List<String> getHeader();

    List<List<String>> getLines();

    Map<String, Integer> getHeads();

    String getTimestampFormat();

    void setTimestampFormat(String s);

    String getStartTsFormat();

    void setStartTsFormat(String s);

    List<Listbox> getLists();

    List<Integer> getIgnoredPos();

    Map<Integer, String> getOtherTimeStampsPos();

    Div getPopUPBox();

    void setPopUPBox(Div popUPBox);


    // Public methods

    void automaticFormat(ListModelList<String[]> result, List<String> myHeader, LogSample sample);
    void openPopUp(LogSample sample);
    void setIgnoreAll(Window window, List<String> sampleLine, LogSample sample);
    void setOtherAll(Window window, List<String> line, LogSample sample);
    void setOtherTimestamps(ListModelList<String[]> result, List<String> sampleLine, LogSample sample);
    void tryParsing(String format, int colPos, List<String> sampleLine, LogSample sample);
}