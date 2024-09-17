package org.example;


import io.github.webbasedwodt.adapter.WoDTDigitalAdapter;
import io.github.webbasedwodt.adapter.WoDTDigitalAdapterConfiguration;
import it.wldt.core.engine.DigitalTwin;
import it.wldt.core.engine.DigitalTwinEngine;
import it.wldt.exception.EventBusException;
import it.wldt.exception.ModelException;
import it.wldt.exception.WldtConfigurationException;
import it.wldt.exception.WldtDigitalTwinStateException;
import it.wldt.exception.WldtEngineException;
import it.wldt.exception.WldtRuntimeException;
import it.wldt.exception.WldtWorkerException;

import java.net.URI;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import org.example.ontology.PersonOntology;
import org.example.ontology.RoomOntology;
import org.example.physicalAdapter.CampusPA;
import org.example.physicalAdapter.PersonPA;
import org.example.physicalAdapter.RoomPA;
import org.example.shadowingFunction.UseCaseShadowingFunction;

public final class Launcher {

    private static final String PERSON_URI_VARIABLE = "PERSON_URI";
    private static final String ROOM_1_URI_VARIABLE = "ROOM_1_URI";
    private static final String ROOM_2_URI_VARIABLE = "ROOM_2_URI";
    private static final String PERSON_EXPOSED_PORT_VARIABLE = "PERSON_EXPOSED_PORT";
    private static final String ROOM1_EXPOSED_PORT_VARIABLE = "ROOM1_EXPOSED_PORT";
    private static final String ROOM2_EXPOSED_PORT_VARIABLE = "ROOM2_EXPOSED_PORT";
    private static final String CAMPUS_EXPOSED_PORT_VARIABLE = "CAMPUS_EXPOSED_PORT";
    private static final String PLATFORM_URL_VARIABLE = "PLATFORM_URL";
    

    static {
       
        Objects.requireNonNull(System.getenv(PERSON_URI_VARIABLE), "Please provide the person uri");
        Objects.requireNonNull(System.getenv(ROOM_1_URI_VARIABLE), "Please provide the room 1 uri");
        Objects.requireNonNull(System.getenv(ROOM_2_URI_VARIABLE), "Please provide the room 2 uri");
        Objects.requireNonNull(System.getenv(PERSON_EXPOSED_PORT_VARIABLE), "Please provide the exposed port");
        Objects.requireNonNull(System.getenv(ROOM1_EXPOSED_PORT_VARIABLE), "Please provide the exposed port");
        Objects.requireNonNull(System.getenv(ROOM2_EXPOSED_PORT_VARIABLE), "Please provide the exposed port");
        Objects.requireNonNull(System.getenv(CAMPUS_EXPOSED_PORT_VARIABLE), "Please provide the exposed port");
        Objects.requireNonNull(System.getenv(PLATFORM_URL_VARIABLE), "Please provide the platform url");
    }

    public Launcher() {

    }

   
    public static void main(String[] args) {
        try {

            final String room1DTId = "room1-dt";
            final String room2DTId = "room2-dt";
            final String personDTId = "person-dt";
            final String campusDTId = "campus-dt";

            final int personPortNumber = Integer.parseInt(System.getenv(PERSON_EXPOSED_PORT_VARIABLE));
            final int room1PortNumber = Integer.parseInt(System.getenv(ROOM1_EXPOSED_PORT_VARIABLE));
            final int room2PortNumber = Integer.parseInt(System.getenv(ROOM2_EXPOSED_PORT_VARIABLE));
            final int campusPortNumber = Integer.parseInt(System.getenv(CAMPUS_EXPOSED_PORT_VARIABLE));

            final DigitalTwin personDT = new DigitalTwin(personDTId, new UseCaseShadowingFunction());
            personDT.addPhysicalAdapter(new PersonPA());
            personDT.addDigitalAdapter(new WoDTDigitalAdapter("wodt-dt-person-digital-adapter",
                new WoDTDigitalAdapterConfiguration(
                            "http://localhost:" + personPortNumber + "/",
                            new PersonOntology(),
                            personPortNumber,
                            "person-physiscal-adapter",
                            Set.of(URI.create(System.getenv(PLATFORM_URL_VARIABLE))))
            ));

            final DigitalTwin room1DT = new DigitalTwin(room1DTId, new UseCaseShadowingFunction());
            room1DT.addPhysicalAdapter(new RoomPA(
                System.getenv(PERSON_URI_VARIABLE)
            ));
            room1DT.addDigitalAdapter(new WoDTDigitalAdapter(
                    "wodt-dt-room1-adapter",
                    new WoDTDigitalAdapterConfiguration(
                            "http://localhost:" + room1PortNumber + "/",
                            new RoomOntology(),
                            room1PortNumber,
                            "room1-physiscal-adapter",
                            Set.of(URI.create(System.getenv(PLATFORM_URL_VARIABLE))))
            ));

            final DigitalTwin room2DT = new DigitalTwin(room2DTId, new UseCaseShadowingFunction());
            room2DT.addPhysicalAdapter(new RoomPA(
                System.getenv(PERSON_URI_VARIABLE)
            ));
            room2DT.addDigitalAdapter(new WoDTDigitalAdapter(
                    "wodt-dt-room2-adapter",
                    new WoDTDigitalAdapterConfiguration(
                            "http://localhost:" + room2PortNumber + "/",
                            new RoomOntology(),
                            room2PortNumber,
                            "room2-physiscal-adapter",
                            Set.of(URI.create(System.getenv(PLATFORM_URL_VARIABLE))))
            ));

            final DigitalTwin campusDT = new DigitalTwin(campusDTId, new UseCaseShadowingFunction());
            campusDT.addPhysicalAdapter(new CampusPA(
                System.getenv(ROOM_1_URI_VARIABLE),
                System.getenv(ROOM_2_URI_VARIABLE)
            ));
            campusDT.addDigitalAdapter(new WoDTDigitalAdapter(
                    "wodt-dt-campus-adapter",
                    new WoDTDigitalAdapterConfiguration(
                            "http://localhost:" + campusPortNumber + "/",
                            new RoomOntology(),
                            campusPortNumber,
                            "campus-physiscal-adapter",
                            Set.of(URI.create(System.getenv(PLATFORM_URL_VARIABLE))))
            ));

            final DigitalTwinEngine digitalTwinEngine = new DigitalTwinEngine();

            digitalTwinEngine.addDigitalTwin(personDT);
            digitalTwinEngine.startDigitalTwin(personDTId);
            digitalTwinEngine.addDigitalTwin(room1DT);
            digitalTwinEngine.startDigitalTwin(room1DTId);
            digitalTwinEngine.addDigitalTwin(room2DT);
            digitalTwinEngine.startDigitalTwin(room2DTId);
            digitalTwinEngine.addDigitalTwin(campusDT);
            digitalTwinEngine.startDigitalTwin(campusDTId);

        } catch (ModelException
                 | WldtDigitalTwinStateException
                 | WldtWorkerException
                 | WldtRuntimeException
                 | EventBusException
                 | WldtConfigurationException
                 | WldtEngineException e) {
            Logger.getLogger(Launcher.class.getName()).info(e.getMessage());
        }
    }
}
