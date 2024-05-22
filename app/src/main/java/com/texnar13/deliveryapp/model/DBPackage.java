package com.texnar13.deliveryapp.model;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DBPackage {
    public static final String TABLE_NAME = "Pckage";

    public static final String PACKAGE_CATEGORY = "Category";
    public static final String PACKAGE_DESCRIPTION = "Description";
    public static final String PACKAGE_DIMENSIONS_ARRAY = "Dimension";
    public static final String PACKAGE_WEIGHT = "Weight";
    public static final String PACKAGE_NAME = "Name";
    public static final String PACKAGE_PICTURE = "Pictures";

    private final String category;
    private final String description;
    private final double[] dimensions;
    private final double weight;
    private final String name;
    private final String picture;

/*
"Package":{
	"Category":"Electronics",
	"Description":"The box is sleek and sturdy, designed to ensure the safe delivery of the laptop inside. It s made of durable cardboard with reinforced edges and corners, providing extra protection during transit. The exterior is plain brown, adorned only with a shipping label and fragile stickers for added caution. Inside, the laptop rests snugly within custom-fit foam padding, keeping it secure and cushioned against any bumps or jostles during its journey",
	"Dimension":[
		{"$numberDouble":"35.56"},
		{"$numberDouble":"25.3"},
		{"$numberDouble":"3.2"}
	],
	"Name":"Box",
	"Picture":"URL",
	"Weight":{"$numberDouble":"1.1"}
}
 */

    public DBPackage(Document notificationDocument) {
        this.category = notificationDocument.getString(PACKAGE_CATEGORY);
        this.description = notificationDocument.getString(PACKAGE_DESCRIPTION);
        this.weight = notificationDocument.getDouble(PACKAGE_WEIGHT);
        this.name = notificationDocument.getString(PACKAGE_NAME);
        this.picture = notificationDocument.getString(PACKAGE_PICTURE);


        // и массив размеров Dimension
        List<Double> dimensionDocs = notificationDocument.getList(PACKAGE_DIMENSIONS_ARRAY, Double.class);
        this.dimensions = new double[dimensionDocs.size()];
        for (int i = 0; i < dimensionDocs.size(); i++) {
            this.dimensions[i] = dimensionDocs.get(i);
        }
    }

    public DBPackage(String category, String description, double[] dimensions, double weight, String name, String picture) {
        this.category = category;
        this.description = description;
        this.dimensions = dimensions;
        this.weight = weight;
        this.name = name;
        this.picture = picture;
    }

    public Document toDocument() {
        return new Document()
                .append(PACKAGE_CATEGORY, category)
                .append(PACKAGE_DESCRIPTION, description)
                .append(PACKAGE_DIMENSIONS_ARRAY, Arrays.asList(
                        new Document("$numberDouble", ""+dimensions[0]),
                        new Document("$numberDouble", ""+dimensions[1]),
                        new Document("$numberDouble", ""+dimensions[2])
                ))
                .append(PACKAGE_WEIGHT, weight)
                .append(PACKAGE_NAME, name)
                .append(PACKAGE_PICTURE, picture);
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public double[] getDimensions() {
        return dimensions;
    }

    public String getDimensionsString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            if (i == 2)
                result.append(dimensions[i]);
            else
                result.append(dimensions[i]).append(", ");
        }
        return result.toString();
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }


    public double getWeight() {
        return weight;
    }
}
