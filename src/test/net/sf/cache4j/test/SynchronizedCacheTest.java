/* =========================================================================
 * File: $Id: SynchronizedCacheTest.java,v 1.1 2010/06/18 17:01:16 smhuang Exp $SynchronizedCacheTest.java,v$
 *
 * Copyright 2006 by Yuriy Stepovoy.
 * email: stepovoy@gmail.com
 * All rights reserved.
 *
 * =========================================================================
 */

package net.sf.cache4j.test;

import net.sf.cache4j.CacheFactory;
import net.sf.cache4j.CacheConfig;
import net.sf.cache4j.Cache;
import net.sf.cache4j.impl.BlockingCache;
import net.sf.cache4j.impl.SynchronizedCache;
import net.sf.cache4j.impl.CacheConfigImpl;
import net.sf.cache4j.impl.SynchronizedCacheOpt;
import net.sf.cache4j.impl.SynchronizedCacheOpt2;
import net.sf.cache4j.test.BlockingCacheTest.TThread;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * ����� BlockingCacheTest ��������� SynchronizedCache
 *
 * @version $Revision: 1.1 $ $Date: 2010/06/18 17:01:16 $
 * @author Yuriy Stepovoy. <a href="mailto:stepovoy@gmail.com">stepovoy@gmail.com</a>
 **/

public class SynchronizedCacheTest implements Test {
// ----------------------------------------------------------------------------- ���������
// ----------------------------------------------------------------------------- �������� ������
// ----------------------------------------------------------------------------- ����������� ����������
// ----------------------------------------------------------------------------- ������������
// ----------------------------------------------------------------------------- Public ������

    //-------------------------------------------------------------------------- Test interface
    /**
     * ���������� ����� ������� ������������
     * @throws Exception
     */
    public void init() throws Exception {
    }

    public static void main(String[] args) throws Exception {
    	
    	Set testMap = new HashSet();
    	testMap.add("aa");
    	for(Object o: testMap)
    	{
    		System.out.println(o);
    		
    	}
    	
    	SynchronizedCache cache = new SynchronizedCache();// SynchronizedCacheOpt2
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, 0, 0, 1000, null, "lru", "strong");
        cache.setCacheConfig(cacheConfig);

        int tcount = Integer.parseInt(args[0]);
        int count = Integer.parseInt(args[1]);;
        
        TThread[] threads = new TThread[tcount];
        for (int i = 0; i <tcount; i++) {
        	threads[i] =  new TThread(cache, count);
        }
        
        long start = System.currentTimeMillis();
        for (int i = 0; i <tcount; i++) {
        	threads[i].start();
        }

        for (int i = 0; i <tcount; i++) {
        	threads[i].join();
        }
        
        long end = System.currentTimeMillis();
        System.out.println("duration: " + (end-start));
        
//        while(_threadCount!=0){
//            Thread.currentThread().yield();
//            Thread.currentThread().sleep(1);
//        }
        return ;
    }
    
    /**
     * ���������� ����� ���������� ������� ��������� ������
     * @throws Exception
     */
    public void afterEachMethod() throws Exception {
        //������� CacheFactory
        CacheFactory cf = CacheFactory.getInstance();
        Object[] cacheIds = cf.getCacheIds();
        for (int i = 0, indx = cacheIds==null ? 0 : cacheIds.length; i <indx; i++) {
            cf.removeCache(cacheIds[i]);
        }
    }

    /**
     * ���������� ����� ������������
     * @throws Exception
     */
    public void destroy() throws Exception {
    }
    //-------------------------------------------------------------------------- Test interface


    /**
     * �������� ������ � ������ ��� �� ����.
     */
    public static boolean test_PUT_GET() throws Exception {
        SynchronizedCache cache = new SynchronizedCache();
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, 0, 0, 0, null, "lru", "strong");
        cache.setCacheConfig(cacheConfig);

        Object key = "key";
        Object value = "value";
        cache.put(key, value);

        return cache.get(key)!=null ? true : false;
    }

    /**
     * �������� ������. �������� null. ��������� ������ ���� null.
     */
    public static boolean test_PUT_OBJ_PUT_NULL_GET() throws Exception {
        SynchronizedCache cache = new SynchronizedCache();
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, 0, 0, 0, null, "lru", "strong");
        cache.setCacheConfig(cacheConfig);

        Object key = "key";
        Object value = "value";

        cache.put(key, value);
        cache.put(key, null);

        return cache.get(key)==null ? true : false;
    }

    /**
     * �������� ������. ������� ������. ��������� ������ ���� null.
     */
    public static boolean test_PUT_REMOVE_GET() throws Exception {
        SynchronizedCache cache = new SynchronizedCache();
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, 0, 0, 0, null, "lru", "strong");
        cache.setCacheConfig(cacheConfig);

        Object key = "key";
        Object value = "value";

        cache.put(key, value);
        cache.remove(key);

        return cache.get(key)==null ? true : false;
    }

    /**
     * �������� ������. ������� ���. ��������� ������ ���� null.
     */
    public static boolean test_PUT_CLEAR_GET() throws Exception {
        SynchronizedCache cache = new SynchronizedCache();
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, 0, 0, 0, null, "lru", "strong");
        cache.setCacheConfig(cacheConfig);

        Object key = "key";
        Object value = "value";

        cache.put(key, value);
        cache.clear();

        return cache.get(key)==null ? true : false;
    }

    /**
     * ������������� ������������ ���������� ��������� � ����. �������� ������ ��� ����������.
     * ����������� ������ ���� ������������ ���������� ��������.
     */
    public static boolean test_MAXSIZE() throws Exception {
        SynchronizedCache cache = new SynchronizedCache();
        int maxSize = 100;
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, 0, 0, maxSize, null, "lru", "strong");
        cache.setCacheConfig(cacheConfig);

        for (int i = 0, maxi = maxSize*2; i <maxi; i++) {
            cache.put(new Long(i), new Long(i));
        }

        return cache.size()==maxSize ? true : false;
    }

    /**
     * ������������� ������������ ������ ������ ���������� ��������� ����.
     * �������� ������� ������� �������� ����� ��� ��������� ������������ ������.
     * � ���������� ������� � ���� ������ �������� ������ �� �����������
     * ������������.
     */
    public static boolean test_MAXMEMORYSIZE() throws Exception {
        SynchronizedCache cache = new SynchronizedCache();
        int msxMemorySize = 1000;
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, 0, msxMemorySize, 0, null, "lru", "strong");
        cache.setCacheConfig(cacheConfig);

        for (int i = 0; i <1000; i++) {
            cache.put(new Long(i), new Long(i));
        }

        return cache.getCacheInfo().getMemorySize()<=msxMemorySize ? true : false;
    }

    /**
     * ������������� ������������ ���������� � ������������ ���� �������� � ����.
     * �������� ������� �������� ����� ��� ��������� ������������ ���������� � �����.
     * � ���������� ���������� �������� � ����� �� ������ ��������� ������������
     * ��������.
     */
    public static boolean test_MAXSIZE_AND_MAXMEMORYSIZE() throws Exception {
        SynchronizedCache cache = new SynchronizedCache();
        int maxMemorySize = 1000;
        int maxSize = 1000;
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, 0, maxMemorySize, maxSize, null, "lru", "strong");
        cache.setCacheConfig(cacheConfig);

        for (int i = 0; i <maxSize*2; i++) {
            cache.put(new Long(i), new Long(i));
        }

        return cache.getCacheInfo().getMemorySize()<=maxMemorySize && cache.size()<=maxSize ? true : false;
    }

    /**
     * ������������� ������������ ����� ����� �������. �������� ������.
     * ������������� ����� �� ����� ����������� ������������ �����.
     * � ���������� ��� ������ ������� null.
     */
    public static boolean test_TTL() throws Exception {
        SynchronizedCache cache = new SynchronizedCache();
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 1, 0, 0, 0, null, "lru", "strong");
        cache.setCacheConfig(cacheConfig);

        Object key = "key";
        Object value = "value";

        cache.put(key, value);
        Thread.sleep(30);

        return cache.get(key)==null ? true : false;
    }

    /**
     * ������������� ������������ ����� ����������� �������. �������� ������.
     * ������������� ����� �� ����� ����������� ������������ �����.
     * � ���������� ��� ������ ������� null.
     */
    
    public static boolean test_IDLE() throws Exception {
        SynchronizedCache cache = new SynchronizedCache();
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, 1, 0, 0, null, "lru", "strong");
        cache.setCacheConfig(cacheConfig);

        Object key = "key";
        Object value = "value";

        cache.put(key, value);
        Thread.sleep(30);

        return cache.get(key)==null ? true : false;
    }

    /**
     * ��������� ��� �������� ������������� ������� �������� � ������� �����������
     * ����� �����. ��������� ������� � ������������ �������� ����� �,
     * ������������� �������� ������� X*2, �������� ������� ����� �� �*3.
     * � ���������� ��� ������ ���� �������� ������.
     */
    public static boolean test_CACHE_CLEANER_TTL() throws Exception {
        CacheFactory cf = CacheFactory.getInstance();
        long ttl = 100;
        long cleanInterval = ttl*2;
        long sleep = ttl*3;

        SynchronizedCache cache = new SynchronizedCache();
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, ttl, 0, 0, 0, null, "lru", "strong");
        cache.setCacheConfig(cacheConfig);

        for (int i = 0; i <1000; i++) {
            cache.put(new Long(i), new Long(i));
        }

        cf.addCache(cache);
        cf.setCleanInterval(cleanInterval);
        Thread.sleep(sleep);

        return cache.size()==0 ? true : false;
    }

    /**
     * ��������� ��� �������� ������������� ������� �������� � ������� �����������
     * ����� �����������. ��������� ������� � ������������ �������� ����������� �,
     * ������������� �������� ������� X*2, �������� ������� ����� �� �*3.
     * � ���������� ��� ������ ���� �������� ������.
     */
    public static boolean test_CACHE_CLEANER_IDLE() throws Exception {
        CacheFactory cf = CacheFactory.getInstance();
        long idle = 100;
        long cleanInterval = idle*2;
        long sleep = idle*3;

        SynchronizedCache cache = new SynchronizedCache();
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, idle, 0, 0, null, "lru", "strong");
        cache.setCacheConfig(cacheConfig);

        for (int i = 0; i <1000; i++) {
            cache.put(new Long(i), new Long(i));
        }

        cf.addCache(cache);
        cf.setCleanInterval(cleanInterval);
        Thread.sleep(sleep);

        return cache.size()==0 ? true : false;
    }

    /**
     * ��������� �������� ���������� LRU. ��� ������������ ���� ������ ���������
     * ������ � ���������� �������� �������������.
     */
    public static boolean test_EVICTION_ALGORITHM_LRU() throws Exception {
        SynchronizedCache cache = new SynchronizedCache();
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, 0, 0, 2, null, "lru", "strong");
        cache.setCacheConfig(cacheConfig);

        Object o1 = "o1";
        Object o2 = "o2";
        Object o3 = "o3";

        cache.put(o1, o1);
        Thread.sleep(50);
        cache.put(o2, o2);
        Thread.sleep(50);
        cache.get(o1); //update access time

        cache.put(o3, o3);

        //������ ������ o1 access time : 12
        //������ ������ is null access time:2
        //������ ������ o3 time:13
        return cache.get(o1)!=null &&
               cache.get(o3)!=null &&
               cache.get(o2)==null ? true : false;
    }

    /**
     * ��������� �������� ���������� LFU. ��� ������������ ���� ������ ���������
     * ������ ������� ������������� ���������� ���������� ���.
     */
    public static boolean test_EVICTION_ALGORITHM_LFU() throws Exception {
        SynchronizedCache cache = new SynchronizedCache();
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, 0, 0, 2, null, "lfu", "strong");
        cache.setCacheConfig(cacheConfig);

        Object o1 = "o1";
        Object o2 = "o2";
        Object o3 = "o3";

        cache.put(o1, o1);
        cache.get(o1); //update frequency count
        Thread.sleep(50);
        cache.put(o2, o2);

        cache.put(o3, o3);

        //������ ������ o1 frequency count:2
        //������ ������ is null frequency count:1
        //������ ������ o3 frequency count:1
        return cache.get(o1)!=null &&
               cache.get(o3)!=null &&
               cache.get(o2)==null ? true : false;
    }

    /**
     * ��������� �������� ���������� FIFO. ��� ������������ ���� ������ ���������
     * ������ � ���������� �������� ��������.
     */
    public static boolean test_EVICTION_ALGORITHM_FIFO() throws Exception {
        SynchronizedCache cache = new SynchronizedCache();
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, 0, 0, 2, null, "fifo", "strong");
        cache.setCacheConfig(cacheConfig);

        Object o1 = "o1";
        Object o2 = "o2";
        Object o3 = "o3";

        cache.put(o1, o1); //create time 1
        Thread.sleep(50);
        cache.put(o2, o2); //create time 2
        Thread.sleep(50);
        cache.put(o3, o3); //create time 3

        return cache.get(o2)!=null &&
               cache.get(o3)!=null &&
               cache.get(o1)==null ? true : false;
    }

    /**
     * ��������� ��� ����� � �������� strong. ��� ��������� �������� ����������
     * �������� ������ �������� ���������� OutOfMemoryError.
     */
    public static boolean test_REFERENCE_STRONG() throws Exception {
        SynchronizedCache cache = new SynchronizedCache();
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, 0, 0, 0, null, "lru", "strong");
        cache.setCacheConfig(cacheConfig);

        int i = 0;
        try {
            for (; i <10; i++) {
                cache.put(new Integer(i), new Long[2048*2048]);
            }
        } catch (OutOfMemoryError o){
            //int size = cache.size();
            cache.clear();
            return true; //return size==i;
        }

        return false;
    }

    /**
     * ��������� ��� ����� � �������� soft. ��� ��������� �������� ����������
     * �������� ���������� OutOfMemoryError �� ������ ��������.
     */
    public static boolean test_REFERENCE_SOFT() throws Exception {
        SynchronizedCache cache = new SynchronizedCache();
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, 0, 0, 0, null, "lru", "soft");
        cache.setCacheConfig(cacheConfig);

        int i = 0;
        try {
            for (; i <10; i++) {
                cache.put(new Integer(i), new Long[2048*2048]);
            }
        } catch (OutOfMemoryError o){
            cache.clear();
            return false;
        }

        return cache.size()==i;
    }

    /**
     * ��������� ������ ���� � ��������
     */
    /*
    public static boolean test_THREAD1() throws Exception {
        SynchronizedCache cache = new SynchronizedCache();
        CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, 0, 0, 1000, null, "lru", "strong");
        cache.setCacheConfig(cacheConfig);

        int tcount = 10;
        int count = 10000;
        for (int i = 0; i <tcount; i++) {
            new TThread(cache, count).start();
        }

        while(_threadCount!=0){
            Thread.currentThread().yield();
            Thread.currentThread().sleep(1);
        }
        return true;
    }
*/
// ----------------------------------------------------------------------------- Package scope ������
// ----------------------------------------------------------------------------- Protected ������
// ----------------------------------------------------------------------------- Private ������
// ----------------------------------------------------------------------------- Inner ������

    private static int _threadCount;
    private static long _id;
    synchronized static void incThreadCount(){
        _threadCount++;
    }
    synchronized static void decThreadCount(){
        _threadCount--;
    }
    synchronized static long nextId(){
        return _id++;
    }
    static class TThread extends Thread {
        private Cache _cache;
        private long _count;
        private Random _rnd = new Random(nextId());
        public TThread(Cache cache, long count) {
            incThreadCount();
            _cache = cache;
            _count = count;
        }

        public void run() {
            long count = 0;
            try {
                while(count<_count){
                    count++;
                    Object key = new Long(_rnd.nextInt(1500000));
                    if(this.getId() %5!=0 )
                    _cache.get(key);
                    else {
                    	 _cache.put(key, key);
					}

                    
                  
                   
                    //_cache.remove(key);
                }
            } catch (Exception e){
            	e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            } finally {
                decThreadCount();
            }
        }
    }

}

/*
$Log: SynchronizedCacheTest.java,v $
Revision 1.1  2010/06/18 17:01:16  smhuang
*** empty log message ***

*/
