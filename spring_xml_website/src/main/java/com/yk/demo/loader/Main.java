package com.yk.demo.loader;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.ServiceLoader;

public class Main {
    public static void main(String[] args) {
        ServiceLoader<AbstractServiceLoaderTestService> loader = ServiceLoader.<AbstractServiceLoaderTestService>load(AbstractServiceLoaderTestService.class);
        Iterator<AbstractServiceLoaderTestService> iterator = loader.iterator();
        while (iterator.hasNext()) {
            AbstractServiceLoaderTestService service = iterator.next();
            if (service.getClass().isInterface() || Modifier.isAbstract(service.getClass().getModifiers())) {
                continue;
            }
            AbstractServiceLoaderTestService run = service;
            run.running();
        }
    }
}
