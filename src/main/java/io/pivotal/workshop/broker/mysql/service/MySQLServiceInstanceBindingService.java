package io.pivotal.workshop.broker.mysql.service;

import io.pivotal.workshop.broker.mysql.model.ServiceInstanceBinding;
import io.pivotal.workshop.broker.mysql.repository.MySQLServiceInstanceBindingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by df on 16-10-25.
 */
@Service
public class MySQLServiceInstanceBindingService implements ServiceInstanceBindingService {

    @Autowired
    private MySQLServiceInstanceBindingRepository bindingRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${mysql.hostname}")
    private String hostname;

    @Value("${mysql.port}")
    private String port;

    @Override
    public CreateServiceInstanceBindingResponse createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
        System.out.println("create service broker... start ");

        String bindingId = request.getBindingId();
        String serviceInstanceId = request.getServiceInstanceId();

        ServiceInstanceBinding binding = bindingRepository.findOne(bindingId);
        if (binding != null) {
            throw new ServiceInstanceBindingExistsException(serviceInstanceId, bindingId);
        }

        String database = toDBName(serviceInstanceId);
        String username = "U"+(Math.random()+"").replace("\\.","").substring(0,15);
        String password = "P"+(Math.random()+"").replace("\\.","").substring(0,15);
        addUser(database, username, password);

        Map<String,Object> credentials = getCredentials(database,username,password);
        binding = new ServiceInstanceBinding(bindingId, serviceInstanceId, credentials, null, request.getBoundAppGuid());
        bindingRepository.save(binding);
        System.out.println("create service broker... end ");
        return new CreateServiceInstanceAppBindingResponse().withCredentials(credentials);
    }

    private Map<String,Object> getCredentials(String database, String username, String password){
        Map<String, Object> credentials = new HashMap();
        credentials.put("hostname",hostname);
        credentials.put("jdbcUrl","jdbc:mysql://"+hostname+":"+port+"/"+database+"?user="+username+"&password="+password);
        credentials.put("name",database);
        credentials.put("username",username);
        credentials.put("password",password);
        credentials.put("port",port);
        credentials.put("uri","mysql://"+hostname+":"+port+"/"+database+"?reconnect=true");
        return credentials;
    }

    private String toDBName(String instanceId){
        return "db"+instanceId.replaceAll("-","_");
    }

    @Override
    public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
        String bindingId = request.getBindingId();
        ServiceInstanceBinding binding = getServiceInstanceBinding(bindingId);

        if (binding == null) {
            throw new ServiceInstanceBindingDoesNotExistException(bindingId);
        }

        jdbcTemplate.execute("DROP USER '"+binding.getCredentials().get("username")+ "'");
        bindingRepository.delete(bindingId);
    }

    protected ServiceInstanceBinding getServiceInstanceBinding(String id) {
        return bindingRepository.findOne(id);
    }

    public void addUser(String database,String username, String password) {
        jdbcTemplate.execute("CREATE USER '" + username + "' IDENTIFIED BY '" + password + "'");
        jdbcTemplate.execute("GRANT ALL PRIVILEGES ON `" + database+ "`.* TO '" + username + "'@'%'");
        jdbcTemplate.execute("FLUSH PRIVILEGES");
    }


}
