/**
 * 
 */
package com.leroymerlin.corp.fr.nuxeo.labs.site.theme.bean;

/**
 * @author fvandaele
 *
 */
public class ThemeProperty {
    
    public static final int ORDER_NUMBER_DEFAULT_VALUE = -1;

    private String key;
    
    private String value;
    
    private String label;
    
    private String description;
    
    private String type;
    
    private int orderNumber;
    
    public ThemeProperty() {
        this.key = "";
        this.value = "";
        this.label = "";
        this.description = "";
        this.orderNumber = ORDER_NUMBER_DEFAULT_VALUE;
    }

    public ThemeProperty(String key, String value, String label, String description, String type, int orderNumber) {
        super();
        this.key = key;
        this.value = value;
        this.label = label;
        this.description = description;
        this.type = type;
        this.orderNumber = orderNumber;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ThemeProperty other = (ThemeProperty) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
}
