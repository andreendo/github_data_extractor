package com.github.github_data_extractor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author andreendo
 */
public class GithubMetrics {
    private String appName;
    private String repo;
    private Map<String, String> metrics;
    private static List<String> METRICSTOPRINT = Arrays.asList(
            "language","createdAt", "repoAge",                  //general
            "forks", "watchers", "stars",                       //level of interest
            "openIssues", "closedIssues", "totalIssues",        //activity
            "IssuesOpenedLast6Months", "AvgIssueCloseTime",
            "commits", "dateOfLastCommit", "daysOfActivity",
            "commitsInLast6Months",
            "contributors", "committersParticipation");         //team
    
    public GithubMetrics(String appName, String repoName) {
        this.appName = appName;
        this.repo = repoName;
        metrics = new HashMap<>();
    }
    
    public void setMetric(String metricName, String metricValue) {
        metrics.put(metricName, metricValue);
    }
    
    public static String header() {
        String h = "";
        for (String me : METRICSTOPRINT) 
            h = h + "," + me;

        return "game,repo" + h;
    }    

    @Override
    public String toString() {
        StringBuffer toPrint = new StringBuffer(appName + "," + repo);

        for (String me : METRICSTOPRINT) {
            toPrint.append(",");
            toPrint.append( metrics.get(me) );
        }
        

        return toPrint.toString();
    }
}
