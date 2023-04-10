# easy-widget
整理好用的组件：
分布式缓存、分布式锁、读写excel、http

按需要引入对应的组件即可
环境要求spring boot(用到了auto config) + jdk8 + spring data redis

## 分布式缓存
spring cache不支持不同key不同的过期时间，所以才有这个，也是基于redis实现，支持Spring EL表达式
###  注解式
```java
@GetMapping("/cache2")
@MyCacheable(group = "cn.hsa.mds",key = "#p0",expire = 10,timeUnit = TimeUnit.MINUTES)
public String testCache2(String test) {
    System.out.print("cache put:" + test );
    return test;
}
```

### API式
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
基于redis实现(命令set key value nx ex 200)，同时实现JDK Lock接口，也实现key自动续期，通过时间轮+看门狗，支持Spring EL表达式
###  注解式
```java
@GetMapping("/lock")
@DLock(key = "#p0")
public void lock(String test) throws InterruptedException {
    TimeUnit.MINUTES.sleep(1);
}
```

###  API式
```java
@GetMapping("/lock")
public void lock(String test) {
    Lock lock = lockContext.getLock(test);
    if (!lock.tryLock()) {
        //拿不到锁，提示
    }
    try {
        //拿到锁，业务处理
        
    } finally {
        //释放锁(注意：拿到锁才有释放动作)
        lock.unlock();
    }
}
```

## 分布式ID生成
基于redis lua实现(与redis所在的机子的时间（与应用所在机子无关）有关，雪花算法原理)
```java
@Resource
private IdGenerator idGenerator;

@GetMapping("/id")
public Object id() {
    return idGenerator.nextId("order");
}
```

## excel读写，只支持xlsx文件格式
### 读
```java
@Test
public void reader() throws Exception {
    List<User> rows = XLSXReader.build().skipRow(1).open(new FileInputStream("中文.xlsx")).sheetsParser(User.class);
    StringBuilder buffer = new StringBuilder();
    for (User user : rows) {
        buffer.append(user.getAddress());
    }
    System.out.println(buffer);
}
```
### 写
```java
@Test
    public void writer() throws Exception {
        List<User> list = new ArrayList();

        for (int i = 0; i < 1000; i++) {
            User u = new User();
            u.setAge(i);
            u.setUsername("A" + i);
            u.setCompany("B"+i);
            u.setAddress("C" + i);
            u.setBirthday(new Date());
            if (i == 1) {
                 u.setSalary(new BigDecimal(10000000034.12345+""));
            } else if (i == 2) {
                u.setSalary(new BigDecimal(100056.8967+""));
            } else if (i == 3) {
                u.setSalary(new BigDecimal(-100000005464.12345+""));
            } else {
                u.setSalary(new BigDecimal(-1000464.12345+""));
            }
            list.add(u);
        }
        Date s = new Date();
        System.out.println(s);
        FileOutputStream out = new FileOutputStream("中文.xlsx");
        XLSXWriter.build().toOutputStream(list, out);
        Date e = new Date();
        System.out.println(e);
        System.out.println("耗时:" + (e.getTime() - s.getTime()) / 1000);
        out.close();
    }
```
### 合并单元格  多行表头 + 相同数据行的合并
```java
@Data
public class User {

    @ExcelColumn(name = "年龄",groupName = "XX")
    private int age;

    @ExcelColumn(name = "姓名",groupName = "YY",cellMerge = true)
    private String username;

    @ExcelColumn(name = "公司",groupName = "XX")
    private String company;

    @ExcelColumn(name = "地址",groupName = "YY")
    private String address;

    @ExcelColumn(name = "生日")
    private Date birthday;

    @ExcelColumn(name = "薪水", cellFormat = @ExcelCellFormat(payload = "元"))
    private BigDecimal salary;
}
```
