package cft.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private boolean isSortAscending;
    private String outputFileName;
    private String[] inputFileNames;
    private boolean isDataTypeInt;

    public Parser(String[] args) {
    }

    void parse(String[] args) {
        Merge.logger.info("Parsing args started");
        boolean isSortModeSet = false;
        List<String> fileNames = new ArrayList<>();
        if (args.length < 3) {
            throw new IllegalArgumentException("Illegal numbers of arguments: " + args.length);
        }
        if (args[0].equals("-a")) {
            isSortAscending = true;
            isSortModeSet = true;
        } else if (args[0].equals("-d")) {
            isSortAscending = false;
            isSortModeSet = true;
        } else if (!isSortModeSet) {
            if (args[0].equals("-i")) {
                isDataTypeInt = true;
            }
            if (args[0].equals("-s")) {
                isDataTypeInt = false;
            }
            if (!args[0].equals("-i") && !args[0].equals("-s")) {
                throw new IllegalArgumentException("Invalid argument values: " + args[0]);
            }
        }
        if (isSortModeSet) {
            if (args[1].equals("-i")) {
                isDataTypeInt = true;
            } else if (args[1].equals("-s")) {
                isDataTypeInt = false;
            } else {
                throw new IllegalArgumentException("Invalid argument values: " + args[1]);
            }
        }
        if (isSortModeSet && args.length == 3) {
            throw new IllegalArgumentException("Not all arguments were specified.");
        }
        if (isSortModeSet) {
            outputFileName = args[2];
        } else {
            outputFileName = args[1];
        }
        int correction = isSortModeSet ? 1 : 0;
        for (int i = correction + 2; i < args.length; i++) {
            fileNames.add(args[i]);
        }
        inputFileNames = fileNames.toArray(new String[fileNames.size()]);
        if (!isSortModeSet) {
            isSortAscending = true;
        }
        Merge.logger.info("Parsing finished." + this.toString());
    }

    @Override
    public String toString() {
        return "Parser{" +
                "isSortAscending=" + isSortAscending +
                ", outputFileName='" + outputFileName + '\'' +
                ", inputFileNames=" + Arrays.toString(inputFileNames) +
                ", isDataTypeInt=" + isDataTypeInt +
                '}';
    }

    public boolean isSortAscending() {
        return isSortAscending;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public String[] getInputFileNames() {
        return inputFileNames;
    }

    public boolean isDataTypeInt() {
        return isDataTypeInt;
    }
}
