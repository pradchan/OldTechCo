package com.oracle.cloud.demo.oe.rest;
 
import java.io.File;
import java.security.SecureRandom;
 
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.MediaType;
 
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
 
public class JerseyClientPost {
 
    public static void main(String[] args) {
        String proxyHost = "";
        String proxyPort = "";
        
 
        System.out.println("");
 
        System.out
                .println("[INFO] ------------------------------------------------------------------------");
        System.out
                .println("[INFO] STARTING MAVEN JERSEY REST CLIENT WLS DEPLOYMENT");
        System.out
                .println("[INFO] ------------------------------------------------------------------------");
        System.out.println("");
 
        String s = System.getenv("HTTP_PROXY");
        if (s != null) {
            proxyHost = s.substring(s.indexOf("//") + 2,
                    s.indexOf(":", s.indexOf(":") + 1));
            proxyPort = s.substring(s.indexOf(":", s.indexOf(":") + 1) + 1,
                    s.length());
 
            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", proxyPort);
            System.setProperty("https.proxyHost", proxyHost);
            System.setProperty("https.proxyPort", proxyPort);
        }
 
        String username = args[0];
        String password = args[1];
        String restURL = args[2];
        String deploymentPath = args[3];
        String stringModel = args[4];
 
        System.out.println("JVM Arguments:");
        String javaHome = System.getenv("JAVA_HOME");
        System.out.println("Jersey Client JAVA_HOME=" + javaHome);
 
        if (System.getProperty("http.proxyHost") != null
                && System.getProperty("http.proxyPort") != null
                && System.getProperty("https.proxyHost") != null
                && System.getProperty("https.proxyPort") != null) {
            System.out.println("JVM Proxy Configurations = HTTP.ProxyHost:"
                    + System.getProperty("http.proxyHost").toString()
                    + ", HTTP.ProxyPort:"
                    + System.getProperty("http.proxyPort").toString()
                    + ", HTTPS.ProxyHost:"
                    + System.getProperty("https.proxyHost").toString()
                    + ", HTTPS.ProxyPort:"
                    + System.getProperty("https.proxyPort").toString());
        }
 
        System.out.println("");
        System.out.println("Jersey Client Arguments:");
        System.out.println("http(s)ProxyHost=" + proxyHost);
        System.out.println("http(s)ProxyPort=" + proxyPort);
        System.out.println("Weblogic Username=" + username);
        System.out.println("Weblogic Password=" + "*******");
        System.out.println("Weblogic REST endpoint=" + restURL);
        System.out.println("Deployment File=" + deploymentPath);
        System.out.println("Model=" + stringModel);
        System.out.println("");
 
        try {
 
            String model = stringModel;
 
            Client client = Client.create(configureClient());
 
            WebResource webResource = client.resource(restURL);
 
            client.addFilter(new HTTPBasicAuthFilter(username, password));
            webResource.header("x-requested-by", "WLSMavenDeployClient");
            webResource.header("accept", "application/json");
            webResource.header("content-type", "multipart/form-data");
 
            FileDataBodyPart deployment = new FileDataBodyPart("deployment",
                    new File(deploymentPath),
                    MediaType.APPLICATION_OCTET_STREAM_TYPE);
 
            FormDataMultiPart multiPart = new FormDataMultiPart();
            multiPart.bodyPart(deployment);
            multiPart.field("model", model, MediaType.APPLICATION_JSON_TYPE);
 
            ClientResponse response = webResource.type(
                    MediaType.MULTIPART_FORM_DATA_TYPE).post(
                    ClientResponse.class, multiPart);
 
            System.out.println("\n");
            System.out
                    .println("[INFO] ------------------------------------------------------------------------");
            System.out.println("[INFO] Response from WLS Server:");
            System.out
                    .println("[INFO] ------------------------------------------------------------------------");
            System.out.println("");
            System.out.println("response2 : " + response.toString());
            String output = response.getEntity(String.class);
            System.out.println(output);
            System.out
                    .println("[INFO] ------------------------------------------------------------------------");
            System.out.println("");
 
            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }
 
        } catch (Exception e) {
 
            e.printStackTrace();
 
        }
 
    }
 
    public static ClientConfig configureClient() {
        System.setProperty("jsse.enableSNIExtension", "false");
        try {
            trustAllHttpsCertificates();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
 
        SSLContext ctx = null;
        javax.net.ssl.TrustManager[] trustAllCerts =
 
        new javax.net.ssl.TrustManager[1];
 
        javax.net.ssl.TrustManager tm = new miTM();
 
        trustAllCerts[0] = tm;
 
        javax.net.ssl.SSLContext sc;
        try {
            sc = javax.net.ssl.SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, null);
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(
 
            sc.getSocketFactory());
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
 
        try {
            ctx = SSLContext.getInstance("SSL");
            ctx.init(null, trustAllCerts, new SecureRandom());
        } catch (java.security.GeneralSecurityException ex) {
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        try {
            trustAllHttpsCertificates();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        ClientConfig config = new DefaultClientConfig();
        try {
            config.getProperties().put(
                    HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
                    new HTTPSProperties(new HostnameVerifier() {
 
                        @Override
                        public boolean verify(String hostname,
                                SSLSession session) {
                            // TODO Auto-generated method stub
                            return true;
                        }
                    }, ctx));
 
        } catch (Exception e) {
        }
        return config;
    }
 
    private static void trustAllHttpsCertificates() throws Exception {
 
        // Create a trust manager that does not validate certificate chains:
        System.out
                .println("\n[WARNING] SSL INSECURE: Trusting all https certificates\n");
 
        javax.net.ssl.TrustManager[] trustAllCerts =
 
        new javax.net.ssl.TrustManager[1];
 
        javax.net.ssl.TrustManager tm = new miTM();
 
        trustAllCerts[0] = tm;
 
        javax.net.ssl.SSLContext sc =
 
        javax.net.ssl.SSLContext.getInstance("SSL");
 
        sc.init(null, trustAllCerts, null);
 
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(
 
        sc.getSocketFactory());
 
    }
 
    public static class miTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
 
        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }
 
        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }
 
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
 
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }
 
}