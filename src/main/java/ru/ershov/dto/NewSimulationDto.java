package ru.ershov.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewSimulationDto {
    private int source;
    private int buffer;
    private int device;
    private boolean step;
}
