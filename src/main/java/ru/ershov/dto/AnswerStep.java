package ru.ershov.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerStep {
    private String name;
    private String nameSource;
    private double time;
    private String type;
    private int countRequest;
    private int countRefusal;
}
