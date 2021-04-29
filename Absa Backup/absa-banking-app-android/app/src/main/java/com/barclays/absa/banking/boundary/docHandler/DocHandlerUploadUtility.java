/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 *
 */

package com.barclays.absa.banking.boundary.docHandler;


import android.graphics.Bitmap;

import com.barclays.absa.utils.ImageUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

class DocHandlerUploadUtility {

    private final String LINE_FEED = "\r\n";
    private final String boundary;
    private HttpURLConnection httpConn;
    private OutputStream outputStream;
    private DataOutputStream request;
    private PrintWriter writer;
    private String charset = "UTF-8";

    DocHandlerUploadUtility(HttpURLConnection Connection, String authToken) throws IOException {
        this.httpConn = Connection;
        boundary = "===" + System.currentTimeMillis() + "===";
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Authorization", authToken);
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
    }

    void addFormField(String name, String value) {
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=").append(charset).append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    void addFilePart(String fileName, Bitmap bitmap) throws IOException {
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append("files[]").append("\"; filename=\"").append(fileName).append("\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=").append(charset).append(LINE_FEED);
        writer.append(LINE_FEED);
        String bitmapToBase64EncodedString = ImageUtils.convertBitmapToBase64EncodedStringQuality80(bitmap);
        writer.append(bitmapToBase64EncodedString).append(LINE_FEED);
        writer.flush();
    }

    void addFilePart(String fileName, byte[] file) throws IOException {
        request = new DataOutputStream(httpConn.getOutputStream());
        request.writeBytes("--" + boundary + LINE_FEED);
        request.writeBytes("Content-Disposition: form-data; name=\"" + "\";filename=\"" + fileName + "\"" + LINE_FEED);
        request.writeBytes(LINE_FEED);
        request.write(file);
    }

    public String finish() throws IOException {
        StringBuilder response = new StringBuilder();
        writer.append(LINE_FEED).flush();
        writer.append("--").append(boundary).append("--").append(LINE_FEED).append(LINE_FEED);
        writer.close();

        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            outputStream.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }

        return response.toString();
    }
}