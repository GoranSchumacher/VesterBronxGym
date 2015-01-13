package controllers;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.F;
import play.libs.XPath;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
    private static String PAYEX_CURRENCY = PAYEX_CONFIGURATION.getString("currency");
    private static String PAYEX_INITIALIZE_RETURNURL = PAYEX_CONFIGURATION.getString("initialize_returnurl");
    private static String PAYEX_INITIALIZE_CANCELURL = PAYEX_CONFIGURATION.getString("initialize_cancelurl");
    private static String PAYEX_VIEW = PAYEX_CONFIGURATION.getString("view");
    private static String PAYEX_CLIENTLANGUAGE = PAYEX_CONFIGURATION.getString("client_language");

    public static F.Promise<Result> CreateAgreement3(String description) {
        F.Promise<Node> documentPromise = getCreateAgreement3AsDocumentPromise(description);
        F.Promise<Result> promiseOfResult = getResultPromiseFromDocumentPromise(documentPromise);
        return  promiseOfResult;
    }

    private static F.Promise<Node> getCreateAgreement3AsDocumentPromise(String description) {
        WSRequestHolder holder = WS.url(PAYEX_TEST_BASE_URL + "/pxagreement/pxagreement.asmx/CreateAgreement3")
        .setTimeout(10000)
        .setContentType("application/x-www-form-urlencoded");

        String hash = getHash(PAYEX_ACCOUNTNO+PAYEX_MERCHANTREF+description+PAYEX_PURCHASE_OPERATION+PAYEX_MAXAMOUNT, PAYEX_ENCRYPTIONKEY);

        StringBuffer body = new StringBuffer();
        body.append("accountNumber="+ PAYEX_ACCOUNTNO);
        body.append("&merchantRef="+ PAYEX_MERCHANTREF);
        body.append("&description="+ description);
        body.append("&purchaseOperation="+ PAYEX_PURCHASE_OPERATION);
        body.append("&maxAmount="+ PAYEX_MAXAMOUNT);
        body.append("&notifyUrl="+ "");
        body.append("&startDate="+ "");
        body.append("&stopDate="+ "");
        body.append("&hash="+ hash);

        return getDocumentPromiseFromWSPost(holder, body);
    }

    public static F.Promise<Result> createAgreement3ANDInitialize8(Long price, Integer vat, String orderID,
            String productNumber, String description) {

        String clientIPAddress = request().remoteAddress();
        Logger.debug("clientIPAddress: " +clientIPAddress);

        F.Promise<Node> createAgreement3DocumentPromise = getCreateAgreement3AsDocumentPromise(description);

        Document doc2 = getDocument(createAgreement3DocumentPromise, 10000);

        String agreementRef = XPath.selectNode("payex//agreementRef", doc2).getTextContent();
        Logger.debug("agreementRef from call createAgreement3: " + agreementRef);


        F.Promise<Node> documentPromise = getInitialize8AsDocumentPromise(price, vat, orderID,
                productNumber, description, clientIPAddress, agreementRef);
        F.Promise<Result> promiseOfResult = getResultPromiseFromDocumentPromise(documentPromise);

        // Here we should redirect

        return  promiseOfResult;
    }

    private static F.Promise<Node> getInitialize8AsDocumentPromise(Long price, Integer vat, String orderID,
                   String productNumber, String description, String clientIPAddress, String agreementRef) {
        WSRequestHolder holder = WS.url(PAYEX_TEST_BASE_URL + "/pxorder/pxorder.asmx/Initialize8")
                .setTimeout(10000)
                .setContentType("application/x-www-form-urlencoded");

        // accountNumber + purchaseOperation + price + priceArgList + currency + vat + orderID + productNumber +
        // description + clientIPAddress + clientIdentifier + additionalValues + externalID + returnUrl + view + agreementRef + cancelUrl + clientLanguageAll
        String hash = getHash(PAYEX_ACCOUNTNO+PAYEX_PURCHASE_OPERATION+price+"" + PAYEX_CURRENCY + vat + orderID + productNumber +
                description + clientIPAddress + "" + "" + "" + PAYEX_INITIALIZE_RETURNURL + PAYEX_VIEW + agreementRef + PAYEX_INITIALIZE_CANCELURL + PAYEX_CLIENTLANGUAGE, PAYEX_ENCRYPTIONKEY);

        StringBuffer body = new StringBuffer();
        body.append("accountNumber="+ PAYEX_ACCOUNTNO);
        body.append("&purchaseOperation="+ PAYEX_PURCHASE_OPERATION);
        body.append("&price="+ price);
        body.append("&priceArgList="+ "");
        body.append("&currency="+ PAYEX_CURRENCY);
        body.append("&vat="+ vat);
        body.append("&orderID="+ orderID);
        body.append("&productNumber="+ productNumber);
        body.append("&description="+ description);
        body.append("&clientIPAddress="+ clientIPAddress);
        body.append("&clientIdentifier="+ "");
        body.append("&additionalValues="+ "");
        body.append("&externalID="+ "");
        body.append("&returnUrl="+ PAYEX_INITIALIZE_RETURNURL);
        body.append("&view="+ PAYEX_VIEW);
        body.append("&agreementRef="+ agreementRef);
        body.append("&cancelUrl="+ PAYEX_INITIALIZE_CANCELURL);
        body.append("&clientLanguage="+ PAYEX_CLIENTLANGUAGE);
        body.append("&hash="+ hash);

        return getDocumentPromiseFromWSPost(holder, body);
    }

    //////////////////// GENERAL UTILITIES ////////////////////

    private static Document getDocument(F.Promise<Node> promiseNode, long timeout) {
        Node dom = promiseNode.get(timeout);
        Document doc2 = null;
        try {
            doc2 = parseStringToXMLDocument(dom.getTextContent());
        } catch(Exception e) {

        }
        return doc2;
    }

    private static F.Promise<Node> getDocumentPromiseFromWSPost(WSRequestHolder complexHolder, StringBuffer body) {
        return complexHolder.post(body.toString()).map(
                    new F.Function<WSResponse, Node>() {
                        public Node apply(WSResponse response) {
                            Logger.debug(response.getBody().toString());
                            Document xml = response.asXml();
                            Node xml2 = xml.getChildNodes().item(0).getChildNodes().item(0);
                            return xml2;
                        }
                    }
            );
    }

    private static F.Promise<Result> getResultPromiseFromDocumentPromise(F.Promise<Node> documentPromise) {
        return documentPromise.map(
                    new F.Function<Node,Result>() {
                        public Result apply(Node doc1) {
                            String s = doc1.getTextContent();
                            Document doc2 = null;
                            try {
                                doc2 = parseStringToXMLDocument(s);
                            } catch (Exception e) {

                            }
                            try {
                                return ok(getStringFromDoc(doc2));
                            } catch(TransformerException e) {
                                return internalServerError(e.getMessage());
                            }
                        }
                    }
            );
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

    public static String getStringFromDoc(Node doc) throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(domSource, result);
        writer.flush();
        return writer.toString();
    }
}
