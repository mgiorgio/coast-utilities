package edu.uci.ics.comon.eventprocessor.input.samples.processors;

import java.util.function.Consumer;

import edu.uci.ics.comon.eventprocessor.input.samples.Sample;

public interface SampleProcessor extends Consumer<Sample> {
}
