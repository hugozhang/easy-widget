###### 参考项目
######https://gitee.com/LimboHome/redis-lock
######https://github.com/finefuture/RedisLock-with-WatchDog
###### @Import的使用和原理
#####https://www.cnblogs.com/kevin-yuan/p/13583269.html
###### Spring EL表达式应用参与了Spring Cache的实现方式
https://www.cnblogs.com/zhuxudong/p/10322609.html

```
public interface Lock {
   　　// 用来获取锁，如果锁已经被其他线程获取，则一直等待，直到获取到锁
      void lock();
   　　// 该方法获取锁时，可以响应中断，比如现在有两个线程，一个已经获取到了锁，另一个线程调用这个方法正在等待锁，
      // 但是此刻又不想让这个线程一直在这死等，可以通过调用线程的Thread.interrupted()方法，来中断线程的等待过程
   　　void lockInterruptibly() throws InterruptedException;
   　　// tryLock方法会返回bool值，该方法会尝试着获取锁，如果获取到锁，就返回true，如果没有获取到锁，就返回false，
      // 但是该方法会立刻返回，而不会一直等待
      boolean tryLock();
   　　// 这个方法和上面的tryLock差不多是一样的，只是会尝试指定的时间，如果在指定的时间内拿到了锁，则会返回true，
      // 如果在指定的时间内没有拿到锁，则会返回false
      boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
   　　// 释放锁
      void unlock();
   　　// 实现线程通信，相当于wait和notify
      Condition newCondition();
   }
```