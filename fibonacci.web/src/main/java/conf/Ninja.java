package conf;

import com.google.gson.Gson;
import model.HAProxyConfig;
import model.Server;
import ninja.metrics.InstrumentedNinja;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ssa on 2015-10-09 12:08
 */
public class Ninja extends InstrumentedNinja {

    private static final Logger LOG = LoggerFactory.getLogger(Ninja.class);

    @Override
    public void onFrameworkStart() {
        super.onFrameworkStart();
        registerOnHAProxy();
    }

    public HttpClient createHttpClient(){
        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder = requestBuilder.setConnectTimeout(500);
        requestBuilder = requestBuilder.setConnectionRequestTimeout(500);
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(requestBuilder.build());
        HttpClient client = builder.build();
        return client;
    }

    public String getMyPublicIp() throws Exception{
        try {
            HttpClient client = createHttpClient();
            HttpGet request = new HttpGet("http://169.254.169.254/latest/meta-data/public-ipv4");
            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String rawIp = rd.readLine();
            LOG.info("public ip: {}", rawIp);
            String parsedIp = parsePublicIp(rawIp);
            LOG.info("parsed ip: {}", parsedIp);
            return parsedIp;
        }catch (Exception e){
            LOG.error("Error while getting public ip", e);
        }
        return null;


    }

    //parse public
    //http://169.254.169.254/latest/meta-data/public-ipv4
    //52.19.198.178ubuntu@ip-172-31-7-129
    public static String parsePublicIp(String rawString){
        Pattern r = Pattern.compile("^(\\d+.\\d+.\\d+.\\d+).*");
        Matcher m = r.matcher(rawString);
        if(m.find()){
            return m.group(1);
        }
        return null;

    }

    public void registerOnHAProxy(){
        try {
            String myPublicIp = getMyPublicIp();
            LOG.info("My public API is {}", myPublicIp);
            HAProxyConfig haProxyConfig = getConfig();
            LOG.info("haProxyConfig: {}", haProxyConfig);

            Server server = new Server();
            LOG.info("Creating server: ", server);
            server.setPort(Integer.valueOf(System.getProperty("ninja.port", "9000")));
            server.setHost(myPublicIp);
            server.setName(("name_" + myPublicIp).replace('.','_'));
            LOG.info("adding server: {} ", server);

            haProxyConfig.getBackends().get(0).getServers().add(server);
            setConfig(haProxyConfig);
        }catch (Exception e){
            LOG.error("Error while registering to HA proxy", e);
        }
    }

    public void setConfig(HAProxyConfig haProxyConfig ) throws Exception{
        LOG.info("Setting config...");
        HttpClient client = createHttpClient();
        HttpPost post = new HttpPost("http://52.19.198.178:10001/v1/config");
        post.addHeader("Content-Type", "application/json");
        String json = new Gson().toJson(haProxyConfig);
        LOG.info("Sending new conf: {}", json);

        HttpEntity entity = new ByteArrayEntity(json.getBytes("UTF-8"));
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        String result = EntityUtils.toString(response.getEntity());
        LOG.info("Setting config response:[{}], body:[{}]",response.getStatusLine().getStatusCode(), result);

    }

    public HAProxyConfig getConfig() throws Exception{
        HttpClient client = createHttpClient();
        HttpGet request = new HttpGet("http://52.19.198.178:10001/v1/config");
        request.addHeader("Content-Type", "application/json");
        HttpResponse response = client.execute(request);

        BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while((line = br.readLine())!= null){
            stringBuilder.append(line);
        }
        LOG.info("Raw configuration is: {}", stringBuilder);
        return new Gson().fromJson(stringBuilder.toString(),HAProxyConfig.class );
    }
}
