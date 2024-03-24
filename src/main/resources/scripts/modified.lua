local productHash = KEYS[1]
local productId = KEYS[2]
local productField = redis.call('HGET', productHash, productId)

if productField then
    local product = cjson.decode(productField)
    local qty = tonumber(product.qty)
    if qty <= 0 then
        return -1
    end
    product.qty = qty - 1
    local updatedProductField = cjson.encode(product)
    redis.call('HSET', productHash, productId, updatedProductField)
    return qty - 1
else
    return -1
end