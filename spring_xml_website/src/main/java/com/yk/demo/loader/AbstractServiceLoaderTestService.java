package com.yk.demo.loader;

/**
 * 测试使用ServiceLoader 加载抽象类的子类
 */
public abstract class AbstractServiceLoaderTestService implements IServiceLoaderTestService {

    protected abstract void onLoader();

    public void running() {

    }
}
