package controllers;

import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;
import models.User;
import org.apache.commons.codec.binary.Hex;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import play.Configuration;
import play.Play;
import play.Routes;
import play.api.*;
import play.data.Form;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import play.mvc.*;
import play.mvc.Http.Response;
import play.mvc.Http.Session;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;

import views.html.*;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.user.AuthUser;
import play.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author Gøran Schumacher (GS) / Schumacher Consulting Aps
 * @version $Revision$ 12/01/15
 */
public class PayEx extends Controller {

    private static Configuration PAYEX_CONFIGURATION = Play.application().configuration().getConfig("payex");
    private static String PAYEX_ACCOUNTNO = PAYEX_CONFIGURATION.getString("accountNumber");
    private static String PAYEX_MERCHANTREF = PAYEX_CONFIGURATION.getString("merchantRef");
    private static String PAYEX_PURCHASE_OPERATION = PAYEX_CONFIGURATION.getString("purchaseOperation");
    private static String PAYEX_MAXAMOUNT = PAYEX_CONFIGURATION.getString("maxAmount");
    private static String PAYEX_ENCRYPTIONKEY = PAYEX_CONFIGURATION.getString("encryptionKey");

    private static String PAYEX_TEST_BASE_URL = "https://test-external.payex.com";

    public static F.Promise<Result> CreateAgreement3(String description) {
        WSRequestHolder holder = WS.url(PAYEX_TEST_BASE_URL + "/pxagreement/pxagreement.asmx/CreateAgreement3");

//        String hash = "";
//        String hexHash = "";
//        try {
//            MessageDigest md5Hash = MessageDigest.getInstance("MD5");
//            // accountNumber + merchantRef + description + purchaseOperation + maxAmount + notifyUrl + startDate + stopDate + encryptionKey
//            md5Hash.update(PAYEX_ACCOUNTNO.getBytes());
//            md5Hash.update(PAYEX_MERCHANTREF.getBytes());
//            md5Hash.update(description.getBytes());
//            md5Hash.update(PAYEX_PURCHASE_OPERATION.getBytes());
//            md5Hash.update(PAYEX_MAXAMOUNT.getBytes());
////            md5Hash.update("".getBytes());
////            md5Hash.update("".getBytes());
////            md5Hash.update("".getBytes());
//            md5Hash.update(PAYEX_ENCRYPTIONKEY.getBytes());
//            hash = md5Hash.digest().toString();
//            hexHash = Hex.encodeHexString(hash.getBytes());
//            Logger.debug("Hash:" + hash);
//            Logger.debug("hexHash:" + hexHash);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }

        String hash2 = getHash(PAYEX_ACCOUNTNO+PAYEX_MERCHANTREF+description+PAYEX_PURCHASE_OPERATION+PAYEX_MAXAMOUNT, PAYEX_ENCRYPTIONKEY);
        Logger.debug("hash2:" + hash2);

        WSRequestHolder complexHolder = holder.setHeader("headerKey", "headerValue")
                .setTimeout(10000);
//                .setQueryParameter("accountNumber", PAYEX_ACCOUNTNO)
//                .setQueryParameter("merchantRef", PAYEX_MERCHANTREF)
//                .setQueryParameter("description", description)
//                .setQueryParameter("purchaseOperation", PAYEX_CONFIGURATION.getString("purchaseOperation"))
//                .setQueryParameter("maxAmount", PAYEX_CONFIGURATION.getString("maxAmount"))
//                .setQueryParameter("notifyUrl", "")             // Deprecated, set to blank according to documentation.
//                .setQueryParameter("startDate", "")             // Can be blank, set to blank according to documentation.
//                .setQueryParameter("stopDate", "")               // Can be blank, set to blank according to documentation.
//                .setQueryParameter("hash", hash2);

        StringBuffer body = new StringBuffer();
        body.append("accountNumber="+ PAYEX_ACCOUNTNO);
        body.append("&merchantRef="+ PAYEX_MERCHANTREF);
        body.append("&description="+ description);
        body.append("&purchaseOperation="+ PAYEX_CONFIGURATION.getString("purchaseOperation"));
        body.append("&maxAmount="+ PAYEX_CONFIGURATION.getString("maxAmount"));
        body.append("&notifyUrl="+ "");
        body.append("&startDate="+ "");
        body.append("&stopDate="+ "");
        body.append("&hash="+ hash2);

        complexHolder.setContentType("application/x-www-form-urlencoded");

        F.Promise<Document> documentPromise = complexHolder.post(body.toString()).map(
                new F.Function<WSResponse, Document>() {
                    public Document apply(WSResponse response) {
                        Logger.debug(response.getBody().toString());
                        Document xml = response.asXml();
                        return xml;
                    }
                }
        );
        F.Promise<Result> promiseOfResult = documentPromise.map(
                new F.Function<Document,Result>() {
                    public Result apply(Document doc1) {
                        String s = doc1.getChildNodes().item(0).getTextContent();
                        Document doc2 = null;
                        try {
                            doc2 = parseStringToXMLDocument(s);
                            NodeList nodeList = doc2.getElementsByTagName("agreementRef");
                            Logger.debug("agreementRef: " + nodeList.item(0).getTextContent());
                        } catch (Exception e) {

                        }
                        //return ok(doc2.getTextContent());
                        return ok(s);
                    }
                }
        );

        /*
        F.Promise<String> documentPromise = complexHolder.post(body.toString()).map(
                new F.Function<WSResponse, String>() {
                    public String apply(WSResponse response) {
                        Logger.debug(response.getBody().toString());
                        return response.getBody().toString();
                    }
                }
        );
        F.Promise<Result> promiseOfResult = documentPromise.map(
                new F.Function<String,Result>() {
                    public Result apply(String doc) {
                        return ok(doc);
                    }
                }
        );*/

        return  promiseOfResult;
    }

    private static Document parseStringToXMLDocument(String xmlString) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlString)));
    }

    /**
     * Copied from PayEx example code
     * Generate hash-value
     * @param data The hash-input
     * @param key Encryption key
     * @return The hash-value
     */
    private static String getHash(String data, String key) {
        StringBuffer sb= new StringBuffer();
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(data.getBytes());
            byte result[] = md5.digest(key.getBytes());
            for (int i = 0; i < result.length; i++) {
                String s = Integer.toHexString(result[i]);
                int length = s.length();
                if (length >= 2) {
                    sb.append(s.substring(length - 2, length));
                } else {
                    sb.append("0");
                    sb.append(s);
                }
            }
            md5.reset();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
