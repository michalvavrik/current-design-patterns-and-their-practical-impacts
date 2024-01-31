package edu.michalvavrik.microservice;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class ProductEntity extends PanacheEntity {

    String name;
    float prize;

}
