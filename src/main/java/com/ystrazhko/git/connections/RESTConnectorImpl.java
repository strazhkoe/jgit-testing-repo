package com.ystrazhko.git.connections;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.ystrazhko.git.util.RequestType;

class RESTConnectorImpl implements RESTConnector {

    @Override
    public Object sendPost(String suffixForUrl, Map<String, String> params, Map<String, String> header) {
        return sendRequest(suffixForUrl, params,header, RequestType.POST);
    }

    @Override
    public Object sendGet(String suffixForUrl, Map<String, String> params, Map<String, String> header) {
        return sendRequest(suffixForUrl, params,header, RequestType.GET);
    }

    /**
     * Sends request
     *
     * @param suffixForUrl suffix for adding to main URL
     * @param params for request
     * @param header the data to be added to header of request.
     *               if the header is not needed to pass null
     * @param reguest - for example: RequestType.GET or RequestType.POST etc.
     *
     * @return string with data or null, if an error occurred in the request
     */
    private Object sendRequest(String suffixForUrl, Map<String, String> params, Map<String, String> header, RequestType reguest) {
        try {
            URL obj = new URL(URL_MAIN_PART + suffixForUrl);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            // add request header
            if (header != null) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    con.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            con.setRequestMethod(reguest.toString());

            if (params != null) {
                String urlParameters = formParameters(params);
                // Send post request
                con.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
            }

            int responseCode = con.getResponseCode();
            System.out.println("\nSending '" + reguest +"' request to URL : " + obj.toString());
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer response = new StringBuffer();

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String formParameters(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?,?> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                urlEncodeUTF8(entry.getKey().toString()),
                urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return sb.toString();
    }

    private Object urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
