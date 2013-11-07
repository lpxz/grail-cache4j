/* =========================================================================
 * File: CacheCleaner.java$
 *
 * Copyright (c) 2006, Yuriy Stepovoy. All rights reserved.
 * email: stepovoy@gmail.com
 *
 * =========================================================================
 */

package net.sf.cache4j;

import net.sf.cache4j.impl.BlockingCache;
import net.sf.cache4j.impl.CacheConfigImpl;


/**
 * ����� CacheCleaner ��������� ������� ���������� ��������
 */

public class CacheCleaner extends Thread {
// ----------------------------------------------------------------------------- ���������
// ----------------------------------------------------------------------------- �������� ������

    /**
     * �������� �������
     */
    private long _cleanInterval;

    /**
     * true ���� ����� ��������� � ������
     */
    private boolean _sleep = false;

// ----------------------------------------------------------------------------- ����������� ����������
// ----------------------------------------------------------------------------- ������������

    /**
     * �����������
     * @param cleanInterval ��������(� �������������) � ������� ����� ��������� �������
     */
    public CacheCleaner(long cleanInterval) {
        _cleanInterval = cleanInterval;

        setName(this.getClass().getName());
        setDaemon(true);
        //������������� ����������� ��������� �� ����� ������ ��� �������� ����������
        //�������� �� ����� ������ ������
        //setPriority(Thread.MIN_PRIORITY);
    }

// ----------------------------------------------------------------------------- Public ������

    /**
     * ������������� �������� �������
     * @param cleanInterval ��������(� �������������) � ������� ����� ��������� �������
     */
    public void setCleanInterval(long cleanInterval) {
        _cleanInterval = cleanInterval;

        synchronized(this){
            if(_sleep){
                interrupt();
            }
        }
    }

    /**
     * �������� �����. ��� ���� ����� ���������� ����� <code>clean</code>
     */
    public void run() {
    	int iterations=0;
        while(true)  {
        	iterations ++;
        	if(iterations>=2) break;
            try {
                CacheFactory cacheFactory = CacheFactory.getInstance();
                Object[] objIdArr = cacheFactory.getCacheIds();
                for (int i = 0, indx = objIdArr==null ? 0 : objIdArr.length; i<indx; i++) {
                    ManagedCache cache = (ManagedCache)cacheFactory.getCache(objIdArr[i]);
                    if(cache!=null){
                        cache.clean();
                    }
                    yield();
                }
            } catch (Throwable t){
                t.printStackTrace();
            }

            // synchronized(this) // grail.
           synchronized ("haha") // afix or axis
            { // 30059 vs 15 msec.
	            _sleep = true;
	            try {
	            	System.out.println(_cleanInterval);
	            	
	                sleep(_cleanInterval);
	            } catch (Throwable t){
	            } finally {
	                _sleep = false;
	            }
            }
        }
    }
    
    // each thread executes 100 iterations. 
    public static void main(String[] args) throws Exception
    {
    	int threadNo = Integer.parseInt(args[0]);
        CacheFactory cf = CacheFactory.getInstance();
        long ttl = 100;
        long cleanInterval = ttl*2;
        long sleep = ttl*3;

        BlockingCache cache = new BlockingCache();
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, ttl, 0, 0, 0, null, "lru", "strong");
        cache.setCacheConfig(cacheConfig);
        for (int i = 0; i <1000; i++) {
            cache.put(new Long(i), new Long(i));
        }
        cf.addCache(cache);
    	
        CacheCleaner[] threads = new CacheCleaner[threadNo];
        long start = System.currentTimeMillis();
        for(int i=0;i< threadNo ; i++)
        {
        	threads[i] = new CacheCleaner(2);
        	threads[i].start();
        }
//        for(int i=0;i< threadNo ; i++)
//        {
//        	threads[i].setCleanInterval(10);
//        }
        
        for(int i=0;i< threadNo ; i++)
        {
        	threads[i].join();
        }
        long end = System.currentTimeMillis();
        System.out.println("duration: " + (end-start));
        
        
    }

// ----------------------------------------------------------------------------- Package scope ������
// ----------------------------------------------------------------------------- Protected ������
// ----------------------------------------------------------------------------- Private ������
// ----------------------------------------------------------------------------- Inner ������

}

/*
$Log: CacheCleaner.java,v $
Revision 1.1  2010/06/18 17:01:12  smhuang
*** empty log message ***

*/
