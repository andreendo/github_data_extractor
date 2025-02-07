package com.github.github_data_extractor;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHEvent;
import org.kohsuke.github.GHEventInfo;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;

/**
 *
 * @author andre endo
 */
public class TestGithub1 {

    public static void main(String args[]) throws Exception {
        Reader reader = Files.newBufferedReader(Paths.get("./res/MA663.csv"));
        //Reader reader = Files.newBufferedReader(Paths.get("./res/results-TEST.csv"));
        
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());
        
        
        Iterable<CSVRecord> csvRecords = csvParser.getRecords();
        
        MetricsExtractor metricsExtractor = new MetricsExtractor();
        
        int i = 0;
        for (CSVRecord csvRecord : csvRecords) {
            String appName = csvRecord.get("appid");
            String repoName = csvRecord.get("sourceRep").replaceFirst("https://github.com/", "");
            
            System.out.println(++i + "," + repoName);
            
            //Thread.sleep(5000);
        }
        
        
        
        /*GHRepository repo = github.getRepository(repoName);
        System.out.println("Contributors: " + repo.listContributors().asList().size());
        System.out.println("Language: " + repo.getLanguage());
        System.out.println("Commits: " + repo.listCommits().asList().size());
        System.out.println("Created at: " + repo.getCreatedAt());
        
        
        /*
        List<GHCommit> commits = repo.listCommits().asList();
        for(GHCommit c : commits) {
            System.out.println(c.getCommitDate());
        }*/
    }
}
