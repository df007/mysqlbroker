package io.pivotal.workshop.broker.mysql.service;

import io.pivotal.workshop.broker.mysql.model.ServiceInstance;
import io.pivotal.workshop.broker.mysql.repository.MySQLServiceInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.model.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by df on 16-10-24.
 */
@Service
public class MySQLServiceInstanceService implements ServiceInstanceService {

    @Autowired
    private MySQLServiceInstanceRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {
        ServiceInstance instance = repository.findOne(request.getServiceInstanceId());
        if (instance != null) {
            throw new ServiceInstanceExistsException(request.getServiceInstanceId(), request.getServiceDefinitionId());
        }

        instance = new ServiceInstance(request);

        jdbcTemplate.execute("create database "+toDBName(instance.getServiceInstanceId()));
//        jdbcTemplate.execute("GRANT ALL PRIVILEGES ON `" + toDBName(instance.getServiceInstanceId())+ "`.* TO 'root'@'%'");
//        jdbcTemplate.execute("GRANT ALL PRIVILEGES ON `" + toDBName(instance.getServiceInstanceId())+ "`.* TO 'root'@'%' ");
//        jdbcTemplate.execute("FLUSH PRIVILEGES");

        repository.save(instance);

        return new CreateServiceInstanceResponse();
    }

    private String toDBName(String instanceId){
        return "db"+instanceId.replaceAll("-","_");
    }

    @Override
    public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest request) {
        return new GetLastServiceOperationResponse().withOperationState(OperationState.SUCCEEDED);
    }

    public ServiceInstance getServiceInstance(String id) {
        return repository.findOne(id);
    }

    @Override
    public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) {
        String instanceId = request.getServiceInstanceId();
        ServiceInstance instance = repository.findOne(instanceId);
        if (instance == null) {
            throw new ServiceInstanceDoesNotExistException(instanceId);
        }

        jdbcTemplate.execute("drop database "+toDBName(instance.getServiceInstanceId()));

        repository.delete(instanceId);
        return new DeleteServiceInstanceResponse();
    }

    @Override
    public UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest request) {
        String instanceId = request.getServiceInstanceId();
        ServiceInstance instance = repository.findOne(instanceId);
        if (instance == null) {
            throw new ServiceInstanceDoesNotExistException(instanceId);
        }

        repository.delete(instanceId);
        ServiceInstance updatedInstance = new ServiceInstance(request);
        repository.save(updatedInstance);
        return new UpdateServiceInstanceResponse();
    }

}
