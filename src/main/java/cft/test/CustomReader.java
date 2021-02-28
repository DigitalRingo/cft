package cft.test;

import org.apache.commons.io.input.ReversedLinesFileReader;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

class CustomReader implements Closeable {
    private BufferedReader standardReader;
    private ReversedLinesFileReader reversedReader;
    private final boolean REVERSE_READING;

    public CustomReader(CustomFile file) throws IOException {
        this.REVERSE_READING = Merge.parser.isSortAscending()!=file.isSortedAscending();
        if (!this.REVERSE_READING) {
            standardReader = new BufferedReader(new FileReader(file));
        } else {
            reversedReader = new ReversedLinesFileReader(file, Charset.defaultCharset());
        }
    }

    public CustomReader(CustomFile file, boolean isStandardReader) throws IOException{
        REVERSE_READING = false;
        standardReader = new BufferedReader(new FileReader(file));
    }

    public Comparable readLine() throws IOException, NumberFormatException {
        if (!REVERSE_READING && Merge.parser.isDataTypeInt()) {
            try {
                return Integer.valueOf(standardReader.readLine());
            } catch (NumberFormatException e) {
                if (e.getLocalizedMessage() == "null") {
                    return null;
                } else {
                    throw e;
                }
            }
        } else if (!REVERSE_READING && !Merge.parser.isDataTypeInt()) {
            return standardReader.readLine();
        } else if (REVERSE_READING && Merge.parser.isDataTypeInt()) {
            try {
                return Integer.valueOf(reversedReader.readLine());
            } catch (NumberFormatException e) {
                if (e.getLocalizedMessage() == "null") {
                    return null;
                } else {
                    throw e;
                }
            }
        } else {
            return reversedReader.readLine();
        }
    }

    @Override
    public void close() throws IOException {
        if (!REVERSE_READING) {
            standardReader.close();
        } else {
            reversedReader.close();
        }
    }
}
