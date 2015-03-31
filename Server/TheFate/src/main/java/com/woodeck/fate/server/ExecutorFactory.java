/**
 * 
 */
package com.woodeck.fate.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.electrotank.electroserver5.extensions.BaseManagedObjectFactory;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;

/**
 * @author Killua
 * This class is responsible for creating a second thread pool for accessing the database.
 * With this second thread pool, the probability of our Electroserver be more responsive
 * during heavy usage of database access is increased.
 */
public class ExecutorFactory extends BaseManagedObjectFactory {
	
	public static final String FACTORY_NAME = "ExecutorFactory";
	private ExecutorService executor;
	
    @Override
    public void init(EsObjectRO parameters) {
        executor = new ThreadPoolExecutor(
        		parameters.getInteger("corePoolSize", 10),	// Get the parameter from Extension.xml file
        		parameters.getInteger("maxPoolSize", 20),
        		parameters.getInteger("keepAliveTime", 60),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactory() {
        			AtomicInteger threadNumber = new AtomicInteger(1);
                    @Override
                    public Thread newThread(Runnable r) {
                    	return new Thread(r, "SecondThreadPool " + threadNumber.getAndIncrement());
                    }
                });
    }
 
    @Override
    public ExecutorService acquireObject(EsObjectRO parameters) {
        return executor;
    }
 
    @Override
    public void destroy() {
        executor.shutdown();
        super.destroy();
    }
    
}