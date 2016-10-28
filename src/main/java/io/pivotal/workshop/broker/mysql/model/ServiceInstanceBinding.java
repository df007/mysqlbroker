package io.pivotal.workshop.broker.mysql.model;

import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by df on 16-10-24.
 */

@Entity
public class ServiceInstanceBinding {

    @Id
    private String id;

    private String serviceInstanceId;

    @org.hibernate.annotations.Type(
            type = "org.hibernate.type.SerializableToBlobType",
            parameters = { @Parameter( name = "classname", value = "java.util.HashMap" )})
    private Map<String,Object> credentials = new HashMap<>();
    private String syslogDrainUrl;
    private String appGuid;

    @SuppressWarnings("unused")
    private ServiceInstanceBinding() {}


    public ServiceInstanceBinding(String id,
                                  String serviceInstanceId,
                                  Map<String,Object> credentials,
                                  String syslogDrainUrl, String appGuid) {
        this.id = id;
        this.serviceInstanceId = serviceInstanceId;
        setCredentials(credentials);
        this.syslogDrainUrl = syslogDrainUrl;
        this.appGuid = appGuid;
    }

    public String getId() {
        return id;
    }

    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    public Map<String, Object> getCredentials() {
        return credentials;
    }

    private void setCredentials(Map<String, Object> credentials) {
        if (credentials == null) {
            this.credentials = new HashMap<>();
        } else {
            this.credentials = credentials;
        }
    }

    public String getSyslogDrainUrl() {
        return syslogDrainUrl;
    }

    public String getAppGuid() {
        return appGuid;
    }

}
