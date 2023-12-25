package edu.michalvavrik.cdc;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class GreetingsEntity extends PanacheEntity {

    String greeting;

}
