package com.github.github_data_extractor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author andre endo
 */
public class EntryMain {

    public static String OUTPUT_CSV_FILE = "./res/githubdata.csv";
    public static String INPUT_PROJECTS_FILE = "./res/games-1.csv";

    public static void main(String args[]) throws Exception {
        var f = new BufferedWriter( new FileWriter(OUTPUT_CSV_FILE) );

        var reader = Files.newBufferedReader(Paths.get(INPUT_PROJECTS_FILE));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());
        Iterable<CSVRecord> csvRecords = csvParser.getRecords();
        
        var metricsExtractor = new MetricsExtractor();
        metricsExtractor.setSixMonthsDate(Utils.getSixMonthsAgoDate(new Date()));

        System.out.println( "orderId," + GithubMetrics.header() );
        f.write( "orderId," + GithubMetrics.header() + "\n");
        
        int i = 0;
        for (CSVRecord csvRecord : csvRecords) {
            String appName = csvRecord.get("Game");
            String repoName = csvRecord.get("Github_Link").replaceFirst("https://github.com/", "");
            GithubMetrics metrics = metricsExtractor.extractForRepo(appName, repoName);
            ++i;
            System.out.println(i + "," + metrics.toString());
            f.write( i + "," + metrics.toString() + "\n");
            f.flush();

            TimeUnit.MINUTES.sleep(1);
        }
        
        f.close();
    }
}