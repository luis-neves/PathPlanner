package communication;

import org.codehaus.jackson.annotate.JsonProperty;

public class Product {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Article")
    private String article;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("Quantity")
    private float quantity;
    @JsonProperty("Unity")
    private String unity;

    public Product() {
    }

}
