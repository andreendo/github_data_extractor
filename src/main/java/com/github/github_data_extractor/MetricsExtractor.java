package com.github.github_data_extractor;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.cdimascio.dotenv.Dotenv;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

/**
 *
 * @author andreendo
 */
public class MetricsExtractor {

    GitHub github;
    
    //Constant for six months ago
    private Date sixMonthsDate;
    private HashMap<String, Integer> committersParticipation;

    public MetricsExtractor() {
        try {
            // Get your here https://docs.github.com/en/organizations/managing-programmatic-access-to-your-organization/setting-a-personal-access-token-policy-for-your-organization#restricting-access-by-personal-access-tokens
            // Add .env file at the root of the project
            var dotenv = Dotenv.load();
            github = GitHub.connect(dotenv.get("GITHUB_LOGIN"), dotenv.get("GITHUB_TOKEN"));
        } catch (IOException ex) {
            Logger.getLogger(MetricsExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setSixMonthsDate(Date sixMonthsDate) {
        this.sixMonthsDate = sixMonthsDate;
    }

    public GithubMetrics extractForRepo(String appName, String repoName) throws Exception {
        if (sixMonthsDate == null) {
            throw new RuntimeException("set six month date first");
        }

        GithubMetrics metrics = new GithubMetrics(appName, repoName);
        GHRepository repo = github.getRepository(repoName);
        //general
        populateGeneralMetrics(repo, metrics);
        //level of interest
        populateInterestMetrics(repo, metrics);
        //activity
        populateActivityMetrics(repo, metrics);
        //team
        populateTeamMetrics(repo, metrics);
        
        return metrics;
    }

    public void populateGeneralMetrics(GHRepository repo, GithubMetrics metrics) throws Exception {
        metrics.setMetric("language", repo.getLanguage());
        metrics.setMetric("createdAt", repo.getCreatedAt().toString() );    
        metrics.setMetric("repoAge", ""+getDiffInDays(new Date(), repo.getCreatedAt()) );
    }
    
    public void populateInterestMetrics(GHRepository repo, GithubMetrics metrics) throws Exception {
        metrics.setMetric("forks", ""+repo.getForks() );
        metrics.setMetric("watchers", ""+repo.getWatchers() );
        metrics.setMetric("stars", ""+repo.getStargazersCount() );        
    }
    
    public void populateActivityMetrics(GHRepository repo, GithubMetrics metrics) throws Exception {
        //issues related
        if(repo.hasIssues()) {
            List<GHIssue> allIssues = repo.getIssues(GHIssueState.ALL);
            List<GHIssue> openIssues = repo.getIssues(GHIssueState.OPEN);
            List<GHIssue> closedIssues = repo.getIssues(GHIssueState.CLOSED);
            
            metrics.setMetric("openIssues", ""+openIssues.size() );
            metrics.setMetric("closedIssues", ""+closedIssues.size() );
            metrics.setMetric("totalIssues", ""+ (openIssues.size() + closedIssues.size()) );
            metrics.setMetric("IssuesOpenedLast6Months", ""+ countIssuesOpenedLastSixMonths(allIssues) );
            metrics.setMetric("AvgIssueCloseTime", ""+ GetAverageIssueCloseTime(closedIssues) );
        }
        else {
            metrics.setMetric("openIssues", "0" );
            metrics.setMetric("closedIssues", "0" );
            metrics.setMetric("totalIssues", "0" );
            metrics.setMetric("IssuesOpenedLast6Months", "0" );
            metrics.setMetric("AvgIssueCloseTime", "0" );
        }
        
        //commits related
        List<GHCommit> commits = repo.listCommits().asList();
        metrics.setMetric("commits", "" + commits.size() );
        
        //
        Date dateOfLastCommit = new GregorianCalendar(1970, Calendar.AUGUST, 27).getTime();
        int numCommitsLast6Months = 0;
        committersParticipation = new HashMap<>();
        for (GHCommit commit : commits) {
            Date commitDate = commit.getCommitDate();
            if(commitDate.after(dateOfLastCommit))
                dateOfLastCommit = commitDate;
            
            if(commitDate.after(sixMonthsDate))
                numCommitsLast6Months++;
            
            //for some commits, it is not possible to retrieve user
            String committer = "NULL####";
            if(commit.getAuthor() != null)
                committer = commit.getAuthor().getLogin();
            
            
            Integer nCommitsPerCommitter = committersParticipation.get(committer);
            if(nCommitsPerCommitter == null)
                nCommitsPerCommitter = 0;
            nCommitsPerCommitter++;
            committersParticipation.put(committer, nCommitsPerCommitter);
        }
        metrics.setMetric("dateOfLastCommit", dateOfLastCommit.toString() );
        
        metrics.setMetric("daysOfActivity", ""+getDiffInDays(dateOfLastCommit, repo.getCreatedAt()) );
        
        metrics.setMetric("dateOfLastCommit", dateOfLastCommit.toString() );
        
        metrics.setMetric("commitsInLast6Months", ""+numCommitsLast6Months );
    }
    
    public void populateTeamMetrics(GHRepository repo, GithubMetrics metrics) throws Exception {
        metrics.setMetric("contributors", ""+repo.listContributors().asList().size() );
        
        String participation = committersParticipation.values().toString();
        participation = participation.replace(",", ";");
        
        metrics.setMetric("committersParticipation",  participation);
    }

    private int countIssuesOpenedLastSixMonths(List<GHIssue> issues) throws Exception {
        int numOfIssues = 0;
        for (GHIssue issue : issues) {
            Date d = issue.getCreatedAt();
            if(d.after(sixMonthsDate))
                numOfIssues++;
        }
        return numOfIssues;
    }
    
    private double GetAverageIssueCloseTime(List<GHIssue> closedIssues) throws Exception {
        double sum = 0;
        for (GHIssue issue : closedIssues) {
            Date createdAt = issue.getCreatedAt();
            Date closedAt = issue.getClosedAt();    
            sum += getDiffInDays(closedAt, createdAt);
        }
        
        if(closedIssues.isEmpty())
            return 0;
        
        return sum / closedIssues.size();
    }
    
    private long getDiffInDays(Date end, Date begin) {
        long diffTime = end.getTime() - begin.getTime();
        long diffDays = diffTime / (1000 * 60 * 60 * 24);
        return diffDays;
    }
}
