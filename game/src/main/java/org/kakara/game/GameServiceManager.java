package org.kakara.game;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.kakara.core.common.ControllerKey;
import org.kakara.core.common.service.Service;
import org.kakara.core.common.service.ServiceManager;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class GameServiceManager implements ServiceManager {

    //TODO Allow multiple impls of the same service and ways of easily changing IMPL
    private final Set<Service> registeredServices = new HashSet<>();

    private final Multimap<ControllerKey, Consumer<Service>> keySetMap = ArrayListMultimap.create();
    private final Multimap<Class<? extends Service>, Consumer<Service>> serviceMap = ArrayListMultimap.create();

    @Override
    public Optional<Service> getService(ControllerKey controllerKey) {
        return registeredServices.stream().filter(service -> {
            return service.getControllerKey().equals(controllerKey);
        }).findFirst();
    }

    @Override
    public Optional<Service> getService(Class<? extends Service> serviceToFind) {
        return registeredServices.stream().filter(service -> {
            //I think?
            return service.getClass().isAssignableFrom(serviceToFind);
        }).findFirst();
    }

    @Override
    public <T extends Service> void executeOnceServiceIsFound(Consumer<T> service, Class<? extends Service> serviceToFind) {
        Optional<Service> service1 = getService(serviceToFind);
        if (service1.isPresent()) {
            service.accept((T) service1.get());
            return;
        }
        serviceMap.put(serviceToFind, (Consumer<Service>)service);
    }

    @Override
    public <T extends Service> void executeOnceServiceIsFound(Consumer<T> service, ControllerKey controllerKey) {
        Optional<Service> service1 = getService(controllerKey);
        if (service1.isPresent()) {
            service.accept((T) service1.get());
            return;
        }
        keySetMap.put(controllerKey, (Consumer<Service>) service);
    }


    @Override
    public void registerService(Service service) {
        registeredServices.add(service);
        checkForServices(service);
    }

    private <T extends Service> void checkForServices(T service) {
        keySetMap.get(service.getControllerKey()).forEach(serviceConsumer -> {
            serviceConsumer.accept(service);
        });
        keySetMap.removeAll(service.getControllerKey());

        serviceMap.get(service.getServiceClass()).forEach(serviceConsumer -> {
            serviceConsumer.accept(service);
        });
        serviceMap.removeAll(service.getServiceClass());

    }
}
