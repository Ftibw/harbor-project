package com.whxm.harbor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.whxm.harbor.mapper")
//@RestController
public class HarborWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(HarborWebApplication.class, args);
    }

    /*@Override   extends SpringBootServletInitializer
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(HarborWebApplication.class);
    }*/

/*    @Autowired
    private RedisDistributedLock lock;

    @GetMapping("/info")
    public String tryAcquire() {
        return String.valueOf(lock.tryAcquire("1", "2", 100000));
    }

    @GetMapping("/info/")
    public String tryRelease() {
        return String.valueOf(lock.tryRelease("1", "2"));
    }*/
}
