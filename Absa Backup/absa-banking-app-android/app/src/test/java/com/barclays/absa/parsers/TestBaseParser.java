/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.parsers;

import com.barclays.absa.banking.boundary.model.Payload;
import com.barclays.absa.banking.boundary.model.PayloadModel;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import junit.framework.Assert;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TestBaseParser {
    protected static final Gson gson = new Gson();
    private static final String base_url = "src/stub/assets/api_responses/";

    protected static String getContentBody(String mockFile) {
        return getContentBody(mockFile, true, true);
    }

    protected static String getContentBody(String mockFile, boolean hasSuccessResponseElement) {
        return getContentBody(mockFile, true, hasSuccessResponseElement);
    }

    private static String getContentBody(String mockFile, boolean print, boolean hasSuccessResponseElement) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(base_url + mockFile)));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
                if (print)
                    System.out.println(line);
            }
            JsonNode jsonElement = getPayDataJson(json.toString(), hasSuccessResponseElement);
            if (jsonElement != null) {
                return jsonElement.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        return null;
    }

    private static JsonNode getPayDataJson(String json) {
        try {
            Payload data = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(json, Payload.class);
            if (data == null) System.out.println("services is null");
            PayloadModel model = data == null ? null : data.getPayloadModel();
            if (model != null) return model.getPayloadData();
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private static JsonNode getPayDataJson(String json, boolean hasSuccessModelElement) {

        if (hasSuccessModelElement) {
            return getPayDataJson(json);
        }

        PayloadModel payloadModel;
        try {
            payloadModel = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(json, PayloadModel.class);
            if (payloadModel != null) {
                return payloadModel.getPayloadData();
            } else {
                System.out.println("Payload model services is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Test
    public void testing() {
        Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes field) {
                return field.getName().equals("name");
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();

        Testing t = new Testing();
        t.name = "kobe";
        t.surname = "bryant";
        System.out.println(gson.toJson(t));
    }
}

class Testing {
    @Expose(serialize = false, deserialize = true)
    String name = "";

    String surname = "";

    @NotNull
    @Override
    public String toString() {
        return name + " . " + surname;
    }
}