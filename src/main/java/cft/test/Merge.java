package cft.test;

import org.apache.log4j.Logger;

public class Merge {
    final static Logger logger = Logger.getLogger(Merge.class);
        static Parser parser;
        public static void main(String[] args) {
            parser = new Parser(args);
            try {
                parser.parse(args);
            } catch (IllegalArgumentException e){
                logger.error("Program will be stopped: " + e.getMessage());
                System.exit(1);
            }
            CustomFile output = new CustomFile(parser.getOutputFileName());
            CustomFile[] inputFiles = CustomFile.initFilesIfExist(parser.getInputFileNames());
            CustomFile.merge(inputFiles, output,parser.isSortAscending());
        }

    }
