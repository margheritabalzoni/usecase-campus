package org.example.ontology;

import io.github.webbasedwodt.model.ontology.DTOntology;
import io.github.webbasedwodt.model.ontology.Individual;
import io.github.webbasedwodt.model.ontology.Node;
import io.github.webbasedwodt.model.ontology.Property;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Optional;

public class CampusOntology implements DTOntology {

    private static final Map<String, Pair<String, String>> RELATIONSHIP_MAP = Map.of(
        "contains-room", Pair.of("https://wodtcampusontology.com/ontology#containsRoom", "https://wodtcampusontology.com/ontology#Room")
    );

    @Override
    public String getDigitalTwinType() {
        return "https://wodtcampusontology.com/ontology#Campus";
    }

    @Override
    public Optional<Property> obtainProperty(String rawProperty) {
        if (RELATIONSHIP_MAP.containsKey(rawProperty)) {
            return Optional.of(new Property(RELATIONSHIP_MAP.get(rawProperty).getLeft()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> obtainPropertyValueType(String rawProperty) {
        if (RELATIONSHIP_MAP.containsKey(rawProperty)) {
            return Optional.of(RELATIONSHIP_MAP.get(rawProperty).getRight());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public <T> Optional<Pair<Property, Node>> convertPropertyValue(String rawProperty, T value) {
        return Optional.empty();
    }

    @Override
    public Optional<Pair<Property, Individual>> convertRelationship(String rawRelationship, String targetUri) {
        if (RELATIONSHIP_MAP.containsKey(rawRelationship)) {
            return Optional.of(
                    Pair.of(new Property(RELATIONSHIP_MAP.get(rawRelationship).getLeft()), new Individual(targetUri))
            );
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> obtainActionType(String rawAction) {
        return Optional.empty();
    }

}
