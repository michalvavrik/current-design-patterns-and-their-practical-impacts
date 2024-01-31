package edu.michalvavrik.microservice;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

import java.util.List;

@Path("product")
public class ProductResource {

    private final ProductService productService;

    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    @GET
    public List<ProductDTO> list() {
        return null;
    }

    @GET
    @Path("{id}")
    public ProductDTO getDetail() {
        return null;
    }

    @POST
    public long create(ProductDTO dto) {
        // FIXME: impl. me!
        return 1;
    }


    @PUT
    public void update(ProductDTO dto) {
        // FIXME: impl. me!
    }

}
