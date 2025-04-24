package AIB.Bean;

import java.io.Serializable;

public class FruitBean implements Serializable {

    private Long id;
    private Long sourceCountryId;
    private String name;
    private String unit;
    private int quantity;

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Default constructor
    public FruitBean() {
    }

    // Parameterized constructor
    public FruitBean(Long id, Long sourceCountryId, String name, String unit) {
        this.id = id;
        this.sourceCountryId = sourceCountryId;
        this.name = name;
        this.unit = unit;
    }

    public FruitBean(Long id, Long sourceCountryId, String name, String unit, int quantity) {
        this.id = id;
        this.sourceCountryId = sourceCountryId;
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceCountryId() {
        return sourceCountryId;
    }

    public void setSourceCountryId(Long sourceCountryId) {
        this.sourceCountryId = sourceCountryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "FruitBean{"
                + "id=" + id
                + ", sourceCountryId=" + sourceCountryId
                + ", name='" + name + '\''
                + ", unit='" + unit + '\''
                + '}';
    }
}
