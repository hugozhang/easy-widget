package me.about.widget.cache.stats;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicLong;

public class CacheStatistics {
    private final AtomicLong cacheSize = new AtomicLong(); // 缓存当前大小
    private final AtomicLong totalRequests = new AtomicLong(); // 总请求数
    private final AtomicLong hitCount = new AtomicLong(); // 总命中数
    private final AtomicLong evictionCount = new AtomicLong(); // 总逐出数

    public void requestMade() {
        totalRequests.incrementAndGet();
    }

    public void cacheSizeIncrease() {
        cacheSize.incrementAndGet();
    }

    public void cacheSizeDecrease() {
        long l = cacheSize.get();
        if (l == 0) return;
        cacheSize.decrementAndGet();
    }

    public void cacheHit() {
        hitCount.incrementAndGet();
    }

    public void cacheEviction() {
        evictionCount.incrementAndGet();
    }

    public double getHitRate() {
        long requests = totalRequests.get();
        return requests == 0 ? 0.0 : (double) hitCount.get() / requests * 100;
    }

    public double getEvictionRate() {
        long size = cacheSize.get();
        return size == 0 ? 0.0 : (double) evictionCount.get() / size * 100;
    }

    public String displayStatistics() {
        DecimalFormat df = new DecimalFormat("#.##");
        return String.format(
                "Cache Statistics:\n" +
                        "  Total Requests: %d\n" +
                        "  Total Hits: %d\n" +
                        "  Total Evictions: %d\n" +
                        "  Current Cache Size: %d\n" +
                        "  Hit Rate: %s%%\n" +
                        "  Eviction Rate: %s%%",
                totalRequests.get(),
                hitCount.get(),
                evictionCount.get(),
                cacheSize.get(),
                df.format(getHitRate()),
                df.format(getEvictionRate())
        );
    }

    public static void main(String[] args) {
        CacheStatistics stats = new CacheStatistics();

        // 模拟一些操作
        stats.requestMade();
        stats.cacheHit();
        stats.requestMade();
        stats.cacheHit();
        stats.requestMade();
        stats.requestMade();
        stats.cacheEviction();

        // 显示统计信息
        System.out.println(stats.displayStatistics());
    }
}