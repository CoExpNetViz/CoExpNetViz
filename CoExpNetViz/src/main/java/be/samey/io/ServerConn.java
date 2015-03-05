package be.samey.io;

import be.samey.model.Model;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author sam
 */
public class ServerConn {

    private Model model;

    public ServerConn(Model model) {
        this.model = model;
    }

    public void connect() throws InterruptedException {
        String url = Model.URL;
        //TODO: get info from coreStatus
        // ...

        //TODO: build useragent
        // ...
        CloseableHttpClient httpclient = HttpClients.createDefault();

        //TODO: run the app on the server from here
        Thread.sleep(1000); //simulate networking time

        //in the finished app this info should come from files downloaded from the server
        Path sifPath = Paths.get("/home/sam/favs/uma1_s2-mp2-data/CexpNetViz_web-interface/out/network/network1.sif");
        Path noaPath = Paths.get("/home/sam/favs/uma1_s2-mp2-data/CexpNetViz_web-interface/out/network/network1.node.attr");
        Path edaPath = Paths.get("/home/sam/favs/uma1_s2-mp2-data/CexpNetViz_web-interface/out/network/network1.edge.attr");
        Path vizPath = Paths.get("/home/sam/favs/uma1_s2-mp2-data/CexpNetViz_web-interface/out/network/CevStyle.xml");

        model.getCoreStatus().setSifPath(sifPath);
        model.getCoreStatus().setNoaPath(noaPath);
        model.getCoreStatus().setEdaPath(edaPath);
        model.getCoreStatus().setVizPath(vizPath);

    }
}
