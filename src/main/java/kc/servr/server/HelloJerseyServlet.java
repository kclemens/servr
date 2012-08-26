package kc.servr.server;

import javax.ws.rs.*;

/**
 * User: kclemens
 * Date: 8/17/12
 */
@Path("/hello/")
public class HelloJerseyServlet {
    @GET
    public String getHello() {
        return "hello";
    }

    @PUT
    public String putHello(String putData) {
        return putData;
    }

    @POST
    public String postHello(String postData) {
        return postData;
    }

    @DELETE
    public String deleteHello() {
        return "deleted";
    }
}
