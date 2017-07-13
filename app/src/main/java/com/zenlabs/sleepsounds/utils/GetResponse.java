package com.zenlabs.sleepsounds.utils;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by fedoro on 5/12/16.
 */
public class GetResponse {

    public GetResponse() {

    }

    @SuppressWarnings({ "unchecked", "serial", "rawtypes" })
    public void SendEmail(final String email) throws Throwable {

        URL api_url = new URL("http://api2.getresponse.com");

        JSONRPC2Session client = new JSONRPC2Session(api_url);
        client.getOptions().setRequestContentType("application/json");

        LogService.Log("SendEmail", "-client-: " + client);

        JSONRPC2Response campaigns = client.send(new JSONRPC2Request(
                "get_campaigns", Arrays.asList(new Object[]{
                "39a72bb775e621eca16c54c7a132a61e",
                new Hashtable<String, Map>() {
                    {
                        put("name", new Hashtable<String, String>() {
                            {
                                put("EQUALS", "zenlabsllc");
                            }
                        });
                    }
                }}), 1));

        LogService.Log("SendEmail", "-campaigns-: "+campaigns);

        final String CAMPAIGN_ID = ((HashMap<String, Map>) campaigns
                .getResult()).keySet().iterator().next();

        JSONRPC2Response result = client.send(new JSONRPC2Request(
                "add_contact", Arrays.asList(new Object[]{
                "39a72bb775e621eca16c54c7a132a61e",
                new Hashtable<String, Object>() {
                    {
                        put("campaign", CAMPAIGN_ID);
                        put("email", email);
                        put("cycle_day", 0);
                    }
                }}), 2));

        LogService.Log("SendEmail", "-result-: "+result);

    }
}
