package cft.test;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class CustomFile extends File {
    private boolean empty;
    private boolean intContent;
    private boolean sortedAscending;
    private static final Comparator<Integer> INT_COMPARATOR = Integer::compare;
    private static final Comparator<String> STRING_COMPARATOR = (String s1, String s2) -> s1.compareTo(s2);

    public CustomFile(String fileName) {
        super(fileName);
        this.intContent = Merge.parser.isDataTypeInt();
    }
    public static CustomFile[] initFilesIfExist(String[] fileNames) {
        Merge.logger.info("Files initialization started.");
        CustomFile[] filesForCheck = new CustomFile[fileNames.length];
        List<CustomFile> checkedFiles = new ArrayList<>();
        for (int i = 0; i < filesForCheck.length; i++) {
            filesForCheck[i] = new CustomFile(fileNames[i]);
            if (filesForCheck[i].exists()) {
                try {
                    filesForCheck[i].checkFileContent();
                } catch (IOException e) {
                    Merge.logger.error(e.getMessage());
                    continue;
                }
                if (filesForCheck[i].isEmpty() == false) {
                    checkedFiles.add(filesForCheck[i]);
                } else {
                    Merge.logger.info(filesForCheck[i].getName() + " is empty. Reading complete.");
                }
            } else {
                Merge.logger.warn("File " + filesForCheck[i].getName() + " does not exist.");
            }
        }
        Merge.logger.info("Files initialization done.");
        return (checkedFiles.toArray(new CustomFile[checkedFiles.size()]));
    }

    private void checkFileContent() throws IOException {
        CustomReader reader = new CustomReader(this, true);
        Comparable line, nextLine;
        try {
            line = reader.readLine();
            if (line == null) {
                this.empty = true;
                return;
            }
        } catch (NumberFormatException e) {
            Merge.logger.warn("File " + this.getName() + " is broken from 1st string");
            return;
        }
        try {
            nextLine = reader.readLine();
            while (line != null && nextLine != null && (this.getComparator().compare(line, nextLine) == 0)) {
                line = nextLine;
                nextLine = reader.readLine();
            }
            if (nextLine == null) {
                this.sortedAscending = true;
                return;
            }
            if (line != null && nextLine != null && this.getComparator().compare(line, nextLine) > 0) {
                this.sortedAscending = false;
                return;
            } else if (line!=null && nextLine != null && this.getComparator().compare(line, nextLine) < 0 ){
                this.sortedAscending = true;
            }
        } catch (NumberFormatException e) {
            Merge.logger.error(e + " in file " + this.getName());
        }
    }

    public static void merge(CustomFile[] input, CustomFile output, boolean ascending) {
        Merge.logger.info("Merging started.");
        List<CustomFile> inputFiles = new ArrayList<>(input.length);
        List<CustomReader> inputReader = getReaders(input);
        List<Comparable> currentLine = new ArrayList<>(inputReader.size());
        List<Comparable> nextLine = new ArrayList<>(inputReader.size());
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));
            for (int i = 0; i < input.length; i++) {
                inputFiles.add(input[i]);
                try{
                    currentLine.add(inputReader.get(i).readLine());
                    nextLine.add(inputReader.get(i).readLine());
                }catch (NumberFormatException e){
                    Merge.logger.error(e + "in file " + inputFiles.get(i));
                    nextLine.add(null);
                }
            }
            while(currentLine.size()!=0) {
                int minMaxIndex;
                minMaxIndex = getMinMaxIndex(currentLine, output.getComparator(), ascending);
                writer.write(currentLine.get(minMaxIndex).toString());
                writer.newLine();
                if(nextLine.get(minMaxIndex)!=null){
                    try {
                        if (ascending) {
                            if (output.getComparator().compare(nextLine.get(minMaxIndex), currentLine.get(minMaxIndex)) >= 0) {
                                currentLine.set(minMaxIndex, nextLine.get(minMaxIndex));
                                nextLine.set(minMaxIndex, inputReader.get(minMaxIndex).readLine());
                            } else {
                                Merge.logger.error("Error in file " + inputFiles.get(minMaxIndex).getName()
                                        + ". Not sorted: "
                                        + nextLine.get(minMaxIndex)
                                        + " after " + currentLine.get(minMaxIndex)
                                        + ". Reading file "
                                        + inputFiles.get(minMaxIndex).getName()
                                        + " stopped");
                                inputFiles.remove(minMaxIndex);
                                inputReader.remove(minMaxIndex);
                                currentLine.remove(minMaxIndex);
                                nextLine.remove(minMaxIndex);
                            }
                        } else {
                            if (output.getComparator().compare(nextLine.get(minMaxIndex), currentLine.get(minMaxIndex)) <= 0) {
                                currentLine.set(minMaxIndex, nextLine.get(minMaxIndex));
                                nextLine.set(minMaxIndex, inputReader.get(minMaxIndex).readLine());
                            } else {
                                Merge.logger.error("Error in file " + inputFiles.get(minMaxIndex).getName()
                                        + ". Not sorted: " + nextLine.get(minMaxIndex)
                                        + " after " + currentLine.get(minMaxIndex)
                                        + ". Reading file " + inputFiles.get(minMaxIndex).getName() + " stopper");
                                inputFiles.remove(minMaxIndex);
                                inputReader.remove(minMaxIndex);
                                currentLine.remove(minMaxIndex);
                                nextLine.remove(minMaxIndex);
                            }
                        }
                    } catch (NumberFormatException e){
                        Merge.logger.error(e.getMessage());
                        Merge.logger.error("Error in file " + inputFiles.get(minMaxIndex).getName()
                                + ". Content error: " + e.getLocalizedMessage()
                                + ". Reading file " + inputFiles.get(minMaxIndex).getName() + " stopped.");
                        inputFiles.remove(minMaxIndex);
                        inputReader.remove(minMaxIndex);
                        currentLine.remove(minMaxIndex);
                        nextLine.remove(minMaxIndex);
                    }
                } else {
                    Merge.logger.info("Reading file " + inputFiles.get(minMaxIndex).getName() + " completed");
                    inputFiles.remove(minMaxIndex);
                    inputReader.remove(minMaxIndex);
                    currentLine.remove(minMaxIndex);
                    nextLine.remove(minMaxIndex);
                }
                writer.flush();
            }
            writer.close();
        } catch (IOException e) {
            Merge.logger.error(e.getMessage());
        }
        Merge.logger.info("Merging done");
    }

    private static int getMinMaxIndex(List<Comparable> currentLine, Comparator comparator, boolean ascending) {
        Comparable min = currentLine.get(0);
        for (Comparable lineValue : currentLine) {
            if ((comparator.compare(min, lineValue) > 0)== ascending) {
                min = lineValue;
            }
        }
        return currentLine.indexOf(min);
    }

    private static List<CustomReader> getReaders(CustomFile[] input) {
        List<CustomReader> inputReaders = new ArrayList<>(input.length);
        for (CustomFile file : input) {
            try {
                inputReaders.add(new CustomReader(file));
            } catch (IOException e) {
                Merge.logger.error(e.getMessage());
            }
        }
        return inputReaders;
    }

    private Comparator getComparator() {
        if (intContent) {
            return INT_COMPARATOR;
        }
        return STRING_COMPARATOR;
    }



    public boolean isSortedAscending() {
        return sortedAscending;
    }

    public boolean isEmpty() {
        return this.empty;
    }

}


