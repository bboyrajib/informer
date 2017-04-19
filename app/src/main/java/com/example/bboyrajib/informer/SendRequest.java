package com.example.bboyrajib.informer;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bboyrajib on 12/04/17.
 */

public class SendRequest extends StringRequest {


    private static final String SEND_REQUEST_URL = "http://eurus.96.lt/InformerSendMessage.php";
    private Map<String, String> params;

    public SendRequest(String topic, String email,String password, Response.Listener<String> listener) {
        super(Method.POST, SEND_REQUEST_URL, listener, null);
        params = new HashMap<>();

        params.put("topic",topic);
        params.put("title", email);
        params.put("message", password);


    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
