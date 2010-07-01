
/*
 * 
 */

package org.apromore.data_access.service_manager;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.2.7
 * Thu Jul 01 17:18:17 EST 2010
 * Generated source version: 2.2.7
 * 
 */


@WebServiceClient(name = "DAManagerService", 
                  wsdlLocation = "http://localhost:8080/Apromore-dataAccess/services/DAManager?wsdl",
                  targetNamespace = "http://www.apromore.org/data_access/service_manager") 
public class DAManagerService extends Service {

    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("http://www.apromore.org/data_access/service_manager", "DAManagerService");
    public final static QName DAManager = new QName("http://www.apromore.org/data_access/service_manager", "DAManager");
    static {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/Apromore-dataAccess/services/DAManager?wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from http://localhost:8080/Apromore-dataAccess/services/DAManager?wsdl");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public DAManagerService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public DAManagerService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public DAManagerService() {
        super(WSDL_LOCATION, SERVICE);
    }

    /**
     * 
     * @return
     *     returns DAManagerPortType
     */
    @WebEndpoint(name = "DAManager")
    public DAManagerPortType getDAManager() {
        return super.getPort(DAManager, DAManagerPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns DAManagerPortType
     */
    @WebEndpoint(name = "DAManager")
    public DAManagerPortType getDAManager(WebServiceFeature... features) {
        return super.getPort(DAManager, DAManagerPortType.class, features);
    }

}
