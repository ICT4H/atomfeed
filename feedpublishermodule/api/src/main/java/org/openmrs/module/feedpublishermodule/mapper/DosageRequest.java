package org.openmrs.module.feedpublishermodule.mapper;

import org.joda.time.LocalDate;
import org.openmrs.DrugOrder;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DosageRequest {
    private final static Pattern PATTERN = Pattern.compile("^(\\d{1})");
    public int numberOfDaysInAWeek;
    public int numberOfTimesInADay;
    public String drugName;
    public Date startDate;


    public static DosageRequest create(DrugOrder drugOrder) {
        String frequency = drugOrder.getFrequency();
        String drugName = drugOrder.getDrug().getName();
        Date startDate = drugOrder.getStartDate();

        DosageRequest dosageRequest = new DosageRequest();
        dosageRequest.drugName = drugName;
        dosageRequest.startDate = startDate;
        String[] frequencySplit = frequency.split("x");

        dosageRequest.numberOfTimesInADay = matchRangeAsIntegerFor(frequencySplit[0]);
        dosageRequest.numberOfDaysInAWeek = matchRangeAsIntegerFor(frequencySplit[1]);
        return dosageRequest;
    }

    private static int matchRangeAsIntegerFor(String range){
        Matcher matcher = PATTERN.matcher(range.trim());
        matcher.find();
        return Integer.parseInt(matcher.group());
    }
}
