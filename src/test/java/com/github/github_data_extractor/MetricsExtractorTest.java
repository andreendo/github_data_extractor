package com.github.github_data_extractor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author andreendo
 */
public class MetricsExtractorTest {

    @Test
    public void test01() throws Exception {
        MetricsExtractor metricsExtractor = new MetricsExtractor();
        metricsExtractor.setSixMonthsDate(Utils.getSixMonthsAgoDate(new Date()));
        GithubMetrics metrics = metricsExtractor
                .extractForRepo("PetClinicPageObjects", "andreendo/PetClinicPageObjects");

        System.out.println(metrics.toString());
    }
}