if redis.call('exists', KEYS[1]) == 1 then
    local qty = tonumber(redis.call('get', KEYS[1]))
    if qty <= 0 then
        return -1
    end
    redis.call('decr', KEYS[1])
    return qty - 1
end
return -1