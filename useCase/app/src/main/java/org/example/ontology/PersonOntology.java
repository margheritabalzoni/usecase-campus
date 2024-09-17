package org.example.ontology;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import io.github.webbasedwodt.model.ontology.DTOntology;
import io.github.webbasedwodt.model.ontology.Individual;
import io.github.webbasedwodt.model.ontology.Node;
import io.github.webbasedwodt.model.ontology.Property;

public class PersonOntology implements DTOntology{

    public PersonOntology(){

    }
    
    @Override
    public String getDigitalTwinType() {
        return "https://wodtcampusontology.com/ontology#Person";
    }

    @Override
    public Optional<Property> obtainProperty(String rawProperty) {
        return Optional.empty();
    }

    @Override
    public Optional<String> obtainPropertyValueType(String rawProperty) {
        return Optional.empty();
    }

    @Override
    public <T> Optional<Pair<Property, Node>> convertPropertyValue(String rawProperty, T value) {
        return Optional.empty();
    }

    @Override
    public Optional<Pair<Property, Individual>> convertRelationship(String rawRelationship, String targetUri) {
        return Optional.empty();
    }

    @Override
    public Optional<String> obtainActionType(String rawAction) {
        return Optional.empty();
    }

}
