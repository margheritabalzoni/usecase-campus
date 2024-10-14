package org.example.ontology;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import io.github.webbasedwodt.model.ontology.Literal;
import io.github.webbasedwodt.model.ontology.DTOntology;
import io.github.webbasedwodt.model.ontology.Individual;
import io.github.webbasedwodt.model.ontology.Node;
import io.github.webbasedwodt.model.ontology.Property;

public class RoomOntology implements DTOntology {

    private static final Map<String, Pair<String, String>> PROPERTY_MAP = Map.of(
        "room-name", Pair.of("http://xmlns.com/foaf/spec/#name", "https://www.w3.org/2001/XMLSchema#string")
    );

    @Override
    public String getDigitalTwinType() {
        return "https://wodtcampusontology.com/ontology#Room";
    }

    @Override
    public Optional<Property> obtainProperty(String rawProperty) {
        if (PROPERTY_MAP.containsKey(rawProperty)) {
            return Optional.of(new Property(PROPERTY_MAP.get(rawProperty).getLeft()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> obtainPropertyValueType(String rawProperty) {
        if (PROPERTY_MAP.containsKey(rawProperty)) {
            return Optional.of(PROPERTY_MAP.get(rawProperty).getRight());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public <T> Optional<Pair<Property, Node>> convertPropertyValue(String rawProperty, T value) {
        if (PROPERTY_MAP.containsKey(rawProperty)) {
            return Optional.of(Pair.of(new Property(PROPERTY_MAP.get(rawProperty).getLeft()), new Literal<>(value)));
        } else {
            return Optional.empty();
        }
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
