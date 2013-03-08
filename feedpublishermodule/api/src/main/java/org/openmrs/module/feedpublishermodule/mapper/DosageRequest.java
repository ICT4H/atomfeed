package org.openmrs.module.feedpublishermodule.mapper;

import org.joda.time.LocalDate;
import org.openmrs.DrugOrder;
import org.openmrs.Order;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DosageRequest {
    private final static Pattern PATTERN = Pattern.compile("^(\\d{1})");
    public int numberOfDaysInAWeek;
    public int numberOfTimesInADay;
    public String drugName;
    public Date startDate;
    public String uuid;

    public static DosageRequest create(DrugOrder drugOrder) {
        String frequency = drugOrder.getFrequency();
        DosageRequest dosageRequest = new DosageRequest();
        dosageRequest.drugName = drugOrder.getDrug().getName();
        dosageRequest.startDate = drugOrder.getStartDate();
        String[] frequencySplit = frequency.split("x");

        dosageRequest.numberOfTimesInADay = matchRangeAsIntegerFor(frequencySplit[0]);
        dosageRequest.numberOfDaysInAWeek = matchRangeAsIntegerFor(frequencySplit[1]);

        dosageRequest.uuid = drugOrder.getUuid();
        return dosageRequest;
    }

    private static int matchRangeAsIntegerFor(String range){
        Matcher matcher = PATTERN.matcher(range.trim());
        matcher.find();
        return Integer.parseInt(matcher.group());
    }
}
