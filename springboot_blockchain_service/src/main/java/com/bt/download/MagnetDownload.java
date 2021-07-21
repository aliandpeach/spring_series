//package com.bt.download;
//
//import bt.Bt;
//import bt.data.Storage;
//import bt.data.file.FileSystemStorage;
//import bt.dht.DHTConfig;
//import bt.dht.DHTModule;
//import bt.runtime.BtClient;
//import bt.runtime.Config;
//import com.google.inject.Module;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
///**
// * 描述
// *
// * @author yangk
// * @version 1.0
// * @since 2021/07/12 15:31:51
// */
//public class MagnetDownload
//{
//    public static void main(String[] args)
//    {
//        System.setProperty("log.home", System.getProperty("user.dir"));
//        String magnetAddr = "magnet:?xt=urn:btih:40f6d6c8992e1bd27efa6758315e055ac6d2d5d9";
//        // enable multithreaded verification of torrent data
//        Config config = new Config()
//        {
//            @Override
//            public int getNumOfHashingThreads()
//            {
//                int count = Runtime.getRuntime().availableProcessors() * 2;
//                return 2;
//            }
//        };
//
//        // enable bootstrapping from public routers
//        Module dhtModule = new DHTModule(new DHTConfig()
//        {
//            @Override
//            public boolean shouldUseRouterBootstrap()
//            {
//                return true;
//            }
//        });
//
//        // get download directory
//        Path targetDirectory = Paths.get("D:\\");
//
//        // create file system based backend for torrent data
//        Storage storage = new FileSystemStorage(targetDirectory);
//
//        // create client with a private runtime
//        BtClient client = Bt.client()
//                .config(config)
//                .storage(storage)
//                .magnet(magnetAddr)
//                .autoLoadModules()
//                .module(dhtModule)
//                .stopWhenDownloaded()
//                .build();
//
//        // launch
//        client.startAsync().join();
//    }
//}
