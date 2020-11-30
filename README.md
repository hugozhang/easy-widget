# easy-widget
整理好用的组件：
分布式缓存、分布式锁、读写excel、http
## 分布式缓存
spring cache不支持不同key不同的过期时间，所以才有这个，也是基于redis实现
*  注解式
```java
@GetMapping("/cache2")
@MyCacheable(group = "cn.hsa.mds",key = "#p0",expire = 10,timeUnit = TimeUnit.MINUTES)
public String testCache2(String test) {
    System.out.print("cache put:" + test );
    return test;
}
```

* API式
```java
@GetMapping("/cache")
public Object cache() {
    Hash hash = cacheService.hash("cn.hsa.mds");
    hash.put("hello", "world",3,TimeUnit.HOURS);
    Object ret = hash.get("hello");
    return ret;
}
```

## 分布式锁
基于redis实现(命令set key value nx ex 200)，同时实现JDK Lock接口，也实现key自动续期，通过时间轮+看门狗
*  注解式
```java
@GetMapping("/lock")
@DLock(key = "#p0")
public void lock(String test) throws InterruptedException {
    TimeUnit.MINUTES.sleep(1);
}
```

* API式
```java
@GetMapping("/lock")
public void lock(String test) {
    Lock lock = lockContext.getLock(test);
    try {
        if (lock.tryLock()) {
            //拿到锁，业务处理
        } else {
            //拿不到锁，提示
        }
    } finally {
        //释放锁
        lock.unlock();
    }
}
```