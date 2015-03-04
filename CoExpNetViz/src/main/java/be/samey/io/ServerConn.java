package be.samey.io;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;



/**
 *
 * @author sam
 */
public class ServerConn {

    public void connect() {
        CloseableHttpClient httpclient = HttpClients.createDefault();

    }
}
