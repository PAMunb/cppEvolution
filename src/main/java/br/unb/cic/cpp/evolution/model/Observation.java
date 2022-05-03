package br.unb.cic.cpp.evolution.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Observation {
    private ObservationType type;
    private String code;
}
