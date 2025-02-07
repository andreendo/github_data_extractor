package com.github.github_data_extractor.processData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author andreendo
 */
public class DataProcessor {
    public static void main(String[] args) throws Exception {
        //processCommittersData();
        processCommittersParticipation();
        
    }

    public static void processCommittersParticipation() throws IOException {
        BufferedWriter f = new BufferedWriter( new FileWriter("./res/committersPart.csv") );
        
        Reader reader = Files.newBufferedReader(Paths.get("./res/githubdata-MA663-toprocess.csv"));
        
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim());
        
        
        Iterable<CSVRecord> csvRecords = csvParser.getRecords();
        
        f.write( "appId,committerParticipation\n");
        
        for (CSVRecord csvRecord : csvRecords) {
            String appName = csvRecord.get("app");
            String s = csvRecord.get("committersParticipation");
            s = s.replaceFirst("\\[", "");
            s = s.replaceFirst("\\]", "");
            s = s.replaceFirst(" ", "");
            String co[] = s.split(";");
            float totalCommits = 0;
            for(String c : co) {
                totalCommits += Float.valueOf(c);
            }

            for(String c : co) {
                f.write( appName + "," + 100*(Float.valueOf(c) / totalCommits) + "\n");
            }

            
            
            f.flush();          
        }
        
        f.close();
    }    
    
    
    public static void processCommittersData() throws IOException {
        BufferedWriter f = new BufferedWriter( new FileWriter("./res/committers.csv") );
        
        Reader reader = Files.newBufferedReader(Paths.get("./res/githubdata-MA663-toprocess.csv"));
        
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim());
        
        
        Iterable<CSVRecord> csvRecords = csvParser.getRecords();
        
        f.write( "appId,committers\n");
        
        for (CSVRecord csvRecord : csvRecords) {
            String appName = csvRecord.get("app");
            String s = csvRecord.get("committersParticipation");
            s = s.replaceFirst("\\[", "");
            s = s.replaceFirst("\\]", "");
            s = s.replaceFirst(" ", "");
            String co[] = s.split(";");
            
            f.write( appName + "," + co.length + "\n");
            f.flush();          
        }
        
        f.close();
    }
}
