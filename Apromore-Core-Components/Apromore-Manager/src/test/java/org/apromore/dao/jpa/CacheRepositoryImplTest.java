//package org.apromore.dao.jpa;
//
//import junit.framework.Assert;
//import org.apromore.cache.ehcache.BasicXML;
//import org.ehcache.CacheManager;
//import org.ehcache.config.Configuration;
//import org.ehcache.core.Ehcache;
//import org.apromore.dao.CacheRepository;
//import org.ehcache.xml.XmlConfiguration;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.springframework.cache.ehcache.EhCacheCacheManager;
//
//import static org.ehcache.config.builders.CacheManagerBuilder.newCacheManager;
//
//public class CacheRepositoryImplTest {
//
//    Configuration xmlConfig = new XmlConfiguration(BasicXML.class.getResource("/ehcache.xml"));
//    CacheManager cacheManager = newCacheManager(xmlConfig);
//
//    private static EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
////    private String cacheName = "test";
////    private org.ehcache.CacheManager cacheManager = CacheManager.getInstance();
//
//    @Before
//    public void setUp() throws Exception {
//        ehCacheCacheManager.setCacheManager(cacheManager);
//
//    }
//
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void getEhCacheCacheManager() {
//    }
//
//    @Test
//    public void setEhCacheCacheManager() {
//    }
//
//    @Test
//    public void getCacheName() {
//    }
//
//    @Test
//    public void setCacheName() {
//    }
//
//    @Test
//    public void get() {
//
//        CacheRepository cacheRepository = new CacheRepositoryImpl();
//        ((CacheRepositoryImpl) cacheRepository).setCacheName(cacheName);
//        ((CacheRepositoryImpl) cacheRepository).setEhCacheCacheManager(ehCacheCacheManager);
//
//        String keyPut = "keyGet1";
//        String valPut = "valGet1";
//        cacheRepository.put(keyPut, valPut);
//        Assert.assertTrue("Same value should be retrieved ", valPut.equals(cacheRepository.get(keyPut)));
//    }
//
//    @Test
//    public void put() {
//
//        CacheRepository cacheRepository = new CacheRepositoryImpl();
//        ((CacheRepositoryImpl) cacheRepository).setCacheName(cacheName);
//        ((CacheRepositoryImpl) cacheRepository).setEhCacheCacheManager(ehCacheCacheManager);
//
//        String keyPut = "keyPut1";
//        String valPut = "valPut1";
//        cacheRepository.put(keyPut, valPut);
//        Assert.assertTrue("Same value should be retrieved ", valPut.equals(cacheRepository.get(keyPut)));
//    }
//
//    @Test
//    public void evict() {
//
//        CacheRepository cacheRepository = new CacheRepositoryImpl();
//        ((CacheRepositoryImpl) cacheRepository).setCacheName(cacheName);
//        ((CacheRepositoryImpl) cacheRepository).setEhCacheCacheManager(ehCacheCacheManager);
//
//        String keyRemove = "keyRemove1";
//        String valRemove = "valRemove1";
//        cacheRepository.put(keyRemove, valRemove);
//        long size = cacheRepository.getMemoryStoreSize();
//        Assert.assertTrue("Same value should be retrieved ", valRemove.equals(cacheRepository.get(keyRemove)));
//        System.out.println("1 ->" + cacheRepository.getMemoryStoreSize());
//
//        cacheRepository.evict(keyRemove);
//        System.out.println("2 ->" + cacheRepository.getMemoryStoreSize());
//        Assert.assertEquals("size should reduce by 1", size - 1, cacheRepository.getMemoryStoreSize());
//        cacheRepository.evict(keyRemove);
//        Assert.assertEquals("Non existing Key removal, size should be the same as last time", size - 1,
//                cacheRepository.getMemoryStoreSize());
//        System.out.println("3 ->" + cacheRepository.getMemoryStoreSize());
//    }
//
//    @Test
//    public void getNativeCache() {
//    }
//
//    @Test
//    public void getMemoryUsage() {
//    }
//
//    @Test
//    public void getMemoryStoreSize() {
//    }
//
//    @Test
//    @Ignore("For pressure testing only")
//    public void testSizing() {
//
//        CacheRepository cacheRepository = new CacheRepositoryImpl();
//        ((CacheRepositoryImpl) cacheRepository).setCacheName(cacheName);
//        ((CacheRepositoryImpl) cacheRepository).setEhCacheCacheManager(ehCacheCacheManager);
//
//        for (int i = 0; i < 3000; i++) {
//            if ((i % 100) == 0) {
//                System.out.println("heatbeat " + i);
//                stats((org.ehcache.Ehcache) cacheRepository.getNativeCache());
//            }
//            cacheRepository.put(i, new byte[1024 * 1024]);
//        }
//        stats((org.ehcache.Ehcache) cacheRepository.getNativeCache());
//        Assert.assertTrue(true);
//    }
//
//    private void stats(Ehcache ehcache) {
//        System.out.println("OnHeapSize=" + ehcache.calculateInMemorySize() / 1024 / 1024 + "MB, OnHeapElements="
//                + ehcache.getMemoryStoreSize());
//        System.out.println("OffHeapSize=" + ehcache.calculateOffHeapSize() / 1024 / 1024 + "MB, OffHeapElements="
//                + ehcache.getOffHeapStoreSize());
//        System.out.println("DiskStoreSize=" + ehcache.calculateOnDiskSize() / 1024 / 1024 + "MB, DiskStoreElements="
//                + ehcache.getDiskStoreSize());
//    }
//}