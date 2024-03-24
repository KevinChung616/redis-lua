package com.mike.redislua.repository;

import com.mike.redislua.domain.Product;
import com.mike.redislua.exception.SoldOutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Repository
@Slf4j
public class ProductRepository {

    private RedisTemplate<String, Object> template;

    private static final String LUA_SCRIPT = "local productHash = KEYS[1]\n" +
            "local productId = KEYS[2]\n" +
            "local productField = redis.call('hget', productHash, productId)\n" +
            "\n" +
            "if productField then\n" +
            "    local product = cjson.decode(productField)\n" +
            "    local qty = tonumber(product.qty)\n" +
            "    if qty <= 0 then\n" +
            "        return -1\n" +
            "    end\n" +
            "    product.qty = qty - 1\n" +
            "    local updatedProductField = cjson.encode(product)\n" +
            "    redis.call('hset', productHash, productId, updatedProductField)\n" +
            "    return qty - 1\n" +
            "else\n" +
            "    return -1\n" +
            "end";

    @Autowired
    public ProductRepository(RedisTemplate<String, Object> template) {
        this.template = template;
    }

    private static final String PRODUCT_REDIS_HASH = "Product";

    public Product save(Product product) {
        template.opsForHash().put(PRODUCT_REDIS_HASH, String.valueOf(product.getId()), product);
        return product;
    }

    public List<Product> findAll() {
        return template.opsForHash().values(PRODUCT_REDIS_HASH).stream().map(obj -> (Product)obj).toList();
    }

    public Product findProductById(int id) {
        return (Product) template.opsForHash().get(PRODUCT_REDIS_HASH, String.valueOf(id));
    }

    public String deleteProduct(int id) {
        template.opsForHash().delete(PRODUCT_REDIS_HASH, String.valueOf(id));
        return "Product Removed";
    }

    public Product decrementQtyById(int id) {
        Product product = (Product) template.opsForHash().get(PRODUCT_REDIS_HASH, String.valueOf(id));
        int qty = product.getQty();
        log.info("product {}  qty before decrement {}", product.getName(), qty);
        product.setQty(qty - 1);
        log.info("product {}  qty after decrement {}", product.getName(), qty - 1);
        template.opsForHash().put(PRODUCT_REDIS_HASH, String.valueOf(id), product);
        return product;
    }

    public Product decrementQtyByIdLua(int id) throws SoldOutException {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_SCRIPT,Long.class);
        long result = template.execute(redisScript, Arrays.asList(PRODUCT_REDIS_HASH, String.valueOf(id)), Collections.emptyList());
        if (result < 0) {
            log.error("Sorry sold out");
            throw new SoldOutException("Already sold out");
        }

        log.info("Congrats! you got a great deal!");
        return (Product) template.opsForHash().get(PRODUCT_REDIS_HASH, String.valueOf(id));
    }
}
