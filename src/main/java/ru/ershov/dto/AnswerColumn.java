package ru.ershov.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AnswerColumn {

    private int sourceNumber;
    private int countRequest;
    private double pOtc;
    private double tPreb;
    private double tOjid;
    private double tObs;
}
