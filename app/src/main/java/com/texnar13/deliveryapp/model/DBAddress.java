package com.texnar13.deliveryapp.model;

import org.bson.Document;

public class DBAddress {

    public static final String[] ADDRESS_LOCALS = {"country", "city", "district", "street"};

    private final String[] address;


    public DBAddress(Document addressDocument) {
        // кастуем документ в массив адреса
        this.address = new String[ADDRESS_LOCALS.length];
        for (int i = 0; i < ADDRESS_LOCALS.length; i++)
            this.address[i] = addressDocument.getString(ADDRESS_LOCALS[i]);
    }

    public DBAddress(String[] address) {
        if (address.length != ADDRESS_LOCALS.length)
            throw new RuntimeException("address.length != " + ADDRESS_LOCALS.length);

        this.address = address;
    }

    public Document getDocument() {
        Document addressArray = new Document();
        for (int i = 0; i < ADDRESS_LOCALS.length; i++)
            addressArray.put(ADDRESS_LOCALS[i], this.address[i]);
        return addressArray;
    }

    public static Document getDocumentFromAddressArray(String[] address) {
        Document addressDocument = new Document();
        for (int i = 0; i < ADDRESS_LOCALS.length; i++)
            addressDocument.put(ADDRESS_LOCALS[i], address[i]);
        return addressDocument;
    }

    public String[] getArray() {
        return address;
    }

    public String getString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < ADDRESS_LOCALS.length; i++) {
            if (i == ADDRESS_LOCALS.length - 1)
                result.append(address[i]);
            else
                result.append(address[i]).append(", ");
        }
        return result.toString();
    }
}
