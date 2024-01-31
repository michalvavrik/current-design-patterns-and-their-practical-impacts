package edu.michalvavrik.microservice;

public class ProductDTO {
    private final String name;
    private final float prize;

    public ProductDTO(String name, float prize) {
        this.name = name;
        this.prize = prize;
    }

    public String getName() {
        return name;
    }

    public float getPrize() {
        return prize;
    }
}
